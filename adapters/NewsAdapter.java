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
import com.panaceasoft.restaurateur.models.PNewsData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 13/8/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class NewsAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PNewsData> newsData;
    private Picasso p;
    private int MAX_WIDTH;

    public NewsAdapter(Activity activity, ArrayList<PNewsData> newsData, Picasso p) {
        this.activity = activity;
        this.newsData = newsData;
        this.p = p;
    }

    @Override
    public int getCount() {
        if (newsData != null) {
            return newsData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return newsData.get(position);
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
        if (convertView == null) {
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.news_row, parent, false);
            }
        }

        if (convertView != null) {
            if (MAX_WIDTH <= 0) {
                MAX_WIDTH = Utils.getScreenWidth(parent.getContext()) / 2;
            }

            TextView txtNewsTitle = convertView.findViewById(R.id.news_title);

            TextView txtMessage = convertView.findViewById(R.id.message);

            TextView txtAgo = convertView.findViewById(R.id.ago);


            Context context = parent.getContext();
            if (context != null) {
                txtNewsTitle.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtMessage.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                txtAgo.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }

            final ImageView imgNewsPhoto = convertView.findViewById(R.id.thumbnail);

            PNewsData news = newsData.get(position);

            txtNewsTitle.setText(news.title);
            txtNewsTitle.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

            String messageStr = news.description.substring(0, Math.min(news.description.length(), 120)) + "...";
            txtMessage.setText(messageStr);
            txtMessage.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

            txtAgo.setText(news.added);
            txtAgo.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));


            if (news.images.get(0).path != null) {

//            p.load(Config.APP_IMAGES_URL + news.images.get(0).path)
//                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .resize(MAX_WIDTH, MAX_WIDTH)
//                    .onlyScaleDown()
//                    .into(imgNewsPhoto);

                Utils.bindImage(context, p, imgNewsPhoto, news.images.get(0), 2);

                imgNewsPhoto.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

            }
        }
        return convertView;
    }
}
