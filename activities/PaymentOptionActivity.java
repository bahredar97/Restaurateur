package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.utilities.Utils;

/**
 * Created by Panacea-Soft on 5/7/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PaymentOptionActivity extends AppCompatActivity{

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private Intent intent;
    private SpannableString paymentOptionString;
    private int selectedShopId;
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
        setContentView(R.layout.activity_payment_option);

        initData();
        initUI();
        bindData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                if(data.getStringExtra("close_activity").equals("YES")){
                    Utils.psLog(" >> PaymentOptionActivity >> close ? ");
                    Intent in = new Intent();
                    in.putExtra("close_activity", "YES");
                    setResult(RESULT_OK, in);
                    finish();
                }
            }

        }

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

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        initToolbar();

        mainLayout = findViewById(R.id.coordinator_layout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        Button btnGoStripe = findViewById(R.id.btn_go_stripe);
        btnGoStripe.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        Button btnGoBankTransfer = findViewById(R.id.btn_go_bank);
        btnGoBankTransfer.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        Button btnGoCod = findViewById(R.id.btn_go_cod);
        btnGoCod.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtStripeDesc = findViewById(R.id.stripe_desc);
        txtStripeDesc.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtBankDesc = findViewById(R.id.bank_desc);
        txtBankDesc.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtCodDesc = findViewById(R.id.cod_desc);
        txtCodDesc.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        LinearLayout stripeLayout = findViewById(R.id.stripe_payment);
        if(GlobalData.shopdata.stripe_enabled == 0) {
            stripeLayout.setVisibility(View.GONE);
        }


        LinearLayout bankLayout = findViewById(R.id.bank_payment);
        if(GlobalData.shopdata.banktransfer_enabled == 0) {
            bankLayout.setVisibility(View.GONE);
        }


        LinearLayout codLayout = findViewById(R.id.cod_payment);
        if(GlobalData.shopdata.cod_enabled == 0) {
            codLayout.setVisibility(View.GONE);
        }

        btnGoStripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Utils.psLog("Go Checkout Confirm with Stripe");
                intent = new Intent(getApplicationContext(), CheckoutConfirmActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "stripe");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                */
                intent = new Intent(getApplicationContext(), CouponDiscountActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "stripe");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);


            }
        });

        btnGoBankTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Utils.psLog("Go Checkout Confirm with Bank");
                intent = new Intent(getApplicationContext(), CheckoutConfirmActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "bank");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                */
                intent = new Intent(getApplicationContext(), CouponDiscountActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "bank");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        });

        btnGoCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Utils.psLog("Go Checkout Confirm with COD");
                intent = new Intent(getApplicationContext(), CheckoutConfirmActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "cod");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                */
                intent = new Intent(getApplicationContext(), CouponDiscountActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "cod");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        });
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
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            selectedShopId = getIntent().getIntExtra("selected_shop_id", 0);
            Utils.psLog("Shop ID : > " + selectedShopId);
            //Utils.psLog(" Shop ID From Global " + GlobalData.shopdata.id);
            // Utils.psLog(" COD " + GlobalData.shopdata.cod_enabled);

            paymentOptionString = Utils.getSpannableString(getApplicationContext(), getResources().getString(R.string.title_payment_option));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        } catch (Exception e){
            Utils.psErrorLog("Error in initData.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {
        toolbar.setTitle(paymentOptionString);

        if(GlobalData.shopdata.stripe_enabled == 0 && GlobalData.shopdata.banktransfer_enabled == 0 && GlobalData.shopdata.cod_enabled == 0) {
            showNoOptionPopup();
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void showNoOptionPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.noption_title);
        builder.setMessage(R.string.noption_message);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }
}
