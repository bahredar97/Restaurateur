package com.panaceasoft.restaurateur.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.ReservationActivity;
import com.panaceasoft.restaurateur.activities.SelectedShopActivity;
import com.panaceasoft.restaurateur.adapters.ShopAdapter;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.listeners.RecyclerTouchListener;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.utilities.CacheRequest;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.panaceasoft.restaurateur.utilities.VolleySingleton;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class ShopsListFragment extends Fragment {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private RecyclerView mRecyclerView;
    private ProgressWheel progressWheel;
    private ShopAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView display_message;
    private ArrayList<PShopData> pShopDataList;
    private ArrayList<PShopData> pShopDataSet;
    private NestedScrollView singleLayout;
    private TextView scShopName;
    private TextView scShopLocation;
    private TextView scShopAbout;
    private TextView scShopCatCount;
    private TextView scShopSubCatCount;
    private TextView scShopItemCount;
    private ImageView scShopPhoto;
    private String jsonStatusSuccessString;
    private String connectionError;
    private int MAX_WIDTH;
    private Picasso p;
    private FrameLayout mainLayout;
    private EditText etKeyword;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public ShopsListFragment() {

    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        MAX_WIDTH = Utils.getScreenWidth(container.getContext());

        View view = inflater.inflate(R.layout.fragment_shops_list, container, false);

        // Inflate the layout for this fragment
        initUI(view);

        initData();

        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;

            progressWheel = null;
            swipeRefreshLayout = null;
            //p.shutdown();
            Utils.unbindDrawables(mainLayout);
            GlobalData.shopdata = null;
            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_goto_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            /*Utils.psLog("Open Map");
            Fragment fragment = new ShopsMapFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment);

            if(Utils.activity != null){

                ((MainActivity)Utils.activity).updateFABAction(MainActivity.FABActions.SHOPMAP);

            }


            //ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
            ft.commit();*/

            if (getActivity() != null) {
                ((MainActivity) getActivity()).openFragment(R.id.nav_home_map);
            }

        }

        return super.onOptionsItemSelected(item);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initUI(View view) {

        mainLayout = view.findViewById(R.id.shops_layout);
        p = new Picasso.Builder(view.getContext()).build();

        initSingleUI(view);

        initSwipeRefreshLayout(view);

        initProgressWheel(view);

        initRecyclerView(view);


    }

    private void initSingleUI(View view) {

        singleLayout = view.findViewById(R.id.single_shop_layout);
        scShopName = view.findViewById(R.id.sc_shop_name);
        scShopLocation = view.findViewById(R.id.sc_shop_loc);
        scShopAbout = view.findViewById(R.id.sc_shop_desc);
        scShopCatCount = view.findViewById(R.id.txt_cat_count);
        scShopSubCatCount = view.findViewById(R.id.txt_sub_cat_count);
        scShopItemCount = view.findViewById(R.id.txt_item_count);
        scShopPhoto = view.findViewById(R.id.sc_shop_photo);
        Button scShopExplore = view.findViewById(R.id.button_explore);
        Button scShopReservation = view.findViewById(R.id.button_reservation);

        int rlWidth = (MAX_WIDTH / 3) - 20;

        RelativeLayout r1 = view.findViewById(R.id.rl_count1);
        RelativeLayout r2 = view.findViewById(R.id.rl_count2);
        RelativeLayout r3 = view.findViewById(R.id.rl_count3);

        r1.setMinimumWidth(rlWidth);
        r2.setMinimumWidth(rlWidth);
        r3.setMinimumWidth(rlWidth);

        scShopPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (pShopDataList != null && pShopDataList.size() > 0) {
                    final Intent intent;
                    intent = new Intent(getActivity(), SelectedShopActivity.class);
                    GlobalData.shopdata = pShopDataList.get(0);
                    intent.putExtra("selected_shop_id", pShopDataList.get(0).id);

                    if (getActivity() != null) {
                        getActivity().startActivity(intent);

                        getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                    }

                }

            }
        });

        scShopExplore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (pShopDataList != null && pShopDataList.size() > 0) {
                    final Intent intent;
                    intent = new Intent(getActivity(), SelectedShopActivity.class);
                    GlobalData.shopdata = pShopDataList.get(0);
                    intent.putExtra("selected_shop_id", pShopDataList.get(0).id);

                    if (getActivity() != null) {
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                    }
                }

            }
        });

        scShopReservation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent intent;
                intent = new Intent(getActivity(), ReservationActivity.class);
                GlobalData.shopdata = pShopDataList.get(0);
                intent.putExtra("selected_shop_id", pShopDataList.get(0).id);
                intent.putExtra("selected_shop_name", pShopDataList.get(0).name);

                if (getActivity() != null) {
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                }

            }
        });

    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (pShopDataList != null) {
                    pShopDataList.clear();
                }

                requestData(Config.APP_API_URL + Config.GET_ALL);
                //loadData();

            }
        });
    }

    private void initProgressWheel(View view) {
        progressWheel = view.findViewById(R.id.progress_wheel);
    }

    private void initRecyclerView(View view) {

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        display_message = view.findViewById(R.id.display_message);
        display_message.setVisibility(View.GONE);

        pShopDataSet = new ArrayList<>();
        adapter = new ShopAdapter(getActivity(), pShopDataSet, p);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initData() {

        if (pShopDataList != null) {
            pShopDataList.clear();
        }

        loadData();

        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        connectionError = getResources().getString(R.string.connection_error);

    }

    private void loadData() {

        if (GlobalData.shopDatas.size() > 1) {


            Utils.psLog("Load Data From Global Data.");

            pShopDataList = GlobalData.shopDatas;

            if (pShopDataList != null) {
                Utils.psLog("Shop Count : " + pShopDataList.size());
                if (pShopDataList.size() == 1) {
                    mRecyclerView.setVisibility(View.GONE);
                    singleLayout.setVisibility(View.VISIBLE);
                    stopLoading();
                    updateSingleDisplay();
                } else {

                    singleLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    updateDisplay();
                }

                //updateGlobalShopList();
            }

        } else {
            //Utils.psLog("Global Data is nothing so need to call API.");
            //startLoading();
            requestData(Config.APP_API_URL + Config.GET_ALL);
        }

    }

    private void requestData(String uri) {
        try {
            CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);

                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            if (progressWheel != null) {
                                progressWheel.setVisibility(View.GONE);
                            }
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PShopData>>() {
                            }.getType();

                            pShopDataList = gson.fromJson(jsonObject.getString("data"), listType);

                            if (pShopDataList != null) {
                                Utils.psLog("Shop Count : " + pShopDataList.size());
                                if (pShopDataList.size() > 1) {
                                    singleLayout.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    updateDisplay();
                                } else {
                                    mRecyclerView.setVisibility(View.GONE);
                                    singleLayout.setVisibility(View.VISIBLE);
                                    stopLoading();
                                    updateSingleDisplay();
                                }

                                updateGlobalShopList();
                            }


                        } else {
                            stopLoading();
                            Utils.psLog("Error in loading ShopList.");
                        }


                    } catch (UnsupportedEncodingException | JSONException e) {
                        stopLoading();
                        e.printStackTrace();
                    } catch (Exception e) {
                        Utils.psErrorLog("Error in loading.", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    try {
                        if (progressWheel != null) {
                            progressWheel.setVisibility(View.GONE);
                        }

                        display_message.setVisibility(View.VISIBLE);
                        display_message.setText(connectionError);

                    } catch (Exception e) {
                        Utils.psErrorLog("onErrorResponse", e);
                    }
                }
            });


            cacheRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(cacheRequest);
        } catch (Exception e) {
            Utils.psErrorLog("Error in loading.", e);
        }

    }

    public void doSearch(final String URL) {
        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", etKeyword.getText().toString());

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PShopData>>() {
                                }.getType();

                                pShopDataList = gson.fromJson(response.getString("data"), listType);

                                if (pShopDataList != null) {
                                    Utils.psLog("Shop Count : " + pShopDataList.size());
                                    if (pShopDataList.size() >= 1) {
                                        pShopDataSet.clear();
                                        adapter.notifyDataSetChanged();
                                        singleLayout.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        updateDisplay();
                                    } else {
                                        Utils.psLog("Result Not Found");
                                        showResultNotFound();
                                    }

                                    updateGlobalShopList();
                                }

                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        } catch (Exception e) {
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(sr);

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void updateSingleDisplay() {
        try {
            if (pShopDataList != null) {
                if (pShopDataList.size() > 0) {

                    display_message.setVisibility(View.GONE);
                    singleLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
                    scShopName.setText(pShopDataList.get(0).name);
                    scShopLocation.setText(pShopDataList.get(0).address);
                    scShopAbout.setText(pShopDataList.get(0).description);
                    String shopCatStr = pShopDataList.get(0).category_count + " Categories";
                    scShopCatCount.setText(shopCatStr);
                    String shopSubCatStr = pShopDataList.get(0).sub_category_count + " Sub Categories";
                    scShopSubCatCount.setText(shopSubCatStr);
                    String shopItemStr = pShopDataList.get(0).item_count + " Foods";
                    scShopItemCount.setText(shopItemStr);

//                    p.load(Config.APP_IMAGES_URL + pShopDataList.get(0).cover_image_file)
//                            //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                            .resize(MAX_WIDTH, MAX_WIDTH)
//                            .onlyScaleDown()
//                            .into(scShopPhoto);

                    Utils.bindImage(getContext(), p, scShopPhoto, pShopDataList.get(0).cover_image_file, 1);


                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in single display data binding.", e);
        }
    }

    private void updateGlobalShopList() {
        GlobalData.shopDatas.clear();
        GlobalData.shopDatas.addAll(pShopDataList);
//        for (PShopData cd : pShopDataList) {
//            GlobalData.shopDatas.add(cd);
//        }
    }

    private void updateDisplay() {

        try {
            if (pShopDataSet != null) {
                if (swipeRefreshLayout.isRefreshing()) {
                    pShopDataSet.clear();
                    adapter.notifyDataSetChanged();

                    pShopDataSet.addAll(pShopDataList);
//                    for (PShopData cd : pShopDataList) {
//                        pShopDataSet.add(cd);
//                    }

                } else {
                    pShopDataSet.addAll(pShopDataList);
//                    for (PShopData cd : pShopDataList) {
//                        pShopDataSet.add(cd);
//                    }

                }

                adapter.notifyItemInserted(pShopDataSet.size());
            }

            stopLoading();

        } catch (Exception e) {
            Utils.psErrorLogE("Error in updateDisplay.", e);
        }


    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void stopLoading() {
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
                Utils.psLog(">>> stopLoading");

            }
        } catch (Exception e) {
            Utils.psErrorLog("Error in stopLoading.", e);
        }
    }

    public void showSearchPopup() {
        final ViewGroup container = null;

        Activity activity = getActivity();
        if (activity == null) {
            activity = Utils.activity;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(R.string.keyword_search_title);
        LayoutInflater inflater = (activity).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.keyword_input, container, false);
        dialogBuilder.setView(dialogView);

        Button btnSearch = dialogView.findViewById(R.id.button_search);
        btnSearch.setTextColor(getResources().getColor(R.color.whiteColor));
        btnSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        etKeyword = dialogView.findViewById(R.id.input_keyword);


        final AlertDialog alert = dialogBuilder.create();
        alert.show();
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alert.dismiss();

                if (!etKeyword.getText().toString().equals("")) {
                    Utils.psLog("Need To Call API " + etKeyword.getText().toString());
                    final String URL = Config.APP_API_URL + Config.SHOP_SEARCH_BY_KEYWORD;
                    Utils.psLog(URL);
                    doSearch(URL);

                } else {
                    Utils.psLog("Keyword is empty!");
                }

            }
        });

    }

    public void showResultNotFound() {
        if (getContext() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.result_no_found_message);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utils.psLog("OK clicked.");
                }
            });
            builder.show();
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


}
