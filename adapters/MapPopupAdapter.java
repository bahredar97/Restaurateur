package com.panaceasoft.restaurateur.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by Panacea-Soft on 26/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class MapPopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater;
    private HashMap<String, Uri> images;
    private HashMap<String, String> addressInfo;
    private int iconWidth;
    private int iconHeight;
    private Marker lastMarker=null;
    private Picasso p;

    public MapPopupAdapter(Context ctxt, LayoutInflater inflater,
                           HashMap<String, Uri> images, HashMap<String, String> addressInfo,Picasso p) {
        Context ctxt1 = ctxt;
        this.inflater = inflater;
        this.images = images;
        this.addressInfo = addressInfo;
        this.p = p;

        iconWidth=
                ctxt.getResources().getDimensionPixelSize(R.dimen.map_icon_width);
        iconHeight=
                ctxt.getResources().getDimensionPixelSize(R.dimen.map_icon_height);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup_marker, null);
        }

        if (lastMarker == null
                || !lastMarker.getId().equals(marker.getId())) {
            lastMarker=marker;

            TextView txtTitle = popup.findViewById(R.id.title);
            txtTitle.setText(marker.getTitle());

            TextView txtDescription =  popup.findViewById(R.id.snippet);
            txtDescription.setText(marker.getSnippet());

            TextView txtAddress = popup.findViewById(R.id.address);
            txtAddress.setText(addressInfo.get(marker.getId()));


            Context context = popup.getContext();
            if(context != null) {
                txtTitle.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtDescription.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtAddress.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }

            Uri image=images.get(marker.getId());
            ImageView icon = popup.findViewById(R.id.icon);
            if(image == null) {
                icon.setVisibility(View.GONE);
            } else {
                p.load(image).resize(iconWidth, iconHeight)
                        .centerCrop().noFade()
                        .placeholder(R.drawable.placeholder)
                        .into(icon, new MarkerCallback(marker));
            }

        }

        return(popup);
    }

    static class MarkerCallback implements Callback {
        Marker marker;

        MarkerCallback(Marker marker) {
            this.marker=marker;
        }

        @Override
        public void onError() {
            Utils.psLog(getClass().getSimpleName() +  "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.showInfoWindow();
            }
        }
    }


}
