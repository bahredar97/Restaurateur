package com.panaceasoft.restaurateur.activities;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Panacea-Soft on 20/9/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class CouponDiscountActivity extends AppCompatActivity{


    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private Intent intent;
    private SpannableString couponDiscountString;
    private int selectedShopId;
    private CoordinatorLayout mainLayout;
    private EditText txtCoupon;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private String selectedPaymentOption;


    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_discount);

        initData();
        initUI();
        bindData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                if(data.getStringExtra("close_activity").equals("YES")){
                    Utils.psLog(" >> CouponDiscountActivity  >> ");
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

        Button btnApply = findViewById(R.id.btn_apply);
        btnApply.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        Button btnSkip = findViewById(R.id.btn_skip);
        btnApply.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtCouponDiscountDesc = findViewById(R.id.coupon_discount_desc);
        txtCouponDiscountDesc.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        txtCoupon = findViewById(R.id.input_coupon);
        txtCoupon.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputValidation()) {
                    final String URL = Config.APP_API_URL + Config.POST_COUPON_SEARCH;
                    Utils.psLog(URL);

                    HashMap<String, String> params = new HashMap<>();
                    params.put("shop_id", String.valueOf(selectedShopId));
                    params.put("coupon_code", txtCoupon.getText().toString().trim());

                    doSubmit(URL, params);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), CheckoutConfirmActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", selectedPaymentOption);
                intent.putExtra("coupon_name", "");
                intent.putExtra("coupon_amount", "");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        });

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

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
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                selectedPaymentOption = bundle.getString("selected_payment_option");
                //Utils.psLog("Shop ID : > " + selectedShopId);
            }
            couponDiscountString = Utils.getSpannableString(getApplicationContext(), getResources().getString(R.string.coupon_title));
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
        toolbar.setTitle(couponDiscountString);

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private boolean inputValidation() {
        if(txtCoupon.getText().toString().equals("")) {
            Toast.makeText(this.getApplicationContext(), R.string.coupon_empty,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void doSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            String status = response.getString("status");
                            Utils.psLog("Response"+ response);
                            if (status.equals(jsonStatusSuccessString)) {

                                prgDialog.cancel();

                                JSONObject dat = response.getJSONObject("data");
                                String coupon_name = dat.getString("coupon_name");
                                String coupon_amount = dat.getString("coupon_amount");

                                Utils.psLog(coupon_name + " - " + coupon_amount);

                                intent = new Intent(getApplicationContext(), CheckoutConfirmActivity.class);
                                intent.putExtra("selected_shop_id", selectedShopId);
                                intent.putExtra("selected_payment_option", selectedPaymentOption);
                                intent.putExtra("coupon_name", coupon_name);
                                intent.putExtra("coupon_amount", coupon_amount);
                                startActivityForResult(intent, 1);
                                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);



                            } else {
                                Utils.psLog("Login Fail");
                                prgDialog.cancel();
                                showFailPopup();

                            }

                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Login Fail : " + e.getMessage());
                            e.printStackTrace();
                            showFailPopup();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.cancel();
                    Utils.psLog("Error: " + error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });
        req.setShouldCache(false);
        // add the request object to the queue to be executed
        VolleySingleton.getInstance(this).addToRequestQueue(req);

    }

    private void showFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.login);
        builder.setMessage(R.string.login_fail);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/



}
