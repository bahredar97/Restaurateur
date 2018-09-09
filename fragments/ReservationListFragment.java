package com.panaceasoft.restaurateur.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.ReservationAdapter;
import com.panaceasoft.restaurateur.models.PReservation;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 26/9/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ReservationListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ReservationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jsonStatusSuccessString;
    private String connectionError;
    private TextView display_message;

    private List<PReservation> pReservationList;
    private List<PReservation> pReservationDataSet;
    private SharedPreferences pref;
    private FrameLayout mainLayout;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public ReservationListFragment() {

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservation_list, container, false);

        initUI(view);

        initData();

        return view;
    }

    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;
            adapter = null;
            pReservationDataSet = null;
            Utils.unbindDrawables(mainLayout);
            super.onDestroy();

        } catch (Exception e) {
            super.onDestroy();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initUI(View view) {

        initSwipeRefreshLayout(view);
        mainLayout = view.findViewById(R.id.main_frame_layout);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        display_message = view.findViewById(R.id.display_message);

        pReservationDataSet = new ArrayList<>();
        adapter = new ReservationAdapter(getActivity(), pReservationDataSet);
        mRecyclerView.setAdapter(adapter);

        display_message.setVisibility(View.GONE);

//        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                onItemClicked(position);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        if (getContext() != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Utils.psLog(Config.APP_API_URL + Config.GET_RESERVATION + pref.getInt("_login_user_id", 0));
                    requestData(Config.APP_API_URL + Config.GET_RESERVATION + pref.getInt("_login_user_id", 0));
                }
            });
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initData() {

        if (getContext() != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            Utils.psLog(Config.APP_API_URL + Config.GET_RESERVATION + pref.getInt("_login_user_id", 0));
            requestData(Config.APP_API_URL + Config.GET_RESERVATION + pref.getInt("_login_user_id", 0));

            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            connectionError = getResources().getString(R.string.connection_error);

            startLoading();
        }

    }

    private void requestData(String uri) {
        JsonObjectRequest request = new JsonObjectRequest(uri,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Utils.psLog("Response : " + response);

                            String status = response.getString("status");

                            Utils.psLog("Status : " + status);
                            if (status.equals(jsonStatusSuccessString)) {

                                display_message.setVisibility(View.GONE);

                                Utils.psLog("Status is success!");


                                Utils.psLog("Init the Json.");
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PReservation>>() {
                                }.getType();

                                Utils.psLog("Convert JSON to Object Array List.");
                                pReservationList = gson.fromJson(response.getString("data"), listType);

                                Utils.psLog("Update List");
                                updateDisplay();

                                Utils.psLog("Update Global Data.");
                                //updateGlobalCityList();


                            } else {
                                display_message.setVisibility(View.VISIBLE);
                                stopLoading();
                                Utils.psLog("Error in loading Transaction List Data.");
                            }
                        } catch (JSONException e) {
                            display_message.setVisibility(View.VISIBLE);
                            Utils.psErrorLogE("Error in loading Transaction List Data.", e);
                            stopLoading();
                            e.printStackTrace();
                        } catch (Exception ee) {
                            display_message.setVisibility(View.VISIBLE);
                            Utils.psErrorLogE("Error in loading Transaction List Data.", ee);
                            stopLoading();
                            ee.printStackTrace();
                        }
                    }
                },


                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        try {
                            stopLoading();

                            display_message.setVisibility(View.VISIBLE);
                            display_message.setText(connectionError);
                        } catch (Exception e) {
                            Utils.psErrorLog("onErrorResponse", e);
                        }


                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void updateDisplay() {

        if (pReservationList != null) {
            pReservationDataSet.clear();

            adapter.notifyDataSetChanged();

            Utils.psLog("Shop Count : " + pReservationList.size());

            adapter.notifyItemInserted(pReservationList.size());
            pReservationDataSet.addAll(pReservationList);

//            for (PReservation cd : pReservationList) {
//                pReservationDataSet.add(cd);
//            }

            adapter.notifyItemInserted(pReservationDataSet.size());
        }
        stopLoading();

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
}
