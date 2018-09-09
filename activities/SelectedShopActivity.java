package com.panaceasoft.restaurateur.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.adapters.CategoryAdapter;
import com.panaceasoft.restaurateur.adapters.NewsViewPagerAdapter;
import com.panaceasoft.restaurateur.listeners.ClickListener;
import com.panaceasoft.restaurateur.listeners.RecyclerTouchListener;
import com.panaceasoft.restaurateur.models.CategoryRowData;
import com.panaceasoft.restaurateur.models.PCategoryData;
import com.panaceasoft.restaurateur.models.PNewsData;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class SelectedShopActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView detailImage;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private List<CategoryRowData> categoryRowDataList = new ArrayList<>();
    private int selectedShopID;
    private PShopData pShop;
    private MenuItem menuItem;
    private int basketCount = 0;
    private DBHandler db = new DBHandler(this);
    private Picasso p;
    private CoordinatorLayout mainLayout;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private NewsViewPagerAdapter newAdapter;
    private CategoryAdapter mAdapter;
    private ArrayList<PNewsData> newsData;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_shop);

        initUI();

        initData();

        saveSelectedShopInfo(pShop);

        bindData();

        loadCategoryGrid();

        loadNewsSlider();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        menuItem = menu.findItem(R.id.action_basket);
        pShop = GlobalData.shopdata;
        selectedShopID = pShop.id;
        basketCount = db.getBasketCountByShopId(selectedShopID);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (pref.getInt("_login_user_id", 0) != 0) {
            if (basketCount > 0) {
                menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
            }
        } else {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Intent intent;
        if (id == R.id.action_basket) {
            if (basketCount > 0) {
                intent = new Intent(getApplicationContext(), BasketActivity.class);
                intent.putExtra("selected_shop_id", selectedShopID);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            } else {
                showCartEmpty();
            }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                if (data.getStringExtra("close_activity").equals("YES")) {
                    finish();
                }
                basketCount = db.getBasketCountByShopId(selectedShopID);

                if (basketCount > 0) {
                    menuItem.setIcon(Utils.buildCounterDrawable(basketCount, R.drawable.ic_shopping_cart_white, this));
                } else {
                    menuItem.setVisible(false);
                }
            }

        }

    }

    @Override
    protected void onDestroy() {
        try {

            mRecyclerView.addOnItemTouchListener(null);
            collapsingToolbar = null;
            toolbar = null;
            detailImage.setImageResource(0);
            detailImage = null;
            mLayoutManager = null;
            categoryRowDataList.clear();
            mAdapter = null;
            categoryRowDataList = null;

//            p.shutdown();
            Utils.unbindDrawables(mainLayout);

            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }

    }

    @Override
    public void onClick(View v) {
        Utils.psLog("Click Click Click >>>>> ");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if(dots != null) {
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
            }

            if(dots.length >= position) {
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initUI() {
        initToolbar();
        initCollapsingToolbarLayout();
        mainLayout = findViewById(R.id.coordinator_layout);
        p = new Picasso.Builder(this).build();
    }

    private void initCollapsingToolbarLayout() {
        try {
            collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
        }
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
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setTitle("");
            toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initData() {
        try {
            detailImage = findViewById(R.id.detail_image);
            pShop = GlobalData.shopdata;
            selectedShopID = pShop.id;
            newsData = pShop.feeds;

            Utils.psLog("News Title : " + pShop.feeds.get(0).title);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void bindData() {
        try {
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle("");
                makeCollapsingToolbarLayoutLooksGood(collapsingToolbar);
            }


            Utils.psLog("Shipping : " + pShop.flat_rate_shipping);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     * *------------------------------------------------------------------------------------------------
     */
    public void loadCategoryGrid() {
        try {
            mRecyclerView = findViewById(R.id.my_recycler_view);

            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new CategoryAdapter(this, categoryRowDataList, mRecyclerView, p);
            mRecyclerView.setAdapter(mAdapter);

            if (pShop != null) {
                ArrayList<PCategoryData> categoryArrayList = pShop.categories;
                for (PCategoryData cd : categoryArrayList) {

                    CategoryRowData info = new CategoryRowData();
                    info.setCatName(cd.name);
                    info.setCatImage(cd.cover_image_file);
                    categoryRowDataList.add(info);
                }
                if (categoryRowDataList != null) {
                    mAdapter.notifyItemInserted(categoryRowDataList.size());
                }
            }
            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    onItemClicked(position);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            mRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in loadCategoryGrid.", e);
        }
    }

    public void onItemClicked(int position) {
        final Intent intent;
        intent = new Intent(this, SubCategoryActivity.class);
        intent.putExtra("selected_category_index", position);
        intent.putExtra("selected_shop_id", selectedShopID);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public void loadNewsSlider() {
        ViewPager news_images = findViewById(R.id.pager_introduction);
        if (newsData.size() > 1) {

            detailImage.setVisibility(View.GONE);
            pager_indicator = findViewById(R.id.viewPagerCountDots);
            newAdapter = new NewsViewPagerAdapter(this, newsData, p);
            news_images.setAdapter(newAdapter);

            news_images.setCurrentItem(0);
            news_images.addOnPageChangeListener(this);

            setupSliderPagination();
            news_images.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        } else {

            news_images.setVisibility(View.GONE);

//            p.load(Config.APP_IMAGES_URL + pShop.cover_image_file)
//                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .resize(MAX_WIDTH, MAX_HEIGHT)
//                    .onlyScaleDown()
//                    .into(detailImage);

            Utils.bindImage(this, p, detailImage, pShop.cover_image_file, 1);

            detailImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        }
    }

    private void setupSliderPagination() {

        if (newAdapter != null) {
            dotsCount = newAdapter.getCount();
            if (dotsCount > 0) {
                try {
                    dots = new ImageView[dotsCount];

                    for (int i = 0; i < dotsCount; i++) {
                        dots[i] = new ImageView(this);
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        params.setMargins(4, 0, 4, 0);

                        pager_indicator.addView(dots[i], params);
                    }

                    dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                } catch (Exception e) {
                    Utils.psErrorLog("Error in dots. ", e);
                }
            }
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));
            ((TextPaint) tpf.get(object)).setColor(getResources().getColor(R.color.colorAccent));
        } catch (Exception ignored) {
        }
    }

    private void saveSelectedShopInfo(PShopData ct) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("_id", ct.id);
            editor.putString("_name", ct.name);
            editor.putString("_cover_image", ct.cover_image_file);
            editor.putString("_address", ct.address);
            editor.putString("_shop_region_lat", ct.lat);
            editor.putString("_shop_region_lng", ct.lng);
            editor.apply();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in saveSelectedShopInfo.", e);
        }
    }

    private void showCartEmpty() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.cart_empty);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

}
