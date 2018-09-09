package com.panaceasoft.restaurateur;

import android.app.Application;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class Config extends Application{

    public static final Double REGION_LAT = 1.352083;

    public static final Double REGION_LNG = 103.819836;

    public static final String START_MODE = "list"; //list or map

    public static final String STRIPE_CHECKOUT_NOTE = "Restaurateur Order From Android App.";

    public static final Boolean SHOW_ADMOB = true;

    public static final int PAGINATION = 3;

    public static final String DECIMAL_PLACES_FORMAT = "0.00"; // 0.00 = two decimal, 0.0 = one decimal, 0 = no decimal

    public static final String APP_API_URL = "http://www.panacea-soft.com/restaurateur-live/index.php";
    //public static final String APP_API_URL = "http://192.168.1.5:8888/restaurateur-admin/index.php";


    public static final String APP_IMAGES_URL = "http://www.panacea-soft.com/restaurateur-live/uploads/";
    //public static final String APP_IMAGES_URL = "http://192.168.1.5:8888/restaurateur-admin/uploads/";


    public static final String GET_ALL = "/rest/shops/get";

    public static final String ITEMS_BY_SUB_CATEGORY = "/rest/items/get/shop_id/";

    public static final String ITEMS_BY_ID = "/rest/items/get/id/";

    public static final String SEARCH_BY_GEO = "/rest/items/search_by_geo/miles/";

    public static final String POST_ITEM_INQUIRY = "/rest/items/inquiry/id/";

    public static final String POST_USER_LOGIN = "/rest/appusers/login";

    public static final String POST_REVIEW = "/rest/items/review/id/";

    public static final String POST_USER_REGISTER = "/rest/appusers/add/";

    public static final String PUT_USER_UPDATE = "/rest/appusers/update/id/";

    public static final String POST_ITEM_LIKE = "/rest/items/like/id/";

    public static final String POST_ITEM_FAVOURITE = "/rest/items/favourite/id/";

    public static final String POST_ITEM_SEARCH = "/rest/items/search/shop_id/";

    public static final String POST_TOUCH_COUNT = "/rest/items/touch/id/";

    public static final String POST_PROFILE_IMAGE = "/rest/images/upload";

    public static final String POST_ITEM_RATING = "/rest/items/rating/id/";

    public static final String POST_ITEM_IS_RATE = "/rest/items/is_rate/id/";

    public static final String GET_FAVOURITE = "/rest/items/is_favourite/id/";

    public static final String GET_LIKE = "/rest/items/is_like/id/";

    public static final String GET_FORGOT_PASSWORD = "/rest/appusers/reset/email/";

    public static final String GET_FAVOURITE_ITEMS = "/rest/items/user_favourites/user_id/";

    public static final String GET_SHOP_NEWS = "/rest/shops/feeds/shop_id/";

    public static final String POST_TRANSACTIONS = "/rest/transactions/add";

    public static final String GET_TRANSACTIONS = "/rest/transactions/user_transactions/user_id/";

    public static final String POST_COUPON_SEARCH = "/rest/coupons/search";

    public static final String POST_RESERVATION = "/rest/reservations/add";

    public static final String GET_RESERVATION = "/rest/reservations/get_all_reservation_by_user/user_id/";

    public static final String SHOP_SEARCH_BY_GEO = "/rest/shops/search_by_geo/miles/";

    public static final String SHOP_SEARCH_BY_KEYWORD = "/rest/shops/search_by_keyword";

    public static final String POST_FCM_REGISTER = "/rest/tokens/register";

    public static final String POST_FCM_UNREGISTER = "/rest/tokens/unregister";

    public static final String POST_STRIPE_TOKEN = "/rest/stripe/android_submit";

    public static final String GET_ABOUT = "/rest/abouts/index";
}
