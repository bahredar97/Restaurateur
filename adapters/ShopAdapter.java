package com.panaceasoft.restaurateur.adapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.panaceasoft.restaurateur.GlobalData;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.ReservationActivity;
import com.panaceasoft.restaurateur.activities.SelectedShopActivity;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private Activity activity;
    private int lastPosition = -1;
    private List<PShopData> pShopDataList;
    private Picasso p;

    static class ShopViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout cv;
        TextView shopName;
        ImageView shopPhoto;
        TextView shopDesc;
        Button shopReservation;


        ShopViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.shop_cv);
            shopName = itemView.findViewById(R.id.shop_name);
            shopDesc = itemView.findViewById(R.id.shop_desc);
            shopPhoto = itemView.findViewById(R.id.shop_photo);
            shopReservation = itemView.findViewById(R.id.shop_reservation);

            Context context = cv.getContext();
            if(context != null) {
                shopName.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                shopDesc.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                shopReservation.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }

    public ShopAdapter(Context context, List<PShopData> cities, Picasso p) {
        this.activity = (Activity) context;
        this.pShopDataList = cities;
        this.p = p;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_row_container, parent, false);
        return new ShopViewHolder(v);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final ShopViewHolder holder, int position) {
        final PShopData shop = pShopDataList.get(position);
        holder.shopName.setText(shop.name);
        holder.shopName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        String descStr = shop.description.substring(0, Math.min(shop.description.length(), 150)) + "...";
        holder.shopDesc.setText(descStr);
        holder.shopDesc.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        holder.shopReservation.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

//        //Version 1
//        p.load(Config.APP_IMAGES_URL + shop.cover_image_file)
//                //.transform(new BitmapTransform(MAX_WIDTH, MAX_WIDTH))
//                .resize(MAX_WIDTH, MAX_WIDTH)
//                .onlyScaleDown()
//                .placeholder(R.drawable.ps_icon)
//                .into(holder.shopPhoto);

        Utils.bindImage(holder.itemView.getContext(), p, holder.shopPhoto, shop.cover_image_file, 1);

        setAnimation(holder.cv, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent intent;
                intent = new Intent(holder.itemView.getContext(), SelectedShopActivity.class);
                GlobalData.shopdata = shop;
                intent.putExtra("selected_shop_id", shop.id);
                holder.itemView.getContext().startActivity(intent);
                activity.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        });

        holder.shopReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent;
                intent = new Intent(holder.itemView.getContext(), ReservationActivity.class);
                GlobalData.shopdata = shop;
                intent.putExtra("selected_shop_id", shop.id);
                intent.putExtra("selected_shop_name", shop.name);
                holder.itemView.getContext().startActivity(intent);
                activity.overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (pShopDataList != null) {
            return pShopDataList.size();
        }
        return 0;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            @SuppressLint("PrivateResource") Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        } else {
            lastPosition = position;
        }
    }

}
