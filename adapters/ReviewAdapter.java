package com.panaceasoft.restaurateur.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PReviewData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 30/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ReviewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PReviewData> reviewData;
    private Picasso p;

    public ReviewAdapter(Activity activity, ArrayList<PReviewData> reviewData, Picasso p) {
        this.activity = activity;
        this.reviewData = reviewData;
        this.p = p;
    }

    @Override
    public int getCount() {
        if (reviewData != null) {
            return reviewData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return reviewData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            convertView = inflater.inflate(R.layout.review_row, parent, false);
        }
        
        if (convertView != null) {

            Context context = parent.getContext();
            TextView txtUserName = convertView.findViewById(R.id.user_name);


            TextView txtMessage = convertView.findViewById(R.id.message);


            TextView txtAgo = convertView.findViewById(R.id.ago);

            if (context != null) {
                txtUserName.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtMessage.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtAgo.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }

            final ImageView imgUserPhoto = convertView.findViewById(R.id.thumbnail);

            PReviewData review = reviewData.get(position);

            txtUserName.setText(review.appuser_name);
            txtUserName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

            txtMessage.setText(review.review);
            txtMessage.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

            txtAgo.setText(review.added);
            txtAgo.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

//            // VERSION 1
//            p.load(Config.APP_IMAGES_URL + review.profile_photo)
//                    .placeholder(R.drawable.ic_person_black)
//                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .resize(MAX_WIDTH, MAX_WIDTH)
//                    .onlyScaleDown()
//                    .into(imgUserPhoto);

            Utils.bindImage(context, p, imgUserPhoto, review.profile_photo, 2);

            imgUserPhoto.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        }
        return convertView;
    }
}
