package com.panaceasoft.restaurateur.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.SpinAdapter;
import com.panaceasoft.restaurateur.models.AttributeData;
import com.panaceasoft.restaurateur.models.AttributeRowData;
import com.panaceasoft.restaurateur.models.BasketData;
import com.panaceasoft.restaurateur.models.PAttributesData;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.models.PReviewData;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.utilities.CacheRequest;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.MyActivity;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.rey.material.widget.Spinner;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.v7.app.AlertDialog;


/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class DetailActivity extends AppCompatActivity {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private Toolbar toolbar;
    private ImageView detailImage;
    private SharedPreferences pref;
    private TextView txtLikeCount;
    private TextView txtReviewCount;
    private TextView txtTotalReview;
    private TextView txtReviewMessage;
    private TextView txtNameTime;
    private TextView txtPhone;
    private TextView txtDescription;
    private TextView title;
    private TextView txtPrice;
    private TextView txtQty;
    private EditText editTextQty;
    private TextView txtDiscount;
    private ImageView userPhoto;
    private Button btnLike;
    private Button btnMoreReview;
    private Button btnInquiry;
    private FloatingActionButton fab;
    private int selectedItemId;
    private int selectedShopId;
    private boolean isEditMode;
    private Bundle bundle;
    private Intent intent;
    private Boolean isFavourite = false;
    private RatingBar getRatingBar;
    private RatingBar setRatingBar;
    private TextView ratingCount;
    private Animation animation;
    private String jsonStatusSuccessString;
    private LinearLayout attributeTitleLayout;
    private DBHandler db = new DBHandler(this);
    private double calculatedPrice = 0.0;
    private double additionalPrice = 0.0;
    private int basketCount = 0;
    private MenuItem menuItem;
    private float touchX = 0;
    private float touchY = 0;
    private ImageView ivAndroid;
    private Picasso p;
    private CoordinatorLayout mainLayout;
    private TextView txtSelectedAttribute;
    private LinearLayout attributeSelectedLayout;
    private int spinnerCreation = 0;
    private double attributePriceOnly = 0.0;
    private String selectedAttributeNameStr = "";
    private int keyId = -1; // This is local basket id
    private String keyAttr = "";
    private PItemData cacheItemData;

    private int MAX_WIDTH = 0;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        MAX_WIDTH = Utils.getScreenWidth(getApplicationContext());

        initData();

        initUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {

                    try {
                        if (data.getStringExtra("from").equals("basket")) {
                            GlobalData.itemData = cacheItemData;
                        }
                    } catch (Exception e) {
                        Utils.psLog("Don't know from");
                    }

                    bindTitle();
                    bindDescription();
                    bindToolbarImage();
                    bindCountValues();
                    bindReview();
                    bindRate();
                    bindFavourite(fab);
                    bindPrice();
                    bindQty();
                    bindDiscount();
                    bindCart();

                    try {
                        if (data.getStringExtra("close_activity").equals("YES")) {
                            Intent in = new Intent();
                            in.putExtra("close_activity", "YES");
                            setResult(RESULT_OK, in);
                            finish();
                        }
                    } catch (Exception e) {
                        Utils.psLog("No data in data for close activity checking.");
                    }


                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("onActivityResult", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent in = new Intent();
            in.putExtra("selected_item_id", GlobalData.itemData.id);
            in.putExtra("like_count", GlobalData.itemData.like_count);
            in.putExtra("review_count", GlobalData.itemData.review_count);
            in.putExtra("close_activity", "NO");
            in.putExtra("refresh_data", "YES");
            setResult(RESULT_OK, in);
            if (!isEditMode) {
                GlobalData.itemData = null;
            }
            finish();
            overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in BackPress.", e);
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        menuItem = menu.findItem(R.id.action_basket);

        try {
            if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {
                basketCount = db.getBasketCountByShopId(GlobalData.itemData.shop_id);
                if (basketCount > 0) {
                    menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
                } else {
                    menuItem.setVisible(false);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in onCreateOptionsMenu.", e);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == R.id.action_basket) {

                if (pref.getInt("_login_user_id", 0) != 0) {

                    if (!isEditMode && GlobalData.itemData != null) {
                        basketCount = db.getBasketCountByShopId(GlobalData.itemData.shop_id);
                        if (basketCount > 0) {
                            intent = new Intent(getApplicationContext(), BasketActivity.class);
                            intent.putExtra("selected_shop_id", selectedShopId);


                            if (GlobalData.shopDatas != null) {

                                for (PShopData pShopData : GlobalData.shopDatas) {
                                    if (pShopData.id == GlobalData.itemData.shop_id) {
                                        GlobalData.shopdata = pShopData;
                                        startActivityForResult(intent, 1);
                                        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                                        break;
                                    }
                                }

                            } else {
                                Toast.makeText(this, "Can't find shop data.", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            showCartEmpty();
                        }
                    } else {
                        // This is edit mode of detail activity
                        // So, it will go back to basket list
                        onBackPressed();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Utils.psErrorLog("onOptionsItemSelected", e);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        try {
            toolbar = null;
            detailImage = null;
            pref = null;
            txtLikeCount = null;
            txtReviewCount = null;
            txtTotalReview = null;
            txtReviewMessage = null;
            txtNameTime = null;
            txtPhone = null;
            txtDescription = null;
            title = null;
            userPhoto = null;
            btnLike = null;
            btnMoreReview = null;
            btnInquiry = null;
            fab = null;
            bundle = null;
            intent = null;
            getRatingBar = null;
            setRatingBar = null;
            ratingCount = null;
            animation = null;

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;
            //GlobalData.itemData = null;

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        try {
            // Event to know the user clicked point
            this.touchX = event.getX();
            this.touchY = event.getY();
        } catch (Exception e) {
            Utils.psErrorLog("dispatchTouchEvent", e);
        }
        return super.dispatchTouchEvent(event);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData() {

        try {
            cacheItemData = GlobalData.itemData;

            p = new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();

            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
            selectedShopId = getIntent().getIntExtra("selected_shop_id", 0);
            isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
            keyId = getIntent().getIntExtra("keyId", -1);
            keyAttr = getIntent().getStringExtra("keyAttr");

            requestData(Config.APP_API_URL + Config.ITEMS_BY_ID + selectedItemId + "/shop_id/" + selectedShopId);
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

            updateTouchCount(selectedItemId);
        } catch (Exception e) {
            Utils.psErrorLog("initData", e);
        }
    }

    private void updateTouchCount(int selectedItemId) {
        try {
            final String URL = Config.APP_API_URL + Config.POST_TOUCH_COUNT + selectedItemId;
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("shop_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params, "touch");
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Touch Count.", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {

        try {
            initToolbar();

            btnLike = findViewById(R.id.btn_like);
            btnLike.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            btnLike.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtLikeCount = findViewById(R.id.total_like_count);
            txtLikeCount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtLikeCount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtReviewCount = findViewById(R.id.total_review_count);
            txtReviewCount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtReviewCount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtTotalReview = findViewById(R.id.total_review);
            txtTotalReview.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtTotalReview.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtReviewMessage = findViewById(R.id.review_message);
            txtReviewMessage.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtReviewMessage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtNameTime = findViewById(R.id.name_time);
            txtNameTime.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtNameTime.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            TextView txtAvailableAttribute = findViewById(R.id.available_attribute);
            txtAvailableAttribute.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtAvailableAttribute.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtPrice = findViewById(R.id.price);
            txtPrice.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtPrice.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));


            txtQty = findViewById(R.id.qty);
            txtQty.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtQty.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtDescription = findViewById(R.id.txtDescription);
            txtDescription.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtDescription.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            title = findViewById(R.id.title);
            title.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            txtDiscount = findViewById(R.id.discount);
            txtDiscount.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            txtDiscount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            editTextQty = findViewById(R.id.edit_text_Qty);
            editTextQty.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            editTextQty.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            userPhoto = findViewById(R.id.user_photo);
            userPhoto.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            detailImage = findViewById(R.id.detail_image);
            detailImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnMoreReview = findViewById(R.id.btn_more_review);
            btnMoreReview.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            btnMoreReview.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnInquiry = findViewById(R.id.btn_inquiry);
            btnInquiry.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            btnInquiry.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            Button btnShopInfo = findViewById(R.id.btn_shopinfo);
            btnShopInfo.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            btnShopInfo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
            btnAddToCart.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            btnAddToCart.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            getRatingBar = findViewById(R.id.get_rating);
            setRatingBar = findViewById(R.id.set_rating);
            ratingCount = findViewById(R.id.rating_count);
            animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.pop_out);

            ivAndroid = findViewById(R.id.iv_android);

            attributeTitleLayout = findViewById(R.id.attribute_title);

            txtSelectedAttribute = findViewById(R.id.selected_attribute);
            txtSelectedAttribute.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            attributeSelectedLayout = findViewById(R.id.selected_attribute_title);

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (pref.getInt("_login_user_id", 0) != 0) {

                        if (editTextQty.getText().toString().matches("") || Integer.parseInt(editTextQty.getText().toString()) == 0) {

                            showRequiredQty();

                        } else {

                            AnimatorSet animSetXYS = new AnimatorSet();
                            ObjectAnimator moveAnimS = ObjectAnimator.ofFloat(ivAndroid, "X", touchX);
                            ObjectAnimator moveAnimS2 = ObjectAnimator.ofFloat(ivAndroid, "Y", touchY - 50); // 50 will be height of animator object
                            animSetXYS.playTogether(moveAnimS, moveAnimS2);
                            animSetXYS.setDuration(1);
                            animSetXYS.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ivAndroid.setVisibility(View.VISIBLE);


                                    AnimatorSet animSetXY = new AnimatorSet();
                                    ObjectAnimator moveAnim = ObjectAnimator.ofFloat(ivAndroid, "X", MAX_WIDTH - 50); // 50 will be width of animator
                                    ObjectAnimator moveAnim2 = ObjectAnimator.ofFloat(ivAndroid, "Y", 0);
                                    animSetXY.playTogether(moveAnim, moveAnim2);
                                    animSetXY.setDuration(800);
                                    animSetXY.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            try {
                                                ivAndroid.setVisibility(View.INVISIBLE);
                                                doAddToCart();
                                            } catch (Exception e) {
                                                Utils.psErrorLog("onAnimationEnd", e);
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    animSetXY.start();

                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            animSetXYS.start();
                        }

                    } else {
                        showNeedLogin();
                    }

                }
            });

            fab = findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFavourite(v);


                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            try {
                                if (isFavourite) {
                                    isFavourite = false;
                                    fab.setImageResource(R.drawable.ic_favorite_border);
                                } else {
                                    isFavourite = true;
                                    fab.setImageResource(R.drawable.ic_favorite_white);
                                }
                            } catch (Exception e) {
                                Utils.psErrorLog("onAnimationStart", e);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }


                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    fab.clearAnimation();
                    fab.startAnimation(animation);
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    doLike(v);

                    Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);
                    btnLike.startAnimation(rotate);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });

            editTextQty.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    return false;
                }
            });

            getRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    try {
                        if (pref.getInt("_login_user_id", 0) != 0) {
                            ratingChanged(rating);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.login_required,
                                    Toast.LENGTH_LONG).show();
                            getRatingBar.setRating(0);
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("onRatingChanged", e);
                    }
                }
            });

            if (Config.SHOW_ADMOB) {
                AdView mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                AdView mAdView2 = findViewById(R.id.adView2);
                AdRequest adRequest2 = new AdRequest.Builder().build();
                mAdView2.loadAd(adRequest2);
            } else {
                AdView mAdView = findViewById(R.id.adView);
                mAdView.setVisibility(View.GONE);
                AdView mAdView2 = findViewById(R.id.adView2);
                mAdView2.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Utils.psErrorLogE("Error in Init UI.", e);
        }

    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (Utils.isAndroid_5_0()) {
                Utils.setMargins(toolbar, 0, -78, 0, 0);
            }
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
            Utils.psErrorLog("initToolbar", e);
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void bindData() {

        try {
            bindTitle();
            bindDescription();
            bindToolbarImage();
            bindCountValues();
            bindReview();
            bindRate();
            bindFavourite(fab);
            bindPrice();
            bindQty();
            bindAttribute();
            bindDiscount();
            bindCart();
            bindSelectedAttributeName();
        } catch (Exception e) {
            Utils.psErrorLog("bindData", e);
        }

    }

    private void bindSelectedAttributeName() {
        try {
            if (isBasketItem() > 0) {

                if (keyId != -1) {
                    BasketData basket = db.getBasketById(keyId);
                    String selectedAttrStr = getString(R.string.selected_attribute) + " : " + Utils.removeLastChar(basket.getSelectedAttributeNames());
                    txtSelectedAttribute.setText(selectedAttrStr);
                }
            } else {
                attributeSelectedLayout.setVisibility(View.GONE);
                db.deleteAttributeByItemId(selectedItemId);
            }
        } catch (Exception e) {
            Utils.psErrorLog("bindSelectedAttributeName", e);
        }
    }

    private void bindCart() {
        try {
            if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {
                basketCount = db.getBasketCountByShopId(GlobalData.itemData.shop_id);
                if (basketCount > 0) {
                    menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
                } else {
                    menuItem.setVisible(false);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in onCreateOptionsMenu.", e);
        }

    }

    private void bindTitle() {

        try {
            title.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            if (GlobalData.itemData != null) {
                title.setText(GlobalData.itemData.name);
            }
        } catch (Exception e) {
            Utils.psErrorLog("bindTitle", e);
        }

    }

    private void bindPrice() {
        try {
            if (GlobalData.itemData != null) {

                double unitPrice;
                String currencySymbol = "";
                String currencyShortForm = "";

                if (GlobalData.itemData.currency_symbol != null) {
                    currencySymbol = GlobalData.itemData.currency_symbol;
                }

                if (GlobalData.itemData.currency_short_form != null) {
                    currencyShortForm = GlobalData.itemData.currency_short_form;
                }

                unitPrice = (double) GlobalData.itemData.unit_price;

                String priceText = getString(R.string.price) + Utils.format(unitPrice) + currencySymbol + "(" + currencyShortForm + ")";
                txtPrice.setText(priceText);
                calculatedPrice = unitPrice; //Double.valueOf(String.format(Locale.US, "%.2f", unitPrice));
            }
        } catch (Exception e) {
            Utils.psErrorLog("bindPrice", e);
        }

    }

    private void bindQty() {
        try {
            txtQty.setText(getString(R.string.qty));
            if (isBasketItem() > 0) {

                if (keyId != -1) {
                    BasketData basket = db.getBasketById(keyId);

                    editTextQty.setText(String.valueOf(basket.getQty()));
                    editTextQty.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                    editTextQty.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
                }

            }
        } catch (Exception e) {
            Utils.psErrorLog("bindQty", e);
        }

    }


    private void bindToolbarImage() {
        try {
            if (GlobalData.itemData != null) {
                detailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                    }
                });
//                p.load(Config.APP_IMAGES_URL + GlobalData.itemData.images.get(0).path)
////                        //.transform(new BitmapTransform(MAX_WIDTH, MAX_WIDTH))
////                        .resize(MAX_WIDTH, MAX_WIDTH)
////                        .onlyScaleDown()
////                        .into(detailImage);

                Utils.bindImage(this, p, detailImage, GlobalData.itemData.images.get(0), 2);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Toolbar Image.", e);
        }
    }

    private void bindCountValues() {
        try {
            if (GlobalData.itemData != null) {
                String likeCountStr = " " + GlobalData.itemData.like_count + " ";
                txtLikeCount.setText(likeCountStr);

                String reviewCountStr = " " + GlobalData.itemData.review_count + " ";
                txtReviewCount.setText(reviewCountStr);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Count.", e);
        }
    }

    private void bindDiscount() {

        try {
            if (GlobalData.itemData != null) {
                if (GlobalData.itemData.discount_type_id == 0) {
                    txtDiscount.setVisibility(View.GONE);
                } else {
                    String discountStr = "(" + GlobalData.itemData.discount_name + ")";
                    txtDiscount.setText(discountStr);
                    calculatedPrice = GlobalData.itemData.unit_price - (Float.parseFloat(GlobalData.itemData.discount_percent) * GlobalData.itemData.unit_price);

                    String priceStr = getString(R.string.price) +
                            Utils.format(calculatedPrice) + GlobalData.itemData.currency_symbol + "(" + GlobalData.itemData.currency_short_form + ")";
                    txtPrice.setText(priceStr);

                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("bindDiscount", e);
        }

    }

    private void bindReview() {
        try {

            if (GlobalData.itemData != null) {
                ArrayList<PReviewData> itemReviewData = GlobalData.itemData.reviews;

                txtNameTime.setVisibility(View.VISIBLE);
                txtReviewMessage.setVisibility(View.VISIBLE);
                btnMoreReview.setText(getString(R.string.view_more_review));

                if (itemReviewData != null) {
                    if (itemReviewData.size() > 0) {
                        if (itemReviewData.size() == 1) {
                            String totalReviewStr = itemReviewData.size() + " " + getString(R.string.review);
                            txtTotalReview.setText(totalReviewStr);
                        } else {
                            String totalReviewStr = itemReviewData.size() + " " + getString(R.string.reviews);
                            txtTotalReview.setText(totalReviewStr);
                        }


                        try {
                            PReviewData reviewData = itemReviewData.get(0);
                            String nameTimeStr = reviewData.appuser_name + " " + "(" + reviewData.added + ")";
                            txtNameTime.setText(nameTimeStr);
                            txtReviewMessage.setText(reviewData.review);
                            if (!reviewData.profile_photo.equals("")) {
                                Utils.psLog(" *** Loading User photo : " + Config.APP_IMAGES_URL + reviewData.profile_photo);
                                //p.load(Config.APP_IMAGES_URL + reviewData.profile_photo).resize(150, 150).into(userPhoto);
                                Utils.bindImage(this, p, userPhoto, reviewData.profile_photo, 3);
                            } else {
                                userPhoto.setColorFilter(Color.argb(114, 114, 114, 114));
                            }
                        } catch (Exception e) {
                            Utils.psErrorLog("Error in review UI binding.", e);
                        }


                    } else {
                        txtTotalReview.setText(getString(R.string.no_review_count));
                        txtNameTime.setVisibility(View.GONE);
                        txtReviewMessage.setVisibility(View.GONE);
                        btnMoreReview.setText(getString(R.string.add_first_review));

                        Drawable myDrawable = ContextCompat.getDrawable(this, R.drawable.ic_rate_review_black);
                        userPhoto.setImageDrawable(myDrawable);

                    }
                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Reviews.", e);
        }
    }

    private void bindAttribute() {

        try {
            if (spinnerCreation == 0 && GlobalData.itemData != null) {

                LinearLayout attributeContainer = findViewById(R.id.attribute_container);
                MyActivity myAct = new MyActivity(this);

                ArrayList<PAttributesData> itemAttributeData = GlobalData.itemData.attributes;

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                Utils.psLog("Param Left " + Utils.dpToPx(getApplicationContext(), 44));
                //params.setMargins(Utils.dpToPx(44), 5, 10, 10);
                params.setMargins(Utils.dpToPx(getApplicationContext(), 44), Utils.dpToPx(getApplicationContext(), 12), Utils.dpToPx(getApplicationContext(), 12), Utils.dpToPx(getApplicationContext(), 8));

                if (itemAttributeData != null) {

                    Utils.psLog("Attribute Size >>>> " + itemAttributeData.size());

                    if (itemAttributeData.size() > 0) {

                        for (int i = 0; i < itemAttributeData.size(); i++) {
                            Spinner spin = myAct.getSpinner();
                            spin.setMinimumHeight(Utils.dpToPx(getApplicationContext(), 24));

                            if (itemAttributeData.get(i) != null) {
                                if (itemAttributeData.get(i).details != null) {

                                    AttributeRowData[] attributesData = new AttributeRowData[itemAttributeData.get(i).details.size() + 1];

                                    for (int j = 0; j < itemAttributeData.get(i).details.size() + 1; j++) {
                                        attributesData[j] = new AttributeRowData();

                                        if (j == 0) {
                                            attributesData[j].setId(0);
                                            attributesData[j].setShopId(0);
                                            attributesData[j].setHeaderId(0);
                                            attributesData[j].setItemId(0);
                                            attributesData[j].setName("Please Select - " + itemAttributeData.get(i).name);
                                        } else {
                                            attributesData[j].setId(itemAttributeData.get(i).details.get(j - 1).id);
                                            attributesData[j].setShopId(itemAttributeData.get(i).details.get(j - 1).shop_id);
                                            attributesData[j].setHeaderId(itemAttributeData.get(i).details.get(j - 1).header_id);
                                            attributesData[j].setItemId(itemAttributeData.get(i).details.get(j - 1).item_id);
                                            attributesData[j].setAdditionalPrice(itemAttributeData.get(i).details.get(j - 1).additional_price);

                                            if (itemAttributeData.get(i).details.get(j - 1).additional_price.equals("0")) {
                                                attributesData[j].setName(itemAttributeData.get(i).name + " : " + itemAttributeData.get(i).details.get(j - 1).name);
                                            } else {
                                                attributesData[j].setName(itemAttributeData.get(i).name + " : " + itemAttributeData.get(i).details.get(j - 1).name + "(" +
                                                        Utils.format(Double.valueOf(itemAttributeData.get(i).details.get(j - 1).additional_price)) +
                                                        GlobalData.itemData.currency_symbol
                                                        + ")");
                                            }
                                        }

                                    }


                                    SpinAdapter adapter = new SpinAdapter(this, R.layout.spinner_item, attributesData);

                                    spin.setAdapter(adapter);
                                    spin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(Spinner spinner, View view, int position, long l) {

                                            if (position != 0) {
                                                AttributeRowData attribute;
                                                if (!(spinner.getSelectedItem() == null)) {
                                                    attribute = (AttributeRowData) spinner.getSelectedItem();


                                                    List<AttributeData> attributesAllByID = db.getAllAttributeDataByIds(attribute.getItemId(), attribute.getHeaderId());

                                                    if (attributesAllByID.size() == 0) {
                                                        //Like First Time, Table is nothing
                                                        db.addAttribute(new AttributeData(
                                                                attribute.getHeaderId(),
                                                                attribute.getId(),
                                                                selectedItemId,
                                                                selectedShopId,
                                                                attribute.getName(),
                                                                attribute.getAdditionalPrice()
                                                        ));

                                                        Utils.psLog("1 Added Attribute Table");

                                                    } else {
                                                        //At least one record is added

                                                        for (AttributeData attData : attributesAllByID) {
                                                            if (attData.getHeader_id() == attribute.getHeaderId()) {
                                                                //already exist need to update

                                                                db.updateAttributeByIds(new AttributeData(
                                                                        attribute.getHeaderId(),
                                                                        attribute.getId(),
                                                                        selectedItemId,
                                                                        selectedShopId,
                                                                        attribute.getName(),
                                                                        attribute.getAdditionalPrice()

                                                                ), attribute.getHeaderId(), attribute.getItemId());

                                                                Utils.psLog("Update Attribute");

                                                            } else {
                                                                // new record insert
                                                                db.addAttribute(new AttributeData(
                                                                        attribute.getHeaderId(),
                                                                        attribute.getId(),
                                                                        selectedItemId,
                                                                        selectedShopId,
                                                                        attribute.getName(),
                                                                        attribute.getAdditionalPrice()
                                                                ));

                                                                Utils.psLog("2 Added Attribute Table");
                                                            }
                                                        }
                                                    }


                                                    //Reading All Attribute
                                                    List<AttributeData> attributesAll = db.getAllAttributeDataByItemId(selectedItemId);
                                                    attributePriceOnly = 0.0;
                                                    selectedAttributeNameStr = "";
                                                    StringBuilder selectedAttributeNameBuilder = new StringBuilder();
                                                    for (AttributeData attributeData : attributesAll) {
                                                        Utils.psLog("Header Id : " + attributeData.getHeader_id() + ", Detail Id : "
                                                                + attributeData.getDetail_id() + ", Detail Name : " + attributeData.getDetail_name()
                                                                + ", Item Id : " + attributeData.getItem_id() + ", Att Price : " + attributeData.getAttribute_price());
                                                        attributePriceOnly += Float.parseFloat(attributeData.getAttribute_price());
                                                        //selectedAttributeNameStr += attributeData.getDetail_name() + ",";
                                                        selectedAttributeNameBuilder.append(attributeData.getDetail_name()).append(",");
                                                    }
                                                    selectedAttributeNameStr = selectedAttributeNameBuilder.toString();

                                                    //attributePriceOnly = Double.valueOf(String.format(Locale.US, "%.2f", attributePriceOnly));

                                                    String selectedAttrStr = getString(R.string.selected_attribute) + " : " + Utils.removeLastChar(selectedAttributeNameStr);
                                                    txtSelectedAttribute.setText(selectedAttrStr);


//                                                Utils.psLog("attributePriceOnly Before >>> " + attributePriceOnly);
//                                                if (Float.parseFloat(attribute.getAdditionalPrice()) != 0.0) {
//                                                    attributePriceOnly = Float.parseFloat(attribute.getAdditionalPrice());
//                                                }

                                                    additionalPrice = calculatedPrice + attributePriceOnly;

                                                    //additionalPrice = Double.valueOf(String.format(Locale.US, "%.2f", additionalPrice));

                                                    if (GlobalData.itemData != null) {
                                                        String priceStr = getString(R.string.price) +
                                                                Utils.format(additionalPrice) + GlobalData.itemData.currency_symbol + "(" + GlobalData.itemData.currency_short_form + ")";
                                                        txtPrice.setText(priceStr);
                                                    }

                                                    Utils.psLog("attributePriceOnly >> " + attributePriceOnly);
                                                    Utils.psLog("additionalPrice >> " + additionalPrice);
                                                    Utils.psLog("calculatedPrice >> " + calculatedPrice);

                                                    ObjectAnimator animY = ObjectAnimator.ofFloat(txtPrice, "translationX", -10f, 0f);
                                                    animY.setDuration(4000);
                                                    animY.setInterpolator(new BounceInterpolator());

                                                    animY.start();

                                                }
                                            }

                                        }


                                    });

                                    attributeContainer.addView(spin, params);


                                    if (isBasketItem() > 0) {

                                        Utils.psLog("Attr Inside Basket");
                                        if (keyId != -1) {
                                            Utils.psLog("Attr Key ID " + keyId);
                                            int posit = 0;
                                            BasketData basket = db.getBasketById(keyId);
                                            String attrIds = basket.getSelectedAttributeIds();

                                            if (attrIds != null) {
                                                Utils.psLog("Param Attr2 : " + attrIds);
                                                String[] attrIdsArray = attrIds.split("#");

                                                for (int ii = 0; ii < attributesData.length; ii++) {

                                                    for (int iii = 0; iii < attrIdsArray.length; iii++) {

                                                        Utils.psLog(ii + " : " + iii + "Attr : attrIdsArray " + attrIdsArray[iii] + " : itemAttributeData : " + attributesData[ii].getId());
                                                        if (attrIdsArray[iii].equals("" + attributesData[ii].getId())) {
                                                            posit = ii;
                                                            Utils.psLog("Attr : Position " + ii);
                                                            break;
                                                        }
                                                    }

                                                    if (posit != 0) {
                                                        break;
                                                    }

                                                }

                                                spin.setSelection(posit);
                                            }

                                        }
                                    }

                                }
                            }


                        }

                    } else {
                        attributeTitleLayout.setVisibility(View.GONE);
                    }
                }
                spinnerCreation = 1;
            }

        } catch (Exception e) {
            Utils.psErrorLog("bindAttribute", e);
        }

    }

    private void bindDescription() {
        try {
            if (GlobalData.itemData != null) {
                txtDescription.setText(GlobalData.itemData.description);
            }
        } catch (Exception e) {
            Utils.psErrorLog("bindDescription", e);
        }
    }

    private void bindFavourite(FloatingActionButton fab) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {
                final String URL = Config.APP_API_URL + Config.GET_FAVOURITE + GlobalData.itemData.id;

                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("shop_id", String.valueOf(getIntent().getIntExtra("selected_shop_id", 0)));
                getFavourite(URL, params, fab);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Favourite.", e);
        }
    }

    public void bindRate() {

        try {
            if (GlobalData.itemData != null) {
                String itemRatingCount = GlobalData.itemData.rating_count;
                setRatingBar.setRating(Float.parseFloat(itemRatingCount));
                String tmpRatingCount = "Total Rating : " + itemRatingCount;
                ratingCount.setText(tmpRatingCount);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bind Rating", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void getFavourite(final String postURL, final HashMap<String, String> params, final FloatingActionButton fab) {
        try {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success_status = response.getString("status");
                                String data_status = response.getString("data");
                                if (success_status.equals(jsonStatusSuccessString)) {
                                    if (data_status.equals("yes")) {

                                        isFavourite = true;
                                        fab.setImageResource(R.drawable.ic_favorite_white);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utils.psErrorLog("Error in getFavourite.", e);
                            } catch (Exception e) {
                                Utils.psErrorLog("Error in getFavourite.", e);
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

            // add the request object to the queue to be executed
            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);
        } catch (Exception e) {
            Utils.psErrorLog("getFavourite", e);

        }
    }


    private void requestData(String uri) {

        try {
            Utils.psLog("API URL : " + uri);

            CacheRequest cacheRequest = new CacheRequest(0, uri, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    try {
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        JSONObject jsonObject = new JSONObject(jsonString);

                        String status = jsonObject.getString("status");
                        if (status.equals(jsonStatusSuccessString)) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<PItemData>() {
                            }.getType();
                            GlobalData.itemData = gson.fromJson(jsonObject.getString("data"), listType);

                            if (GlobalData.itemData != null) {
                                bindData();
                            }

                        } else {

                            Utils.psLog("Error in loading.");
                        }

                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
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

            VolleySingleton.getInstance(this).addToRequestQueue(cacheRequest);
        }catch (Exception e) {
            Utils.psErrorLog("requestData", e);
        }
    }

    private void openGallery() {
        try {
            bundle = new Bundle();
            bundle.putParcelable("images", GlobalData.itemData);
            bundle.putString("from", "item");

            intent = new Intent(getApplicationContext(), GalleryActivity.class);
            intent.putExtra("images_bundle", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } catch (Exception e) {
            Utils.psErrorLog("openGallery", e);
        }
    }


    private void doSubmit(String postURL, HashMap<String, String> params, final String fromWhere) {
        try {
            JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                if (status.equals(jsonStatusSuccessString)) {

                                    if (fromWhere.equals("like")) {

                                        if (GlobalData.itemData != null) {
                                            GlobalData.itemData.like_count = response.getString("data");
                                            String likeCountStr = " " + GlobalData.itemData.like_count + " ";
                                            txtLikeCount.setText(likeCountStr);
                                        }
                                    }
                                } else {
                                    showFailPopup();
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

            // add the request object to the queue to be executed
            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);
        }catch (Exception e) {
            Utils.psErrorLog("doSubmit", e);
        }
    }

    private void showFailPopup() {
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.like_fail);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        } catch (Exception e) {
            Utils.psErrorLog("showFailPopup", e);
        }
    }

    private void showRatingFailPopup() {
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.rating_fail);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        } catch (Exception e) {
            Utils.psErrorLog("showRatingFailPopup", e);
        }
    }

    private void showCartEmpty() {
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.cart_empty);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        } catch (Exception e) {
            Utils.psErrorLog("showCartEmpty", e);
        }
    }

    private void ratingChanged(float rating) {

        try {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_RATING + selectedItemId;

            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("rating", String.valueOf(rating));
            params.put("shop_id", String.valueOf(pref.getInt("_id", 0)));

            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String success_status = response.getString("status");

                                if (success_status.equals(jsonStatusSuccessString)) {
                                    setRatingBar.setRating(Float.parseFloat(response.getString("data")));
                                    String tmpRatingCount = "Total Rating : " + response.getString("data");
                                    ratingCount.setText(tmpRatingCount);
                                } else {
                                    showRatingFailPopup();
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

            // add the request object to the queue to be executed
            req.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(req);

        } catch (Exception e) {
            Utils.psErrorLog("ratingChanged", e);
        }
    }

    private void showNeedLogin() {
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.login_required);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                    startActivity(intent);
                    Utils.psLog("OK clicked.");
                }
            });
            builder.show();
        } catch (Exception e) {
            Utils.psErrorLog("showNeedLogin", e);
        }
    }

    private void showRequiredQty() {
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.qty_required);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        } catch (Exception e) {
            Utils.psErrorLog("showRequiredQty", e);
        }
    }

    private void showRequiredDifferentQty() {
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.qty_different_required);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        } catch (Exception e) {
            Utils.psErrorLog("showRequiredDifferentQty", e);
        }
    }

    // Method to share either text or URL.
    private void shareTextUrl() {
        try {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            share.putExtra(Intent.EXTRA_TEXT, "http://codecanyon.net/user/panacea-soft/portfolio");

            startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            Utils.psErrorLog("shareTextUrl", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void doPhoneCall(View view) {
        try {
            Utils.psLog("Calling Phone : " + txtPhone.getText());
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txtPhone.getText()));
            startActivity(intent);
        } catch (SecurityException se) {
            Utils.psErrorLog("Error in calling phone. ", se);
        }
    }

    public void doEmail(View view) {
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{pref.getString("_email", "")});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (Exception e) {
            Utils.psErrorLog("doEmail", e);
        }
    }

    public void doInquiry(View view) {
        try {
            final Intent intent;
            intent = new Intent(this, InquiryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } catch (Exception e) {
            Utils.psErrorLog("doInquiry", e);
        }
    }

    public void doAddToCart() {

        try {
            if (editTextQty.getText().toString().matches("") || Integer.parseInt(editTextQty.getText().toString()) == 0) {

                showRequiredQty();

            } else {

                if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {

                    Utils.psLog("Calculated Price : " + calculatedPrice);

                    //Need to load Attribute Data from SQLite Tabel
                    //Reading All Attribute
                    List<AttributeData> attributesAll = db.getAllAttributeDataByItemId(selectedItemId);
                    String selectedAttributeName;
                    String selectedAttributeIds;

                    Utils.psLog("attributesAll size : " + attributesAll.size());

                    StringBuilder selectedAttributeIdsBuilder = new StringBuilder();
                    StringBuilder selectedAttributeNameBuilder = new StringBuilder();

                    for (AttributeData attributeData : attributesAll) {
                        Utils.psLog("ATC Header Id : " + attributeData.getHeader_id() + ", Detail Id : " + attributeData.getDetail_id() + ", Detail Name : " + attributeData.getDetail_name() + ", Item Id : " + attributeData.getItem_id());
//                    selectedAttributeIds = selectedAttributeIds + attributeData.getDetail_id() + "#";
//                    selectedAttributeName = selectedAttributeName + attributeData.getDetail_name() + ",";

                        selectedAttributeIdsBuilder.append(attributeData.getDetail_id()).append("#");
                        selectedAttributeNameBuilder.append(attributeData.getDetail_name()).append(",");
                    }
                    selectedAttributeIds = selectedAttributeIdsBuilder.toString();
                    selectedAttributeName = selectedAttributeNameBuilder.toString();

                    keyAttr = selectedAttributeIds;
                    //Delete All Records From Attribute Table
                    //db.deleteAllAttribute();


                    int dbKeyId = isBasketItem();
                    if (dbKeyId > 0) {
                        //Already Inside Basket

                        //Insert into Basket Table
                        BasketData basket = db.getBasketById(dbKeyId);

                        if (basket.getQty() != Integer.parseInt(editTextQty.getText().toString())) {

                            db.updateBasketByIds(new BasketData(
                                    GlobalData.itemData.id,
                                    GlobalData.itemData.shop_id,
                                    pref.getInt("_login_user_id", 0),
                                    GlobalData.itemData.name,
                                    GlobalData.itemData.description,
                                    String.valueOf(calculatedPrice + attributePriceOnly),
                                    GlobalData.itemData.discount_percent,
                                    Integer.parseInt(editTextQty.getText().toString()),
                                    GlobalData.itemData.images.get(0).path,
                                    GlobalData.itemData.currency_symbol,
                                    GlobalData.itemData.currency_short_form,
                                    Utils.removeLastChar(selectedAttributeName),
                                    Utils.removeLastChar(selectedAttributeIds)
                            ), dbKeyId, GlobalData.itemData.shop_id);
                            Utils.psLog("Update Basket");

                        } else {

                            showRequiredDifferentQty();

                        }


                    } else {
                        //New Item Insert Into Basket
                        Utils.psLog("selectedAttributeName : " + selectedAttributeName);
                        Utils.psLog("selectedAttributeName Removed : " + Utils.removeLastChar(selectedAttributeName));
                        db.addBasket(new BasketData(
                                GlobalData.itemData.id,
                                GlobalData.itemData.shop_id,
                                pref.getInt("_login_user_id", 0),
                                GlobalData.itemData.name,
                                GlobalData.itemData.description,
                                String.valueOf(calculatedPrice + attributePriceOnly),
                                GlobalData.itemData.discount_percent,
                                Integer.parseInt(editTextQty.getText().toString()),
                                GlobalData.itemData.images.get(0).path,
                                GlobalData.itemData.currency_symbol,
                                GlobalData.itemData.currency_short_form,
                                Utils.removeLastChar(selectedAttributeName),
                                Utils.removeLastChar(selectedAttributeIds)
                        ));
                        Utils.psLog("New Item Basket");
                    }
                    menuItem.setVisible(true);
                    updateCartBadgeCount();

                } else {
                    showNeedLogin();
                }

                // Reading All Basket Items
                List<BasketData> baskets = db.getAllBasketData();

                for (BasketData basketData : baskets) {

                    Utils.psLog(" id : " + basketData.getId() + ", item_id : " + basketData.getItemId() +
                            ", shop_id : " + basketData.getShopId() + " user_id : " + basketData.getUserId() +
                            ", name : " + basketData.getName() + ", desc : " + basketData.getDesc() + ", price : " + basketData.getUnitPrice() +
                            ", discount : " + basketData.getDiscountPercent() + ", qty : " + basketData.getQty() + ", image_path : " + basketData.getImagePath() +
                            ", currency_symbol : " + basketData.getCurrencySymbol() + ", currency_short_form : " + basketData.getCurrencyShortForm() +
                            ", attribute_name : " + basketData.getSelectedAttributeNames() + ", attribute_id : " + basketData.getSelectedAttributeIds());
                }

            }

        } catch (Exception e) {
            Utils.psErrorLog("doAddToCart", e);
        }

    }

    public void updateCartBadgeCount() {

        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                basketCount = db.getBasketCountByShopId(GlobalData.itemData.shop_id);
                if (basketCount > 0) {
                    menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
                }
            } else {
                menuItem.setVisible(false);
            }
        } catch (Exception e) {
            Utils.psErrorLog("updateCartBadgeCount", e);
        }
    }

    public void doReview(View view) {

        try {
            if (GlobalData.itemData != null) {
                ArrayList<PReviewData> itemReviewData = GlobalData.itemData.reviews;

                if (itemReviewData != null) {
                    if (itemReviewData.size() > 0) {
                        Intent intent = new Intent(this, ReviewListActivity.class);
                        intent.putExtra("selected_item_id", selectedItemId);
                        intent.putExtra("selected_shop_id", selectedShopId);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                    } else {
                        if (pref.getInt("_login_user_id", 0) != 0) {
                            Intent intent = new Intent(this, ReviewEntry.class);
                            intent.putExtra("selected_item_id", selectedItemId);
                            intent.putExtra("selected_shop_id", selectedShopId);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                        } else {
                            Intent intent = new Intent(this, UserLoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("doReview", e);
        }
    }

    public void doFavourite(View view) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {
                final String URL = Config.APP_API_URL + Config.POST_ITEM_FAVOURITE + GlobalData.itemData.id;
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("shop_id", String.valueOf(getIntent().getIntExtra("selected_shop_id", 0)));
                params.put("platformName", "android");
                doSubmit(URL, params, "favourite");
            } else {
                if (isFavourite) {
                    isFavourite = false;
                    fab.setImageResource(R.drawable.ic_favorite_border);
                } else {
                    isFavourite = true;
                    fab.setImageResource(R.drawable.ic_favorite_white);
                }
                showNeedLogin();
            }
        } catch (Exception e) {
            Utils.psErrorLog("Error in do favourite.", e);
        }
    }

    public void doLike(View view) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {
                final String URL = Config.APP_API_URL + Config.POST_ITEM_LIKE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("shop_id", String.valueOf(pref.getInt("_id", 0)));
                params.put("platformName", "android");


                doSubmit(URL, params, "like");
            } else {
                showNeedLogin();
            }
        } catch (Exception e) {
            Utils.psErrorLog("Error in do favourite.", e);
        }
    }

    public void doShare(View view) {

        shareTextUrl();

    }

    public int isBasketItem() {
        try {
            if (pref.getInt("_login_user_id", 0) != 0 && GlobalData.itemData != null) {

                Utils.psLog("Key ID " + db.getBasketIdByIdAndAttr(GlobalData.itemData.id, keyAttr));

                return db.getBasketIdByIdAndAttr(GlobalData.itemData.id, keyAttr);
            }
        } catch (Exception e) {
            Utils.psErrorLog("isBasketItem", e);
        }
        return 0;
    }

    public void doShopInfo(View view) {

        try {
            final Intent intent;
            intent = new Intent(this, ShopInfoActivity.class);
            intent.putExtra("selected_item_id", selectedItemId);
            intent.putExtra("selected_shop_id", selectedShopId);

            if (GlobalData.shopDatas != null) {

                for (PShopData pShopData : GlobalData.shopDatas) {
                    if (pShopData.id == selectedShopId) {
                        GlobalData.shopdata = pShopData;
                        break;
                    }
                }

            } else {
                Toast.makeText(this, "Can't find shop data.", Toast.LENGTH_SHORT).show();
            }

            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } catch (Exception e) {
            Utils.psErrorLog("doShopInfo", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

}