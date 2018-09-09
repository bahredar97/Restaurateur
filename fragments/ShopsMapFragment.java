package com.panaceasoft.restaurateur.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.SelectedShopActivity;
import com.panaceasoft.restaurateur.adapters.MapPopupAdapter;
import com.panaceasoft.restaurateur.listeners.GPSTracker;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.uis.ProgressWheel;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.rey.material.widget.Slider;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Panacea-Soft on 2/10/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ShopsMapFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    //private View view;
    private String jsonStatusSuccessString;
    private SpannableString connectionErrorString;
    private FrameLayout mainLayout;
    private Picasso p;
    private TextView display_message;
    private boolean checkingLatLng = false;
    private HashMap<String, Uri> markerImages = new HashMap<>();
    private HashMap<Marker, PShopData> markerInfo = new HashMap<>();
    private HashMap<String, String> markerAddress = new HashMap<>();
    private double selectedRegionLat;
    private double selectedRegionLng;
    private double currentLongitude;
    private double currentLatitude;
    private View marker;
    private MapView mMapView;
    private ArrayList<PShopData> shops;
    private Marker customMarker;
    private LatLng markerLatLng;
    private ProgressWheel progressWheel;
    private ViewGroup container;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Boolean hasPermission = false;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_shops_map, container, false);

        initData();

        initUI(view, inflater, container, savedInstanceState);

        requestPermission();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_goto_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_list) {
            /*Utils.psLog("Open Map");
            Fragment fragment = new ShopsListFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            //ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
            ft.commit();*/

            if(getActivity() != null) {
                ((MainActivity) getActivity()).openFragment(R.id.nav_home);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {

        try {

            progressWheel = null;
//            p.shutdown();
            Utils.unbindDrawables(mainLayout);
            GlobalData.shopdata = null;
            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - INit data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {

        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            connectionErrorString = Utils.getSpannableString(getContext(), getString(R.string.connection_error));

            if(getContext() != null) {
                p = new Picasso.Builder(getContext())
                        .memoryCache(new LruCache(1))
                        .build();
            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI(View v, LayoutInflater inflater, ViewGroup container,
                        Bundle savedInstanceState){
        this.container = container;

        if (Utils.isGooglePlayServicesOK(getActivity())) {
            Utils.psLog("Google Play Service is ready for Google Map");


            initMessage(v);
            loadPreferenceData();

            loadMap(v, savedInstanceState, inflater, container);
            mainLayout = v.findViewById(R.id.main_layout);

            System.gc();
            //mMapView.getMap().clear();
        } else {
            showNoServicePopup();
        }
    }

    public void showFavPopup(){
        if(hasPermission){

            if (checkingLatLng) {
                if (readyLatLng()) {
                    showSearchPopup();
                } else {
                    showWaitPopup();
                }
            } else {

                showSearchPopup();

            }
        }else {
            requestPermission();
        }
    }

    private void initMessage(View v) {
        display_message = v.findViewById(R.id.display_message);
        display_message.setVisibility(View.GONE);

        progressWheel = v.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);
    }

    private void loadMap(View v, Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        try {

            mMapView = v.findViewById(R.id.mapView);

            ViewGroup.LayoutParams params = mMapView.getLayoutParams();
            params.height = Utils.getScreenHeight(container.getContext());
            mMapView.setLayoutParams(params);

            mMapView.onCreate(savedInstanceState);

            mMapView.onResume();

            try {
                MapsInitializer.initialize(container.getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            marker = inflater.inflate(R.layout.custom_marker, container, false);

            //requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/sub_cat_id/" + selectedSubCatId + "/item/all/", marker);
            //requestData(Config.APP_API_URL + Config.GET_ALL, marker);
            loadData(marker);
        }catch (Exception e){
            Utils.psErrorLog("Error in Load Map.", e);
        }

    }

    private void loadPreferenceData() {
        try {
            selectedRegionLat = Config.REGION_LAT;
            selectedRegionLng = Config.REGION_LNG;
        }catch (Exception e){
            Utils.psErrorLog("Error in load preference data. ", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Current Address : \n");
                Utils.psLog("Getting Address.");
                int AdCount = returnedAddress.getMaxAddressLineIndex();
                String tmpAddress = "";
                StringBuilder tmpAddressBuilter = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {

                    if(!tmpAddress.equals("")){
                        strReturnedAddress.append("\n");
                    }
                    try {
                        if (i != returnedAddress.getMaxAddressLineIndex() - 1) {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i));
                        } else {
                            strReturnedAddress.append(returnedAddress.getAddressLine(i));
                        }

                        //tmpAddress += returnedAddress.getAddressLine(i);
                        tmpAddressBuilter.append(returnedAddress.getAddressLine(i));
                        tmpAddress = tmpAddressBuilter.toString();
                    }catch (Exception e){
                        Utils.psErrorLog("Error in getting address", e);
                    }

                }



                if(AdCount == 0 && tmpAddress.equals("")){
                    tmpAddress = returnedAddress.getAdminArea();
                    if (tmpAddress != null) {
                        if(!tmpAddress.equals("")) {
                            strReturnedAddress.append(tmpAddress);
                        }
                    }else {
                        tmpAddress = "";
                    }


                }
                if(AdCount == 0 && tmpAddress.equals("")){
                    tmpAddress = returnedAddress.getLocality();
                    strReturnedAddress.append(tmpAddress);
                }

                strAdd = strReturnedAddress.toString();
                Utils.psLog("My loction address --- " + "" + strReturnedAddress.toString());
            } else {
                Utils.psLog("My Current loction address" + "No Address returned!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utils.psLog("My Current loction address >>" + e.getMessage());
        }
        return strAdd;
    }

    private void requestData(String uri, final View marker) {

        Utils.psLog(" URI " + uri);
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if(progressWheel != null) {
                                    progressWheel.setVisibility(View.GONE);
                                }
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PShopData>>() {
                                }.getType();
                                shops = gson.fromJson(response.getString("data"), listType);

                                        mMapView.getMapAsync(new OnMapReadyCallback() {

                                            @Override
                                            public void onMapReady(final GoogleMap googleMap) {
                                                googleMap.clear();
                                                if(shops != null) {

                                                    updateGlobalShopList();

                                                    if(shops.size() >= 1) {

                                                        for (PShopData sh : shops) {

                                                            if (sh != null) {

                                                                try {
                                                                    double latitude = Double.parseDouble(sh.lat);
                                                                    double longitude = Double.parseDouble(sh.lng);

                                                                    Utils.psLog("Lat : " + latitude + " Lng : " + longitude);

                                                                    markerLatLng = new LatLng(latitude, longitude);

                                                                    customMarker = googleMap.addMarker(new MarkerOptions()
                                                                            .position(markerLatLng)
                                                                            .title(sh.name)
                                                                            .snippet(sh.description.substring(0, Math.min(sh.description.length(), 80)) + "...")
                                                                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker)))
                                                                            .anchor(0.5f, 1));
                                                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                                                            .target(new LatLng(selectedRegionLat, selectedRegionLng)).zoom(10).build();
                                                                    googleMap.animateCamera(CameraUpdateFactory
                                                                            .newCameraPosition(cameraPosition));


                                                                    if (markerImages != null) {
                                                                        markerImages.put(customMarker.getId(), Uri.parse(Config.APP_IMAGES_URL + sh.cover_image_file));
                                                                    }

                                                                    if (markerInfo != null) {
                                                                        markerInfo.put(customMarker, sh);
                                                                    }

                                                                    if (markerAddress != null) {
                                                                        markerAddress.put(customMarker.getId(), sh.address);
                                                                    }

                                                                    if(getActivity() != null) {
                                                                        googleMap.setInfoWindowAdapter(new MapPopupAdapter(getActivity(), getActivity().getLayoutInflater(), markerImages, markerAddress, p));
                                                                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                                            @Override
                                                                            public boolean onMarkerClick(Marker marker) {
                                                                                marker.showInfoWindow();
                                                                                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                                                                return true;
                                                                            }
                                                                        });

                                                                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                                            @Override
                                                                            public void onInfoWindowClick(Marker marker) {

                                                                                PShopData ct = markerInfo.get(marker);
                                                                                Utils.psLog("Selected Item Name : " + ct.name);
                                                                                final Intent intent;
                                                                                intent = new Intent(getActivity(), SelectedShopActivity.class);
                                                                                GlobalData.shopdata = ct;

                                                                                intent.putExtra("selected_shop_id", ct.id);
                                                                                getActivity().startActivity(intent);
                                                                                getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

                                                                            }
                                                                        });
                                                                    }
                                                                }catch(Exception e){
                                                                    Utils.psErrorLog("", e);
                                                                }
                                                            }
                                                        }

                                                    } else {
                                                        showNotFoundPopup();
                                                        googleMap.clear();

                                                    }
                                                }

                                            }
                                        });

                            } else {

                                Utils.psLog("Error in loading.");

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                },


                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        try {
                            Log.d(">> Volley Error ", ex.getMessage() + "");
                            if (progressWheel != null) {
                                progressWheel.setVisibility(View.GONE);
                            }

                            NetworkResponse response = ex.networkResponse;
                            if (response == null || response.data == null) {
                                display_message.setVisibility(View.VISIBLE);
                                display_message.setText(connectionErrorString);
                            }
                        }catch ( Exception e) {
                            Utils.psErrorLog("onErrorResponse", e);
                        }

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setShouldCache(false);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void requestPermission() {
        if(getActivity() != null) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                        .requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                hasPermission = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                    hasPermission = true;
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                    hasPermission = false;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                hasPermission = false;
        }
    }

    private void loadData(final View marker) {
        try {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {

                    if(GlobalData.shopDatas.size() > 1) {

                        for (PShopData sh : GlobalData.shopDatas) {

                            if (sh != null) {
                                double latitude = Double.parseDouble(sh.lat);
                                double longitude = Double.parseDouble(sh.lng);

                                markerLatLng = new LatLng(latitude, longitude);

                                try {
                                    customMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(markerLatLng)
                                            .title(sh.name)
                                            .snippet(sh.description.substring(0, Math.min(sh.description.length(), 80)) + "...")
                                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker)))
                                            .anchor(0.5f, 1));
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(selectedRegionLat, selectedRegionLng)).zoom(10).build();
                                    googleMap.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(cameraPosition));


                                if (markerImages != null) {
                                    markerImages.put(customMarker.getId(), Uri.parse(Config.APP_IMAGES_URL + sh.cover_image_file));
                                }

                                if (markerInfo != null) {
                                    markerInfo.put(customMarker, sh);
                                }

                                if (markerAddress != null) {
                                    markerAddress.put(customMarker.getId(), sh.address);
                                }

                                if(getActivity() != null) {
                                    googleMap.setInfoWindowAdapter(new MapPopupAdapter(getActivity(), getActivity().getLayoutInflater(), markerImages, markerAddress, p));
                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            marker.showInfoWindow();
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                            return true;
                                        }
                                    });

                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {

                                            PShopData ct = markerInfo.get(marker);
                                            Utils.psLog("Selected Item Name : " + ct.name);
                                            final Intent intent;
                                            intent = new Intent(getActivity(), SelectedShopActivity.class);
                                            GlobalData.shopdata = ct;

                                            intent.putExtra("selected_shop_id", ct.id);
                                            getActivity().startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);

                                        }
                                    });
                                }
                                }catch (Exception e){
                                    Utils.psErrorLog("loadData", e);
                                }


                            }
                        }


                    } else {
                        Utils.psLog("Need to call API");
                        requestData(Config.APP_API_URL + Config.GET_ALL, marker);
                    }


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.psLog("loadData Function  >>" + e.getMessage());

        }

    }

    private void updateGlobalShopList() {
        GlobalData.shopDatas.clear();
        GlobalData.shopDatas.addAll(shops);
//        for (PShopData cd : shops) {
//            GlobalData.shopDatas.add(cd);
//        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/
    public void showSearchPopup() {
        checkingLatLng = false;

        if(getActivity() != null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.location_search_title);
            LayoutInflater inflater = (getActivity()).getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.slider, container, false);
            dialogBuilder.setView(dialogView);

            Button btnSearch = dialogView.findViewById(R.id.button_search);
            btnSearch.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnSearch.setTextColor(getResources().getColor(R.color.whiteColor));
            final TextView addressTextView = dialogView.findViewById(R.id.complete_address);
            getCurrentLocation(addressTextView);
            final Slider slider = dialogView.findViewById(R.id.location_slider);

            final AlertDialog alert = dialogBuilder.create();
            alert.show();
            btnSearch.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    alert.dismiss();

                    if (slider != null) {
                        Utils.psLog(String.valueOf(slider.getValue()));
                        Utils.psLog(Config.APP_API_URL + Config.SHOP_SEARCH_BY_GEO + slider.getValue() + "/userLat/" + currentLatitude + "/userLong/" + currentLongitude);
                        requestData(Config.APP_API_URL + Config.SHOP_SEARCH_BY_GEO + slider.getValue() + "/userLat/" + currentLatitude + "/userLong/" + currentLongitude, marker);
                    }

                }
            });
        }

    }

    public void showWaitPopup() {
        if(getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.pls_wait);
            builder.setMessage(R.string.gps_not_ready);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        }
    }

    public void showNoServicePopup() {
        if(getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.no_google_play);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        }
    }

    public void showNotFoundPopup() {
        if(getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry_title);
            builder.setMessage(R.string.result_not_found);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        }
    }

    public boolean readyLatLng() {
        GPSTracker gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();
            return currentLatitude != 0.0 && currentLongitude != 0.0;

        } else {
            return false;
        }

    }

    public void getCurrentLocation(TextView tv) {

        GPSTracker gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
            checkingLatLng = true;
        }

        tv.setText(getCompleteAddressString(currentLatitude, currentLongitude));

    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        Bitmap bitmap = null;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            view.destroyDrawingCache();
        }catch (Exception e){
            Utils.psErrorLog("createDrawableFromView", e);
        }
        return bitmap;
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/
}
