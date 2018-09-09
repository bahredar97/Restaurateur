package com.panaceasoft.restaurateur.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.SearchResultActivity;
import com.panaceasoft.restaurateur.listeners.SelectListener;
import com.panaceasoft.restaurateur.uis.PSPopupSingleSelectView;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class SearchFragment extends Fragment {

    private TextView txt_search;
    private int selectedShopId;
    private String selectedShopName;
    private String selectCityString;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public SearchFragment() {

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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initData();

        initUI(view);

        return view;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(View view) {

        LinearLayout popupContainer = view.findViewById(R.id.choose_container);
        popupContainer.removeAllViews();

        PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(getActivity(), selectCityString, GlobalData.shopDatas, "");
        psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
            @Override
            public void Select(View view, int position, CharSequence text) {

            }

            @Override
            public void Select(View view, int position, CharSequence text, int id) {
                selectedShopId = id;
                selectedShopName = text.toString();
            }

            @Override
            public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {

            }
        });
        popupContainer.addView(psPopupSingleSelectView);

        Button btn_search = view.findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    prepareForSearch();
                }
            }
        });

        txt_search = view.findViewById(R.id.input_search);

        if(Config.SHOW_ADMOB) {
            AdView mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            AdView mAdView = view.findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);
        }

        btn_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        txt_search.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData(){
        selectCityString = getResources().getString(R.string.select_shop);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void prepareForSearch() {
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra("selected_shop_id", selectedShopId + "");
        intent.putExtra("search_keyword", txt_search.getText().toString().trim());
        intent.putExtra("selected_shop_name", selectedShopName);
        startActivity(intent);
        if(getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

}