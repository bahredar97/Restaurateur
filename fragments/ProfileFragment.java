package com.panaceasoft.restaurateur.fragments;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;

import java.io.File;

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class ProfileFragment extends Fragment {

    private ImageView imgProfilePhoto;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvAboutMe;
    private TextView tvDeliveryAddress;
    private TextView tvBillingAddress;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        bindData();

        return view;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initUI(View view) {
        imgProfilePhoto = view.findViewById(R.id.iv_profile_photo);
        imgProfilePhoto.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvUserName = view.findViewById(R.id.tv_name);
        tvUserName.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        tvUserName.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvEmail = view.findViewById(R.id.tv_email);
        tvEmail.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        tvEmail.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvAboutMe = view.findViewById(R.id.tv_about_me);
        tvAboutMe.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        tvAboutMe.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvDeliveryAddress = view.findViewById(R.id.tv_delivery_address);
        tvDeliveryAddress.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        tvDeliveryAddress.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        tvBillingAddress = view.findViewById(R.id.tv_billing_address);
        tvBillingAddress.setTypeface(Utils.getTypeFace(getContext(), Utils.Fonts.ROBOTO));
        tvBillingAddress.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    public void bindData() {
        try {

            if (getContext() != null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

                tvUserName.setText(pref.getString("_login_user_name", ""));
                tvEmail.setText(pref.getString("_login_user_email", ""));


                if (pref.getString("_login_user_about_me", "").equals("")) {
                    tvAboutMe.setVisibility(View.GONE);
                } else {
                    tvAboutMe.setVisibility(View.VISIBLE);
                    tvAboutMe.setText(pref.getString("_login_user_about_me", ""));
                }

                if (pref.getString("_login_user_delivery_address", "").equals("")) {
                    tvDeliveryAddress.setVisibility(View.GONE);
                } else {
                    tvDeliveryAddress.setVisibility(View.VISIBLE);
                    tvDeliveryAddress.setText(pref.getString("_login_user_delivery_address", ""));
                }

                if (pref.getString("_login_user_billing_address", "").equals("")) {
                    tvBillingAddress.setVisibility(View.GONE);
                } else {
                    tvBillingAddress.setVisibility(View.VISIBLE);
                    tvBillingAddress.setText(pref.getString("_login_user_billing_address", ""));
                }

                File file;

                ContextWrapper cw = new ContextWrapper(Utils.activity.getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                file = new File(directory, pref.getString("_login_user_photo", ""));
                //file = new File(Environment.getExternalStorageDirectory() + "/" + pref.getString("_login_user_photo", ""));
                if (file.exists()) {
                    Utils.psLog("File is exist");
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imgProfilePhoto.setImageBitmap(myBitmap);
                } else {
                    Drawable myDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_person_black);
                    imgProfilePhoto.setImageDrawable(myDrawable);
                }

                Utils.psLog("Successfully loaded.");
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bind data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
}





