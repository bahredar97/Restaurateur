package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.NewsAdapter;
import com.panaceasoft.restaurateur.models.PNewsData;
import com.panaceasoft.restaurateur.utilities.CacheRequest;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private int selectedShopId;
    private ListView listView;
    private ArrayList<PNewsData> newsDataSet;
    private ArrayList<PNewsData> news;
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jsonStatusSuccessString;
    private SpannableString newsListString;
    private CoordinatorLayout mainLayout;
    private Picasso p;
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        initData();

        initUI();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public void onRefresh() {
        Utils.psLog("On Refershing ....");
        requestData();
    }

    @Override
    public void onDestroy() {

        try {
            toolbar = null;
            p.shutdown();

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {
        initToolbar();
        initList();
        mainLayout = findViewById(R.id.coordinator_layout);
        if(Config.SHOW_ADMOB) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            AdView mAdView = findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initList() {
        try {
            listView = findViewById(R.id.news_list);
            listView.setOnItemClickListener(new ListClickHandler());
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initList.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            p = new Picasso.Builder(this).build();
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            newsListString = Utils.getSpannableString(getApplicationContext(), getString(R.string.news_list));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {

        toolbar.setTitle(newsListString);
        selectedShopId = Integer.parseInt(getIntent().getStringExtra("selected_shop_id"));
        newsDataSet = new ArrayList<>();
        adapter = new NewsAdapter(this, newsDataSet, p);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        requestData();
                                    }
                                }
        );
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


    private void requestData() {

        final String uri = Config.APP_API_URL + Config.GET_SHOP_NEWS + selectedShopId;
        Utils.psLog(uri);
        swipeRefreshLayout.setRefreshing(true);

        CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>(){
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);

                    try {
                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PNewsData>>() {
                            }.getType();
                            news = gson.fromJson(jsonObject.getString("data"), listType);

                            if(news != null) {
                                Utils.psLog("Total News : " + news.size());
                            }

                            if(newsDataSet != null) {
                                newsDataSet.clear();
                            }

                            adapter.notifyDataSetChanged();
                            newsDataSet.addAll(news);
//                            for (PNewsData nd : news) {
//                                newsDataSet.add(nd);
//                            }

                            swipeRefreshLayout.setRefreshing(false);

                        } else {

                            Utils.psLog("Error in loading.");
                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    } catch (Exception e){
                        Utils.psErrorLog("Error in loading.", e);
                    }

                } catch (UnsupportedEncodingException | JSONException e) {

                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                try {
                    Log.d(">> Volley Error ", ex.getMessage() + "");

                    swipeRefreshLayout.setRefreshing(false);
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });


        cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(cacheRequest);

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Listener Class
     **------------------------------------------------------------------------------------------------*/
    public class ListClickHandler implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {

            PNewsData newsData = (PNewsData) adapter.getItemAtPosition(position);
            Utils.psLog(" Title " + newsData.title);
            Bundle bundle = new Bundle();
            bundle.putParcelable("news", newsData);

            Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
            intent.putExtra("news_bundle", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Listener Class
     **------------------------------------------------------------------------------------------------*/





}
