package com.panaceasoft.restaurateur.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.fragments.AboutFragment;
import com.panaceasoft.restaurateur.fragments.FavouritesListFragment;
import com.panaceasoft.restaurateur.fragments.NotificationFragment;
import com.panaceasoft.restaurateur.fragments.ProfileFragment;
import com.panaceasoft.restaurateur.fragments.ReservationListFragment;
import com.panaceasoft.restaurateur.fragments.SearchFragment;
import com.panaceasoft.restaurateur.fragments.ShopsListFragment;
import com.panaceasoft.restaurateur.fragments.ShopsMapFragment;
import com.panaceasoft.restaurateur.fragments.TransactionFragment;
import com.panaceasoft.restaurateur.fragments.UserForgotPasswordFragment;
import com.panaceasoft.restaurateur.fragments.UserLoginFragment;
import com.panaceasoft.restaurateur.fragments.UserRegisterFragment;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;


/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class MainActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private & Public Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private int currentMenuId = 0;
    private FABActions fabActions;
    private SharedPreferences pref;
    private FloatingActionButton fab;
    private SpannableString appNameString;
    private SpannableString profileString;
    private SpannableString registerString;
    private SpannableString forgotPasswordString;
    private SpannableString searchKeywordString;
    private SpannableString favouriteItemString;
    private SpannableString reservationString;
    //private Picasso p;
    public Fragment fragment = null;
    private SpannableString aboutString;
    private SpannableString notiSettingString;
    private SpannableString transactionString;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private & PublicVariables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUtils();

        initUI();

        initData();

        bindData();

        FirebaseMessaging.getInstance().subscribeToTopic("restaurateur");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Utils.psLog("OnActivityResult");
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    refreshProfileData();

                    if(data.getStringExtra("close_activity").equals("YES")){
                        finish();
                    }

                }
            } else if (requestCode == 0) { // for refresh favourite list

                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in main.", e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Do you really want to quit?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                                System.exit(0);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
            return true;
        }


    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    private void initUtils() {
        new Utils(this);
        VolleySingleton.getInstance(this);
        FirebaseAnalytics.getInstance(this);

        MobileAds.initialize(this, getResources().getString(R.string.banner_ad_unit_id));
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI(){
        initToolbar();
        initDrawerLayout();
        initNavigationView();
        initFAB();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout != null && toolbar != null) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };

            drawerLayout.addDrawerListener(drawerToggle);
            drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    drawerToggle.syncState();
                }
            });
        }
    }

    private void initNavigationView() {
        navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                            navigationMenuChanged(menuItem);
                            return true;
                        }
                    });
        }
    }

    private void initFAB() {
        fab = findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked(view);
            }
        });
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean notiFlag = getIntent().getBooleanExtra("show_noti", false);
            if (notiFlag) {
                savePushMessage(getIntent().getStringExtra("msg"));
                openFragment(R.id.nav_push_noti);
            } else {
                openFragment(R.id.nav_home);
            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in getting notification flag data.", e);
        }

        try {
            appNameString = Utils.getSpannableString(getApplicationContext(), getString(R.string.app_name));
            profileString = Utils.getSpannableString(getApplicationContext(), getString(R.string.profile));
            registerString = Utils.getSpannableString(getApplicationContext(), getString(R.string.register));
            forgotPasswordString = Utils.getSpannableString(getApplicationContext(), getString(R.string.forgot_password));
            searchKeywordString = Utils.getSpannableString(getApplicationContext(), getString(R.string.search_keyword));
            favouriteItemString = Utils.getSpannableString(getApplicationContext(), getString(R.string.favourite_item));
            reservationString = Utils.getSpannableString(getApplicationContext(), getString(R.string.my_reservation));
            aboutString = Utils.getSpannableString(getApplicationContext(), getString(R.string.about_app));
            notiSettingString = Utils.getSpannableString(this, getString(R.string.push_noti_setting));
            transactionString = Utils.getSpannableString(this, getString(R.string.transaction));

        }catch(Exception e){
            Utils.psErrorLogE("Error in init Data.", e);
        }

    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {

        toolbar.setTitle(appNameString);

        bindMenu();

    }

    // This function will change the menu based on the user is logged in or not.
    public void bindMenu() {
        if (pref.getInt("_login_user_id", 0) != 0) {
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, false);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, false);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void disableFAB() {
        fab.setVisibility(View.GONE);
    }

    private void enableFAB() {
        fab.setVisibility(View.VISIBLE);
    }

    private void updateFABIcon(int icon) {
        fab.setImageResource(icon);
    }

    public void updateFABAction(FABActions action) {
        fabActions = action;
    }

    private void navigationMenuChanged(MenuItem menuItem) {
        try {
            openFragment(menuItem.getItemId());
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
        }catch (Exception e) {
            Utils.psErrorLog("navigationMenuChanged", e);
        }
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    private void doLogout() {
        pref.edit().remove("_login_user_id").apply();
        pref.edit().remove("_login_user_name").apply();
        pref.edit().remove("_login_user_email").apply();
        pref.edit().remove("_login_user_about_me").apply();
        pref.edit().remove("_login_user_photo").apply();
        pref.edit().remove("_login_user_phone").apply();
        pref.edit().remove("_login_user_billing_address").apply();
        pref.edit().remove("_login_user_delivery_address").apply();
        bindMenu();

        openFragment(R.id.nav_home);
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void fabClicked(View view) {
        if (fabActions == FABActions.PROFILE) {
            final Intent intent;
            intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 1);
        }else if(fabActions == FABActions.SHOPLIST) {
            if(fragment instanceof ShopsListFragment) {
                ((ShopsListFragment)fragment).showSearchPopup();
            }
        }else if(fabActions == FABActions.SHOPMAP) {
            if(fragment instanceof ShopsMapFragment) {
                ((ShopsMapFragment)fragment).showFavPopup();
            }
        }
    }

    public void openFragment(int menuId) {

        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                //disableFAB();
                enableFAB();
                updateFABIcon(R.drawable.ic_search);

                String mapKey = "map";
                if (Config.START_MODE.equals(mapKey)) {
                    fragment = new ShopsMapFragment();
                    updateFABAction(FABActions.SHOPMAP);
                } else {
                    fragment = new ShopsListFragment();
                    updateFABAction(FABActions.SHOPLIST);
                }
                toolbar.setTitle(appNameString);

                break;
            case R.id.nav_home_map_login:
            case R.id.nav_home_map:
                enableFAB();
                updateFABIcon(R.drawable.ic_search);
                fragment = new ShopsMapFragment();
                updateFABAction(FABActions.SHOPMAP);
                break;
            case R.id.nav_profile:
            case R.id.nav_profile_login:
                if (pref.getInt("_login_user_id", 0) != 0) {
                    enableFAB();
                    updateFABIcon(R.drawable.ic_edit_white);
                    updateFABAction(FABActions.PROFILE);

                    fragment = new ProfileFragment();

                } else {
                    disableFAB();
                    fragment = new UserLoginFragment();
                }
                toolbar.setTitle(profileString);
                break;

            case R.id.nav_register:
                fragment = new UserRegisterFragment();
                toolbar.setTitle(registerString);
                break;

            case R.id.nav_forgot:
                fragment = new UserForgotPasswordFragment();
                toolbar.setTitle(forgotPasswordString);
                break;

            case R.id.nav_logout:
                doLogout();
                break;

            case R.id.nav_search_keyword:
            case R.id.nav_search_keyword_login:
                disableFAB();
                fragment = new SearchFragment();
                toolbar.setTitle(searchKeywordString);
                break;

            case R.id.nav_push_noti:
            case R.id.nav_push_noti_login:
                disableFAB();
                fragment = new NotificationFragment();
                toolbar.setTitle(notiSettingString);
                break;

            case R.id.nav_transaction:
                disableFAB();
                fragment = new TransactionFragment();
                toolbar.setTitle(transactionString);
                break;

            case R.id.nav_favourite_item_login:
                disableFAB();
                fragment = new FavouritesListFragment();
                toolbar.setTitle(favouriteItemString);
                break;

            case R.id.nav_my_reservation_login:
                disableFAB();
                fragment = new ReservationListFragment();
                Utils.psLog("Reservation Click");
                toolbar.setTitle(reservationString);
                break;

            case R.id.nav_about:
            case R.id.nav_about_login:
                disableFAB();
                fragment = new AboutFragment();
                toolbar.setTitle(aboutString);
                break;

            default:
                break;
        }

        if (currentMenuId != menuId && menuId != R.id.nav_logout) {
            currentMenuId = menuId;

            updateFragment(fragment);

            try {
                navigationView.getMenu().findItem(menuId).setChecked(true);
            } catch (Exception e) {
                Utils.psErrorLog("Error in find menu item. ", e);
            }
        }


    }

    // Neet to check
    public void refreshProfileData() {

        if (fragment instanceof ProfileFragment) {

            Utils.psLog("Refresh Data.");
            ((ProfileFragment) fragment).bindData();
        }
    }

    public void refreshProfile(){
        openFragment(R.id.nav_profile_login);
    }

    public void refreshNotification(){
        try {
            fragment = new NotificationFragment();

            updateFragment(fragment);
            if (pref.getInt("_login_user_id", 0) != 0) {
                currentMenuId = R.id.nav_push_noti_login;
            }else{
                currentMenuId = R.id.nav_push_noti;
            }

            navigationView.getMenu().findItem(currentMenuId).setChecked(true);
        } catch (Exception e) {
            Utils.psErrorLogE("Refresh Notification View Error. " , e);
        }

    }

    public void savePushMessage(String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("_push_noti_message", msg);
        editor.apply();

        Utils.psLog("push noti at mainactivity : " + msg);
    }

    public void showDownPicasso(){
//        try{
//            this.p.shutdown();
//        }catch(Exception e){
//            Utils.psErrorLogE("Error in Shutdown picasso.", e);
//        }

    }

