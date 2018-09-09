package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.ItemAdapter;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.listeners.RecyclerTouchListener;
import com.panaceasoft.restaurateur.models.PCategoryData;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.models.PSubCategoryData;
import com.panaceasoft.restaurateur.uis.AlertDialogRadio;
import com.panaceasoft.restaurateur.utilities.CacheRequest;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class SubCategoryActivity extends AppCompatActivity implements AlertDialogRadio.AlertPositiveListener {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar;
    private int selectedCategoryIndex = 0;
    private int selectedSubCategoryIndex = 0;
    private String selectedSubCategoryName = "";
    private int selectedShopID;
    private ArrayList<PSubCategoryData> subCategoriesList;
    private MenuItem menuItem;
    private int basketCount = 0;
    //private Adapter adapter;
    DBHandler db = new DBHandler(this);
    private int listSize = 0;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private List<PItemData> it;
    private List<PItemData> pItemDataList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jsonStatusSuccess ;
    private Picasso p;
    private int currentSize= 0;
    private int position = 0;
    private String sortField = "id";
    private String sortType = "asc";
    private String fromWhere = "normal";

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        p = new Picasso.Builder(this).build();

        initRecyclerView();

        initData();

        initUI();

    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        in.putExtra("close_activity", "NO");
        setResult(RESULT_OK, in);
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);

        for(PSubCategoryData cd : subCategoriesList) {
            if(cd != null) {
                menu.add(0, cd.id, 0, cd.name);

            }
        }

        menuItem = menu.findItem(R.id.action_filter);


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (pref.getInt("_login_user_id", 0) != 0) {
            basketCount = db.getBasketCountByShopId(selectedShopID);

            if (basketCount > 0) {
                menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
            }
        }else {
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Utils.psLog("Menu Title : " + item.getTitle());

        int id = item.getItemId();

        if(id == R.id.action_filter) {
            final  Intent intent;
            intent = new Intent(getApplicationContext(), BasketActivity.class);
            intent.putExtra("selected_shop_id", selectedShopID);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

            return true;

        } else if(id == R.id.action_sorting) {
            android.app.FragmentManager manager = getFragmentManager();
            AlertDialogRadio alert = new AlertDialogRadio();
            Bundle b  = new Bundle();
            b.putInt("position", position);
            alert.setArguments(b);
            alert.show(manager, "alert_dialog_radio");
            return true;
        } else {
            loadCategoryUI(id, item.getTitle().toString());
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                mAdapter.updateItemLikeAndReviewCount(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
                mAdapter.notifyDataSetChanged();

                if(data.getStringExtra("close_activity").equals("YES")){
                    Intent in = new Intent();
                    in.putExtra("close_activity", "YES");
                    setResult(RESULT_OK, in);
                    finish();
                }

                basketCount = db.getBasketCountByShopId(selectedShopID);

                if (basketCount > 0) {
                    menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
                } else {
                    menuItem.setVisible(false);
                }

            }


        }

    }

    @Override
    protected void onDestroy() {

        try {
            mRecyclerView = null;
            mLayoutManager = null;
            mAdapter = null;
            pItemDataList = null;

            Utils.unbindDrawables(findViewById(R.id.drawer_layout));

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }

    }

    @Override
    public void onPositiveClick(int position) {
        this.position = position;
        Utils.psLog("Selected Index " + position);
        doSorting(position);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        try {
            initToolbar();
            initProgressWheel();
            initSwipeRefreshLayout();
            //initRecyclerView();
            initLoadMore();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
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


            if(selectedSubCategoryName == null) {
                toolbar.setTitle(Utils.getSpannableString(getApplicationContext(), subCategoriesList.get(selectedSubCategoryIndex).name));
            } else {
                toolbar.setTitle(Utils.getSpannableString(getApplicationContext(), selectedSubCategoryName));

            }

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initProgressWheel() {
        progressWheel = findViewById(R.id.progress_wheel);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                stopLoading();
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mRecyclerView.setLayoutManager(mLayoutManager);
        pItemDataList = new ArrayList<>();

        mAdapter = new ItemAdapter(this, pItemDataList, mRecyclerView, p);
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
    }

    private Boolean isAddedExtraLoadingCell = false;
    private void initLoadMore() {
        try {
            mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

                @Override
                public void onLoadMore() {
                    if(pItemDataList != null) {
                        //add progress item
                        int from = listSize; //pItemDataList.size();
                        Utils.psLog("Data is Loading : " + mAdapter.loading);
                        if(currentSize != from && !mAdapter.loading) {
                            currentSize = from;

                            Utils.psLog("Current Size : " + from);


                            pItemDataList.add(null);
                            isAddedExtraLoadingCell = true;
                            mAdapter.notifyItemInserted(pItemDataList.size() );

                            if(selectedSubCategoryIndex == 0) {
                                Log.d("1 API URL : ", Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoriesList.get(0).id + "/item/all/count/" + Config.PAGINATION + "/form/" + from + "/field/" + sortField + "/type/" + sortType);

                                if(fromWhere.equals("sorting")) {
                                    requestDataSorting(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoriesList.get(0).id + "/item/all/count/" + Config.PAGINATION + "/from/" + from + "/field/" + sortField + "/type/" + sortType);
                                } else {
                                    requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoriesList.get(0).id + "/item/all/count/" + Config.PAGINATION + "/from/" + from + "/field/" + sortField + "/type/" + sortType);
                                }

                            } else {
                                Log.d("2 API URL : ", Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + selectedSubCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/" + from + "/field/" + sortField + "/type/" + sortType);

                                if(fromWhere.equals("sorting")) {
                                    requestDataSorting(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + selectedSubCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/from/" + from + "/field/" + sortField + "/type/" + sortType);
                                } else {
                                    requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + selectedSubCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/from/" + from + "/field/" + sortField + "/type/" + sortType);
                                }
                            }
                        }
                    }
                }

            });

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

            startLoading();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initLoadMore.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        try {

            ArrayList<PCategoryData> categoriesList = GlobalData.shopdata.categories;
            selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);
            selectedSubCategoryIndex = getIntent().getIntExtra("selected_sub_category_index", 0);
            selectedSubCategoryName = getIntent().getStringExtra("selected_sub_category_name");
            int selectedSortIndex = getIntent().getIntExtra("sorting_index", 0);
            fromWhere = getIntent().getStringExtra("from_where");
            if(fromWhere == null) {
                fromWhere = "normal";
            }


            Utils.psLog("sorting_index >>>" + selectedSortIndex);

            selectedShopID = getIntent().getIntExtra("selected_shop_id", 0);
            subCategoriesList = categoriesList.get(selectedCategoryIndex).sub_categories;

            this.jsonStatusSuccess = getResources().getString(R.string.json_status_success);

            Utils.psLog("Sub Cat ID : " + subCategoriesList.get(0).id);
            Utils.psLog("Selected Sub Cat Name : " + selectedSubCategoryName);

            if(selectedSortIndex == 0) {
                sortField = "name";
                sortType  = "asc";
            } else if(selectedSortIndex == 1) {
                sortField = "name";
                sortType  = "desc";
            } else if(selectedSortIndex == 2) {
                sortField = "added";
                sortType  = "asc";
            } else if(selectedSortIndex == 3) {
                sortField = "added";
                sortType  = "desc";
            } else if(selectedSortIndex == 4) {
                sortField = "like_count";
                sortType  = "asc";
            } else if(selectedSortIndex == 5) {
                sortField = "like_count";
                sortType  = "desc";
            }
            Utils.psLog(">>>>>>> " + fromWhere);
            if(selectedSubCategoryIndex == 0) {
                Utils.psLog("init data : " + Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedShopID + "/sub_cat_id/" + subCategoriesList.get(0).id + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);
                if(fromWhere.equals("sorting")){
                    Utils.psLog("1");
                    requestDataSorting(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedShopID + "/sub_cat_id/" + subCategoriesList.get(0).id + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);

                } else {
                    Utils.psLog("2");
                    requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedShopID + "/sub_cat_id/" + subCategoriesList.get(0).id + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);
                }
            } else {
                if(fromWhere.equals("sorting")){
                    Utils.psLog("3");
                    requestDataSorting(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedShopID + "/sub_cat_id/" + selectedSubCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);
                } else {
                    Utils.psLog("4");
                    requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedShopID + "/sub_cat_id/" + selectedSubCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);
                }

                Utils.psLog(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedShopID + "/sub_cat_id/" + selectedSubCategoryIndex + "/item/all/count/" + Config.PAGINATION + "/form/0/field/" + sortField + "/type/" + sortType);
            }


        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void requestData(String uri) {
        Utils.psLog("API URL requestData : " + uri);
        mAdapter.loading = true;

        CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>(){
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String jsonString ;

                    jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    JSONObject jsonObject = new JSONObject(jsonString);

                    Utils.psLog(jsonString);

                    try {
                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccess)) {

                            if(pItemDataList != null) {


                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                it = gson.fromJson(jsonObject.getString("data"), listType);

                                if (pItemDataList.size() > 0 && isAddedExtraLoadingCell) {
                                    pItemDataList.remove(pItemDataList.size() - 1);
                                    mAdapter.notifyItemRemoved(pItemDataList.size());
                                    isAddedExtraLoadingCell = false;
                                }

                                if(it != null) {

                                    if(it.size() > 0) {
                                        for (PItemData pItem : it) {

                                            boolean isNew = true;
                                            for(PItemData existItem : pItemDataList) {
                                                if(existItem.id == pItem.id) {
                                                    isNew = false;
                                                    break;
                                                }
                                            }

                                            if(isNew) {
                                                pItemDataList.add(pItem);
                                            }

                                        }

                                        mAdapter.notifyItemInserted(pItemDataList.size());

                                        Utils.psLog("Got Data" + pItemDataList.size());
                                        listSize = pItemDataList.size();
                                    }

                                }
                                stopLoading();
                                progressWheel.setVisibility(View.GONE);
                                mAdapter.setLoaded();

                            }

                        } else {
                            if(pItemDataList != null) {
                                if (pItemDataList.size() > 0) {
                                    pItemDataList.remove(pItemDataList.size() - 1);
                                    mAdapter.notifyItemRemoved(pItemDataList.size());
                                }
                            }
                            stopLoading();
                            Utils.psLog("Error in loading Sub Categories.");
                        }
                    } catch (JSONException e) {
                        stopLoading();
                        e.printStackTrace();
                    } catch (Exception e){
                        Utils.psErrorLog("Error in loading.", e);
                    }

                } catch (UnsupportedEncodingException | JSONException e) {
                    stopLoading();
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Utils.psLog(error.getMessage());
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

    private void requestDataSorting(String uri) {

        Utils.psLog("API URL requestDataSorting : " + uri);

        mAdapter.loading = true;
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccess)) {

                                if(pItemDataList != null) {


                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PItemData>>() {
                                    }.getType();
                                    it = gson.fromJson(response.getString("data"), listType);

                                    if(it != null) {
                                        if (pItemDataList.size() > 0) {
                                            pItemDataList.remove(pItemDataList.size() - 1);
                                            mAdapter.notifyItemRemoved(pItemDataList.size());
                                        }


//                                        for (PItemData pItem : it) {
//                                            pItemDataList.add(pItem);
//                                        }

                                        pItemDataList.addAll(it);

                                        mAdapter.notifyItemInserted(pItemDataList.size());

                                        Utils.psLog("Got Data" + pItemDataList.size());
                                        listSize = pItemDataList.size();
                                    }
                                    stopLoading();
                                    progressWheel.setVisibility(View.GONE);
                                    mAdapter.setLoaded();

                                }

                            } else {
                                if(pItemDataList != null) {
                                    if (pItemDataList.size() > 0) {
                                        pItemDataList.remove(pItemDataList.size() - 1);
                                        mAdapter.notifyItemRemoved(pItemDataList.size());
                                    }
                                }
                                stopLoading();
                                Utils.psLog("Error in loading Sub Categories.");
                            }
                        } catch (JSONException e) {
                            stopLoading();
                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        try {
                            Utils.psLog(ex.getMessage());
                        }catch (Exception e) {
                            Utils.psErrorLog("onErrorResponse", e);
                        }

                    }
                });

        request.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }



    private void loadCategoryUI(int id, String title){
        toolbar.setTitle(Utils.getSpannableString(getApplicationContext(), title));
        Intent intent = new Intent(this,SubCategoryActivity.class);
        intent.putExtra("selected_category_index", selectedCategoryIndex);
        intent.putExtra("selected_sub_category_index", id);
        intent.putExtra("selected_sub_category_name", title);
        intent.putExtra("selected_shop_id", selectedShopID);
        intent.putExtra("from_where", "sorting");
        startActivity(intent);
        //adapter = null;
        pItemDataList.clear();
        this.finish();

    }

    private void startLoading() {
        try {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
            Utils.psErrorLog("startLoading", e);
        }
    }

    private void stopLoading() {
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            Utils.psErrorLog("stopLoading", e);
        }

    }

    private void doSorting(int id) {
        Utils.psLog("Selected Sub Cat ID in doSorting : " + selectedSubCategoryIndex);
        Intent intent = new Intent(this,SubCategoryActivity.class);
        intent.putExtra("selected_category_index", selectedCategoryIndex);
        intent.putExtra("selected_shop_id", selectedShopID);
        intent.putExtra("sorting_index", id);
        intent.putExtra("selected_sub_category_index", selectedSubCategoryIndex);
        intent.putExtra("selected_sub_category_name", selectedSubCategoryName);
        intent.putExtra("from_where", "sorting");
        startActivity(intent);
        pItemDataList.clear();
        this.finish();
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void openActivity(int selected_item_id){
        final Intent intent;
        intent = new Intent(this, DetailActivity.class);
        Utils.psLog("Selected Shop ID : " + selectedShopID);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_shop_id", selectedShopID);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public void onItemClicked(int position) {

        if(pItemDataList != null) {
            if(pItemDataList.size() >= position) {
                this.openActivity(pItemDataList.get(position).id);
            }
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------------------------------
     * Start Block - Static Class
     **------------------------------------------------------------------------------------------------*/
//    public class Adapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragments = new ArrayList<>();
//        private final List<String> mFragmentTitles = new ArrayList<>();
//        public Adapter(FragmentManager fm) {
//            super(fm);
//        }
//        public void addFragment(Fragment fragment, String title) {
//            mFragments.add(fragment);
//            mFragmentTitles.add(title);
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            if(mFragments != null) {
//                return mFragments.size();
//            }else{
//                return 0;
//            }
//        }
//
//        @Override
//        public SpannableString getPageTitle(int position) {
//            return Utils.getSpannableString(mFragmentTitles.get(position));
//        }
//
//    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Static Class
     **------------------------------------------------------------------------------------------------*/


}
