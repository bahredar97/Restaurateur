package com.panaceasoft.restaurateur.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

public class ShopInfoActivity extends AppCompatActivity {

    private Toolbar toolbar ;
    private ImageView ivShopImage;
    private TextView tvShopName;
    private TextView tvDescription;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView tvAddress;
    private CoordinatorLayout mainLayout;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;
    //private static String[] PERMISSIONS_PHONECALL = {Manifest.permission.CALL_PHONE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        initUI();

        bindData();
    }

    @Override
    public void onDestroy() {

        try {
            toolbar = null;
            Utils.unbindDrawables(mainLayout);
            mainLayout = null;

            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }

    }

    private void initUI() {
        initToolbar();
        mainLayout = findViewById(R.id.coordinator_layout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        ivShopImage = findViewById(R.id.iv_shop_image);
        tvShopName = findViewById(R.id.tv_shop_name);
        tvShopName.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        tvDescription = findViewById(R.id.tv_description);
        tvDescription.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        tvPhone = findViewById(R.id.tv_phone);
        tvPhone.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        tvEmail = findViewById(R.id.tv_email);
        tvEmail.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        tvAddress = findViewById(R.id.tv_address);
        tvAddress.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
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

    private void bindData(){
        try {

            PShopData pd = GlobalData.shopdata;
            tvShopName.setText(pd.name);
            tvDescription.setText(pd.description);
            tvPhone.setText(pd.phone);
            tvEmail.setText(pd.email);
            tvAddress.setText(pd.address);

            try {
                Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + pd.cover_image_file).into(ivShopImage);
            }catch(Exception e){
                Utils.psErrorLogE("Error in Bind Toolbar Image.", e);
            }

        }catch (Exception e){
            Utils.psErrorLog("bindData", e);
        }
    }

    public void doPhoneCall(View view) {
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText()));
//        startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
        } else {
            //Open call function
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText() ));
            startActivity(intent);
        }
    }

    public void doEmail(View view) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{tvEmail.getText().toString()});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