//    public void loadProfileImage(String path) {
//
//        if(!path.equals("")){
//
//            p =new Picasso.Builder(this)
//                    .memoryCache(new LruCache(1))
//                    .build();
//
//            final String fileName = path;
//            Utils.psLog("file name : " + fileName);
//
//            Target target = new Target() {
//
//                @Override
//                public void onPrepareLoad(Drawable arg0) {
//                    Utils.psLog("Prepare Image to load.");
//                }
//
//                @Override
//                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Utils.psLog("inside onBitmapLoaded ");
//
//                    try {
//                        File file;
//                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                        File directory = cw.getDir("imageDir", Context.MODE_APPEND);
//                        file = new File(directory,fileName);
//                        FileOutputStream ostream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//                        ostream.close();
//                        Utils.psLog("Success Image Loaded.");
//
//                        refreshProfile();
//
//                        // After download finished the profile image
//                        // shutdown the Picasso threads
//                        showDownPicasso();
//
//                    } catch (Exception e) {
//                        Utils.psErrorLogE(e.getMessage(), e);
//                    }
//
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable arg0) {
//                    Utils.psLog("Fail Fail Fail");
//
//                }
//            };
//
//            Utils.psLog("profile photo : " + Config.APP_IMAGES_URL + path);
//
//            p.load(Config.APP_IMAGES_URL + path)
//                    .into(target);
//        }
//
//    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Enum
     **------------------------------------------------------------------------------------------------*/
    public enum FABActions {
        PROFILE,
        SHOPLIST,
        SHOPMAP
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Enum
     **------------------------------------------------------------------------------------------------*/

}
