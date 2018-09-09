package com.panaceasoft.restaurateur.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.models.BasketData;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;

import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.List;

import com.stripe.android.*;
import com.stripe.android.exception.AuthenticationException;


/**
 * Created by Panacea-Soft on 9/7/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class StripeActivity extends AppCompatActivity {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private Toolbar toolbar;
    private SpannableString stripeString;

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = '-';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;


    public static final String PUBLISHABLE_KEY = GlobalData.shopdata.stripe_publishable_key;
    public static final String API_KEY = GlobalData.shopdata.stripe_secret_key;

    DBHandler db = new DBHandler(this);
    private String deliveryAddress = "";
    private String billingAddress = "";
    private String email = "";
    private String phone = "";

    //private RequestQueue mRequestQueue;
    private HashMap<String, String> params = new HashMap<>();
    private EditText etCreditCard;
    private EditText etDate;
    private EditText etCVC;
    private String jsonStatusSuccessString;
    private ProgressDialog prgDialog;
    private int selectedShopId;
    private CoordinatorLayout mainLayout;
    private String couponDiscountAmount;
    private String shippingCost;
    public static Double stripeCheckoutTotalAmount = 0.0;


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stripe);
        ButterKnife.bind(this);

        initData();
        initUI();
        bindData();

        Utils.psLog("P Key >> " + PUBLISHABLE_KEY);
        Utils.psLog("T Key >> " + API_KEY);


    }

    @OnTextChanged(value = R.id.cardNumberEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cardDateEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.cardCVCEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }

    @Override
    public void onDestroy() {
        try {
            toolbar = null;
            prgDialog.cancel();
            prgDialog = null;

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData() {

        stripeString = Utils.getSpannableString(getApplicationContext(), getString(R.string.title_stripe));
        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString("user_email");
            phone = bundle.getString("user_phone");
            deliveryAddress = bundle.getString("user_delivery_address");
            billingAddress = bundle.getString("user_billing_address");
            couponDiscountAmount = bundle.getString("coupon_discount_amount");
            shippingCost = bundle.getString("flat_rate_shipping");

        }

        selectedShopId = getIntent().getIntExtra("selected_shop_id", 0);
        stripeCheckoutTotalAmount = (BasketActivity.totalAmount + Double.valueOf(shippingCost)) - Double.valueOf(couponDiscountAmount);

        loadBasketData();


    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI() {

        initToolbar();
        initProgressDialog();

        mainLayout = findViewById(R.id.coordinator_layout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        etCreditCard = findViewById(R.id.cardNumberEditText);
        etDate = findViewById(R.id.cardDateEditText);
        etCVC = findViewById(R.id.cardCVCEditText);

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
            if (getSupportActionBar() != null) {
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

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void bindData() {
        toolbar.setTitle(stripeString);
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return !isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    public void submitOrderToServer() {
        String URL = Config.APP_API_URL + Config.POST_TRANSACTIONS;


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
                                //Need to close all activities and load home page
                                //closeActivities();

                                showSuccessPopup();

                            } else {
                                showFailPopup();
                                Utils.psLog("Error in loading.");
                            }

                            prgDialog.cancel();

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
                } catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);

    }


    private void loadBasketData() {
//        List<BasketData> basketDataSet = new ArrayList<>();
//        basketDataSet.clear();
        List<BasketData> basket = db.getAllBasketDataByShopId(selectedShopId);

        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject;

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
                jsonObject.put("delivery_address", deliveryAddress);
                jsonObject.put("billing_address", billingAddress);
                jsonObject.put("total_amount", BasketActivity.totalAmount);
                jsonObject.put("basket_item_attribute_id", basketData.getSelectedAttributeIds());
                jsonObject.put("basket_item_attribute", basketData.getSelectedAttributeNames());
                jsonObject.put("payment_method", "stripe");
                jsonObject.put("email", email);
                jsonObject.put("phone", phone);
                jsonObject.put("coupon_discount_amount", couponDiscountAmount);
                jsonObject.put("flat_rate_shipping", shippingCost);
                jsonObject.put("platform", "Android");

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Utils.psErrorLog("Error in loading.", e);
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
        } catch (Exception e) {
            Utils.psErrorLog("Error in loading.", e);
        }

        Utils.psLog(" >>> params >> " + params);

    }

    private void closeActivities() {
        Intent in = new Intent();
        in.putExtra("close_activity", "YES");
        setResult(RESULT_OK, in);
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    public void doStripePayment(View view) throws AuthenticationException {


        if (etCreditCard.getText().toString().equals("") | etDate.getText().toString().equals("")) {
            Utils.psLog("Credit Card No and Date is empty.");
        } else {
            prgDialog.show();
            if (etCVC.getText().toString().equals("")) {
                Utils.psLog("Empty");
            } else {

                String[] explodedDate = etDate.getText().toString().split("/");
                String year = "20" + explodedDate[1];


                Card card = new Card(etCreditCard.getText().toString(), Integer.parseInt(explodedDate[0]), Integer.parseInt(year), etCVC.getText().toString());


                if (!card.validateCard()) {
                    Toast.makeText(this, "Invalid Credit Card", Toast.LENGTH_SHORT).show();
                    return;
                }

                Stripe stripe = new Stripe(PUBLISHABLE_KEY);
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            @Override
                            public void onError(Exception e) {
                                // Show localized error message
                                Toast.makeText(StripeActivity.this,
                                        e.getLocalizedMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            @Override
                            public void onSuccess(com.stripe.android.model.Token token) {
                                //chargeCustomer(token);
                                //need to pass token to server
                                final String URL = Config.APP_API_URL + Config.POST_STRIPE_TOKEN;
                                Utils.psLog(URL);

                                HashMap<String, String> params = new HashMap<>();
                                params.put("stripeToken", String.valueOf(token.getId()));
                                params.put("amount", String.valueOf(stripeCheckoutTotalAmount));
                                params.put("currency", GlobalData.shopdata.currency_short_form);
                                params.put("shopId", String.valueOf(selectedShopId));


                                doSubmit(URL, params);
                            }



                            /*
                            public void chargeCustomer(com.stripe.android.model.Token token) {
                                final Map<String, Object> chargeParams = new HashMap<String, Object>();
                                chargeParams.put("amount", (int) (stripeCheckoutTotalAmount * 100));
                                chargeParams.put("currency", GlobalData.shopdata.currency_short_form);
                                chargeParams.put("card", token.getId());
                                chargeParams.put("description", Config.STRIPE_CHECKOUT_NOTE);
                                Utils.psLog("token : " + token.getId());
                                new AsyncTask<Void, Void, Void>() {

                                    Charge charge;

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        try {
                                            com.stripe.Stripe.apiKey = API_KEY;
                                            charge = Charge.create(chargeParams);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    protected void onPostExecute(Void result) {
                                        submitOrderToServer();
                                    };

                                }.execute();
                            }
                            */


                        }
                );

            }

        }


    }

    public void doSubmit(String URL, final HashMap<String, String> params) {
        Utils.psLog("Stripe Params " + params);
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        prgDialog.cancel();
                        try {
                            //  pb.setVisibility(view.GONE);

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                Utils.psLog(status);
                                submitOrderToServer();

                            } else {
                                showFailPopup();
                                Utils.psLog("Error in loading.");
                            }

                            prgDialog.cancel();

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
                    VolleyLog.e("Error: ", error.getMessage());
                    prgDialog.cancel();
                    Toast.makeText(getApplicationContext(), R.string.order_fail_card, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);
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

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


}
