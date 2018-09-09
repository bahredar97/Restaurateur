package com.panaceasoft.restaurateur.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.models.BasketData;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Panacea-Soft on 6/7/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class CheckoutConfirmActivity extends AppCompatActivity{
    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private int selectedShopId;
    private String jsonStatusSuccessString;
    private SpannableString checkoutConfirmString;
    private SharedPreferences pref;
    private String selectedPayemntOption;
    private TextView txtSubTotalAmount;
    private TextView txtSelectedPayment;
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etUserPhone;
    private EditText etUserDeliveryAddress;
    private EditText etUserBillingAddress;
    DBHandler db = new DBHandler(this);
    private HashMap<String, String> params = new HashMap<>();
    private ProgressDialog prgDialog;
    private CoordinatorLayout mainLayout;
    private String couponDiscountAmount;
    private TextView txtCouponDiscountAmount;
    private TextView txtShippingCostAmount;
    private TextView txtTotalAmount;


    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_confirm);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        initUI();
        initData();
        bindData();

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                if(data.getStringExtra("close_activity").equals("YES")){
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
            pref = null;
            prgDialog.cancel();
            prgDialog = null;

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
        initProgressDialog();
        mainLayout = findViewById(R.id.coordinator_layout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        TextView txtSubTotalAmountLabel = findViewById(R.id.sub_total_amount_label);
        txtSubTotalAmountLabel.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        txtSubTotalAmount = findViewById(R.id.sub_total_amount);
        txtSubTotalAmount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtCouponDiscountLabel = findViewById(R.id.coupon_discount_label);
        txtCouponDiscountLabel.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        txtCouponDiscountAmount = findViewById(R.id.coupon_discount_amount);
        txtCouponDiscountAmount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtShippingCostLabel = findViewById(R.id.shipping_cost_label);
        txtShippingCostLabel.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        txtShippingCostAmount = findViewById(R.id.shipping_cost_amount);
        txtShippingCostAmount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        TextView txtTotalAmountLabel = findViewById(R.id.total_amount_label);
        txtTotalAmountLabel.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        txtTotalAmount = findViewById(R.id.total_amount);
        txtTotalAmount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        txtSelectedPayment = findViewById(R.id.selected_payment);
        txtSelectedPayment.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        etUserName = findViewById(R.id.input_name);
        etUserName.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        etUserEmail = findViewById(R.id.input_email);
        etUserEmail.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        etUserPhone = findViewById(R.id.input_phone);
        etUserPhone.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        etUserDeliveryAddress = findViewById(R.id.input_delivery_address);
        etUserDeliveryAddress.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        etUserBillingAddress = findViewById(R.id.input_billing_address);
        etUserBillingAddress.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

        Button btnConfirmCheckout = findViewById(R.id.btn_confirm_checkout);
        btnConfirmCheckout.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

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

    private void initProgressDialog() {
        try {
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initProgressDialog.", e);
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
            Bundle bundle = getIntent().getExtras();

            if(bundle != null) {

                selectedPayemntOption = bundle.getString("selected_payment_option");
                //String couponName = bundle.getString("coupon_name");
                couponDiscountAmount = bundle.getString("coupon_amount");


                String subTotalAmountStr =  " : " + Utils.format(BasketActivity.totalAmount) + GlobalData.shopdata.currency_symbol + "(" + GlobalData.shopdata.currency_short_form + ")";
                txtSubTotalAmount.setText(subTotalAmountStr);

                if (!couponDiscountAmount.equals("")) {
                    String couponDiscountAmountStr = " : - " + Utils.format(Double.valueOf(couponDiscountAmount)) + GlobalData.shopdata.currency_symbol + "(" + GlobalData.shopdata.currency_short_form + ")";
                    txtCouponDiscountAmount.setText(couponDiscountAmountStr);
                } else {
                    txtCouponDiscountAmount.setText(" : N.A ");
                    couponDiscountAmount = "0.0";
                }

                if (!GlobalData.shopdata.flat_rate_shipping.equals("0")) {
                    String shippingCostStr = " : + " + Utils.format(Double.valueOf(GlobalData.shopdata.flat_rate_shipping)) + GlobalData.shopdata.currency_symbol + "(" + GlobalData.shopdata.currency_short_form + ")";
                    txtShippingCostAmount.setText(shippingCostStr);
                } else {
                    txtShippingCostAmount.setText(" : N.A ");
                }


                Double finalTotalAmount = BasketActivity.totalAmount - Double.valueOf(couponDiscountAmount) + Double.valueOf(GlobalData.shopdata.flat_rate_shipping);

//            finalTotalAmount = Double.valueOf(String.format(Locale.US, "%.2f", finalTotalAmount));
//" : " +
                String totalAmountStr =  Utils.format(finalTotalAmount) + GlobalData.shopdata.currency_symbol + "(" + GlobalData.shopdata.currency_short_form + ")";
                txtTotalAmount.setText(totalAmountStr);


                switch (selectedPayemntOption) {
                    case "stripe":
                        txtSelectedPayment.setText(getString(R.string.stripe));
                        break;
                    case "bank":
                        txtSelectedPayment.setText(getString(R.string.bank_transfer));
                        break;
                    case "cod":
                        txtSelectedPayment.setText(getString(R.string.cod));
                        break;
                }

                etUserName.setText(pref.getString("_login_user_name", ""));
                etUserEmail.setText(pref.getString("_login_user_email", ""));
                etUserPhone.setText(pref.getString("_login_user_phone", ""));
                etUserDeliveryAddress.setText(pref.getString("_login_user_delivery_address", ""));
                etUserBillingAddress.setText(pref.getString("_login_user_billing_address", ""));


                jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
                checkoutConfirmString = Utils.getSpannableString(getApplicationContext(), getString(R.string.title_checkout_confirm));

            }

        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {
        toolbar.setTitle(checkoutConfirmString);
    }

    private void submitOrderToServer() {
        prgDialog.show();
        String URL = Config.APP_API_URL + Config.POST_TRANSACTIONS;
        Utils.psLog(" >>> here >>> " + URL);
        Utils.psLog(">> params >> " + params);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.psLog(" sever resp : " + response);

                        try {
                            //  pb.setVisibility(view.GONE);

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                Utils.psLog(status);
                                //Need to clear basket sqlite data
                                db.deleteBasketByShopId(selectedShopId);
                                db.deleteAttributeByShopId(selectedShopId);

                                showSuccessPopup();

                            } else {
                                showFailPopup();
                                Utils.psLog("Error in loading.");
                            }

                            prgDialog.cancel();

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

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);

    }

    private void loadBasketData(String paymentOption) {

        List<BasketData> basket = db.getAllBasketDataByShopId(selectedShopId);

        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject ;

        for (BasketData basketData : basket) {


            try {
                jsonObject = new JSONObject();

                jsonObject.put("item_id", String.valueOf(basketData.getItemId()));
                jsonObject.put("shop_id", String.valueOf(basketData.getShopId()));
                jsonObject.put("unit_price", basketData.getUnitPrice());
                jsonObject.put("discount_percent", basketData.getDiscountPercent());
                jsonObject.put("name", basketData.getName());
                jsonObject.put("qty", basketData.getQty());
                jsonObject.put("user_id", basketData.getUserId());
                jsonObject.put("payment_trans_id", "");
                jsonObject.put("delivery_address", etUserDeliveryAddress.getText().toString());
                jsonObject.put("billing_address", etUserBillingAddress.getText().toString());
                jsonObject.put("total_amount", BasketActivity.totalAmount);
                jsonObject.put("basket_item_attribute_id", basketData.getSelectedAttributeIds());
                jsonObject.put("basket_item_attribute", basketData.getSelectedAttributeNames());
                jsonObject.put("payment_method", paymentOption);
                jsonObject.put("email", etUserEmail.getText().toString());
                jsonObject.put("phone", etUserPhone.getText().toString());
                jsonObject.put("coupon_discount_amount", couponDiscountAmount);
                jsonObject.put("flat_rate_shipping", GlobalData.shopdata.flat_rate_shipping);
                jsonObject.put("platform", "Android");

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        JSONObject studentsObj = new JSONObject();
        String jsonStr;
        try {

            studentsObj.put("Orders", jsonArray);
            jsonStr = studentsObj.toString();
            Utils.psLog(" json >>> " + jsonStr);

            params.put("orders", jsonArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.psLog(" >>> params >> " + params);

    }

    private void closeActivities() {
        Utils.psLog(" >>> CheckoutConfirmActivity >>> closeActivities ???  ");
        Intent in = new Intent();
        in.putExtra("close_activity", "YES");
        setResult(RESULT_OK, in);
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void doConfirmCheckout(View view) {

        switch (selectedPayemntOption) {
            case "stripe":
                Intent intent = new Intent(getApplicationContext(), StripeActivity.class);
                intent.putExtra("selected_shop_id", selectedShopId);
                intent.putExtra("selected_payment_option", "stripe");
                intent.putExtra("user_email", etUserEmail.getText().toString());
                intent.putExtra("user_phone", etUserPhone.getText().toString());
                intent.putExtra("user_delivery_address", etUserDeliveryAddress.getText().toString());
                intent.putExtra("user_billing_address", etUserBillingAddress.getText().toString());
                intent.putExtra("coupon_discount_amount", couponDiscountAmount);
                intent.putExtra("flat_rate_shipping", GlobalData.shopdata.flat_rate_shipping);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

                break;
            case "bank":
                loadBasketData("bank");
                submitOrderToServer();
                break;
            case "cod":
                loadBasketData("cod");
                submitOrderToServer();
                break;
        }


    }

    public void showSuccessPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.order_submit_title);
        builder.setMessage(R.string.order_success);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeActivities();
                Utils.psLog("OK clicked.");
            }
        });
        builder.show();
    }

    public void showFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.order_submit_title);
        builder.setMessage(R.string.order_fail);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

}
