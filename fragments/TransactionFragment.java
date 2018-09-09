package com.panaceasoft.restaurateur.fragments;

import android.content.Intent;
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
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.TransactionDetailActivity;
import com.panaceasoft.restaurateur.adapters.TransactionAdapter;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.listeners.RecyclerTouchListener;
import com.panaceasoft.restaurateur.models.PTransactionData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private TransactionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jsonStatusSuccessString;
    private String connectionError;
    private TextView display_message;
    private List<PTransactionData> pTransactionDataArrayList;
    private List<PTransactionData> pTransactionDataSet;
    private SharedPreferences pref;
    private FrameLayout mainLayout;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public TransactionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        initUI(view);

        initData();

        return view;
    }

    @Override
    public void onDestroy() {

        try {
            mRecyclerView = null;
            adapter = null;
            pTransactionDataSet = null;
            Utils.unbindDrawables(mainLayout);
            super.onDestroy();

        } catch (Exception e){
            super.onDestroy();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initUI(View view){

        initSwipeRefreshLayout(view);
        mainLayout = view.findViewById(R.id.main_frame_layout);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        display_message = view.findViewById(R.id.display_message);


        pTransactionDataSet = new ArrayList<>();
        adapter = new TransactionAdapter(getActivity(), pTransactionDataSet);
        mRecyclerView.setAdapter(adapter);

        display_message.setVisibility(View.GONE);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getContext() != null) {
                    pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Utils.psLog(Config.APP_API_URL + Config.GET_TRANSACTIONS + pref.getInt("_login_user_id", 0));
                    requestData(Config.APP_API_URL + Config.GET_TRANSACTIONS + pref.getInt("_login_user_id", 0));
                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Function
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initData(){
        if(getContext() != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            Utils.psLog(Config.APP_API_URL + Config.GET_TRANSACTIONS + pref.getInt("_login_user_id", 0));
            requestData(Config.APP_API_URL + Config.GET_TRANSACTIONS + pref.getInt("_login_user_id", 0));

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

                            Utils.psLog("Status : " + status );
                            if (status.equals(jsonStatusSuccessString)) {

                                display_message.setVisibility(View.GONE);

                                Utils.psLog("Status is success!");


                                Utils.psLog("Init the Json.");
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PTransactionData>>() {
                                }.getType();

                                Utils.psLog("Convert JSON to Object Array List.");
                                pTransactionDataArrayList = gson.fromJson(response.getString("data"), listType);

                                Utils.psLog("Update List");
                                updateDisplay();

                                Utils.psLog("Update Global Data.");
                                updateGlobalCityList();


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
                        } catch (Exception ee){
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
                        }catch (Exception e){
                            Utils.psErrorLogE("onErrorResponse", e);
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
    private void updateGlobalCityList() {
        GlobalData.transactionDatas.clear();
        GlobalData.transactionDatas.addAll(pTransactionDataArrayList);

//        for (PTransactionData cd : pTransactionDataArrayList) {
//            GlobalData.transactionDatas.add(cd);
//        }
    }

    private void updateDisplay() {

        if(pTransactionDataArrayList != null) {
            pTransactionDataSet.clear();

            adapter.notifyDataSetChanged();

            Utils.psLog("Shop Count : " + pTransactionDataArrayList.size());

            adapter.notifyItemInserted(pTransactionDataArrayList.size());
            pTransactionDataSet.addAll(pTransactionDataArrayList);
//            for (PTransactionData cd : pTransactionDataArrayList) {
//                pTransactionDataSet.add(cd);
//            }

            adapter.notifyItemInserted(pTransactionDataSet.size());
        }
        stopLoading();

    }

    private void onItemClicked(int position) {
        Utils.psLog("Position : " + position);
        Intent intent;
        intent = new Intent(getActivity(),TransactionDetailActivity.class);
        intent.putExtra("selected_position", position);
        intent.putExtra("selected_transaction_id", pTransactionDataSet.get(position).id);
        if(getActivity() != null) {
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        }
    }


    private void startLoading(){
        try{
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }catch (Exception e){
            Utils.psErrorLog("startLoading", e);
        }
    }

    private void stopLoading(){
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }catch (Exception e){
            Utils.psErrorLog("stopLoading", e);
        }
    }

}
