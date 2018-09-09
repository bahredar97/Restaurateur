package com.panaceasoft.restaurateur.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.GalleryActivity;
import com.panaceasoft.restaurateur.models.PAboutData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Panacea-Soft on 9/4/17.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */

public class AboutFragment extends Fragment {

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     * *------------------------------------------------------------------------------------------------
     */
    private View view;
    private String jsonStatusSuccessString;
    private LinearLayout mainLayout;
    private List<PAboutData> about;
    private ImageView aboutImage;

    private Picasso p;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView aboutDescription;
    private TextView aboutEmail;
    private TextView aboutPhone;
    private TextView aboutWebsite;
    private Bundle bundle;
    private Intent intent;
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     * *------------------------------------------------------------------------------------------------
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about, container, false);

        initData();

        initUI();

        initCollapsingToolbarLayout();

        return view;
    }

    @Override
    public void onDestroy() {
        try {

            //p.shutdown();
            Utils.unbindDrawables(mainLayout);
            mainLayout = null;
            super.onDestroy();
        } catch (Exception e) {
            super.onDestroy();
        }
    }


    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     * *-----------------------------------------------------------------------------------------------
     */

    private void initData() {
        try {
            if (getContext() != null) {
                p = new Picasso.Builder(getContext())
                        .memoryCache(new LruCache(1))
                        .build();
            }
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            requestData(Config.APP_API_URL + Config.GET_ABOUT);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     * *------------------------------------------------------------------------------------------------
     */
    private void initUI() {
        mainLayout = this.view.findViewById(R.id.nav_about);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        aboutImage = this.view.findViewById(R.id.about_image);
        aboutDescription = this.view.findViewById(R.id.about_description);
        aboutEmail = this.view.findViewById(R.id.about_email);


        aboutPhone = this.view.findViewById(R.id.about_phone);
        aboutWebsite = this.view.findViewById(R.id.about_website);

    }

    private void initCollapsingToolbarLayout() {
        try {
            collapsingToolbar = this.view.findViewById(R.id.collapsing_toolbar);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**
     * ------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     * *------------------------------------------------------------------------------------------------
     */


    private void requestData(String uri) {
        Utils.psLog(" URL : " + uri);
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PAboutData>>() {
                                }.getType();
                                about = gson.fromJson(response.getString("data"), listType);

                                if (about != null) {

                                    if (about.size() > 0) {
                                        Utils.psLog(about.get(0).images.get(0).path);

//                                        p.load(Config.APP_IMAGES_URL + about.get(0).images.get(0).path)
//                                                //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                                                .resize(MAX_WIDTH, MAX_HEIGHT)
//                                                .onlyScaleDown()
//                                                .into(aboutImage);

                                        Utils.bindImage(getContext(), p, aboutImage, about.get(0).images.get(0) , 1);

                                        aboutImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                bundle = new Bundle();
                                                bundle.putParcelable("images", about.get(0));
                                                bundle.putString("from", "about");


                                                intent = new Intent(getContext(), GalleryActivity.class);
                                                intent.putExtra("images_bundle", bundle);
                                                startActivity(intent);

                                            }
                                        });

                                        if (collapsingToolbar != null) {
                                            collapsingToolbar.setTitle(about.get(0).title);
                                        }


                                        aboutDescription.setText(about.get(0).description);

                                        aboutPhone.setText(about.get(0).phone);
                                        aboutPhone.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + about.get(0).phone));
                                                    startActivity(intent);
                                                } catch (SecurityException se) {
                                                    Utils.psErrorLog("Error in calling phone. ", se);
                                                }
                                            }
                                        });

                                        aboutWebsite.setText(about.get(0).website);
                                        aboutWebsite.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_VIEW);
                                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                                intent.setData(Uri.parse(about.get(0).website));
                                                startActivity(intent);
                                            }
                                        });

                                        aboutEmail.setText(about.get(0).email);
                                        aboutEmail.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                emailIntent.setType("plain/text");
                                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{about.get(0).email});
                                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello");
                                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                            }
                                        });

                                    }
                                }
                            }

                        } catch (JSONException e) {
                            Utils.psErrorLog("Error in Getting About.", e);
                        } catch (Exception e) {
                            Utils.psErrorLog("Error in Getting About.", e);
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        try {
                            Utils.psLog(ex.getMessage());
                        }catch (Exception e) {
                            Utils.psErrorLog("onErrorResponse", e);
                        }
                    }
                });

        request.setShouldCache(false);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }


}
