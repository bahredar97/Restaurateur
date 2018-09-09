package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.ItemAdapter;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.listeners.RecyclerTouchListener;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private List<PItemData> myDataSet;
    private List<PItemData> it;
    private TextView txtRecordFound;
    private String jsonStatusSuccessString;
    private SpannableString searchResultString;
    private Picasso p =null;
    private CoordinatorLayout mainLayout;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

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
    protected void onDestroy() {
        try {

            toolbar = null;
            mLayoutManager = null;

            myDataSet.clear();

            mAdapter = null;
            myDataSet = null;

//            p.shutdown();
            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
            Utils.psErrorLog("Error in search on destroy. ", e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.updateItemLikeAndReviewCount(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
        mAdapter.notifyDataSetChanged();
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        initToolbar();
        initRecyclerView();
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
            toolbar.setTitle(searchResultString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init initToolbar.", e);
        }
    }

    public void initRecyclerView() {
        try {
            RecyclerView mRecyclerView = findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            myDataSet = new ArrayList<>();
            mAdapter = new ItemAdapter(this, myDataSet, mRecyclerView, p);
            mRecyclerView.setAdapter(mAdapter);


            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } catch (Exception e) {
            Utils.psErrorLogE("Error in init initRecyclerView.", e);
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
            p =new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            searchResultString = Utils.getSpannableString(getApplicationContext(), getString(R.string.search_result));
        }catch(Exception e){
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void bindData() {
        try {
            TextView txtSelectedCity = findViewById(R.id.search_city);
            txtSelectedCity.setText(getIntent().getStringExtra("selected_shop_name"));
            TextView txtSearchKeyword = findViewById(R.id.search_keyword);
            txtSearchKeyword.setText(getIntent().getStringExtra("search_keyword"));
            txtRecordFound = findViewById(R.id.record_found);

            final String URL = Config.APP_API_URL + Config.POST_ITEM_SEARCH + getIntent().getStringExtra("selected_shop_id");
            Utils.psLog(URL);
            doSearch( URL, getIntent().getStringExtra("search_keyword"));
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/
    public void doSearch( final String URL, final String keyword) {
        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", keyword);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if(myDataSet != null) {



                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PItemData>>() {
                                    }.getType();
                                    it =  gson.fromJson(response.getString("data"), listType);

                                    if(it != null) {
                                        if (myDataSet.size() > 0) {
                                            myDataSet.remove(myDataSet.size() - 1);
                                            mAdapter.notifyItemRemoved(myDataSet.size());
                                        }

                                        String tmpResultCount = "Search Result Count : " + it.size();
                                        txtRecordFound.setText(tmpResultCount);

                                        myDataSet.addAll(it);

//                                        for (PItemData pItem : it) {
//                                            myDataSet.add(pItem);
//                                        }

                                        mAdapter.notifyItemInserted(myDataSet.size());

                                    }
                                    mAdapter.setLoaded();
                                }else {
                                    mAdapter.setLoaded();
                                }


                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Utils.psLog(error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }


            }
        });
        sr.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(sr);

    }

    public void onItemClicked(int position) {
        final Intent intent;

        Utils.psLog("Selected Shop ID : " + myDataSet.get(position).shop_id);

        intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("selected_item_id", myDataSet.get(position).id);
        intent.putExtra("selected_shop_id", myDataSet.get(position).shop_id);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

}