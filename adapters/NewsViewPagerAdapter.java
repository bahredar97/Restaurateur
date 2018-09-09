package com.panaceasoft.restaurateur.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.NewsDetailActivity;
import com.panaceasoft.restaurateur.models.PNewsData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 11/10/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class NewsViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<PNewsData> newsData;
    private Picasso p;

    public NewsViewPagerAdapter(Context mContext, ArrayList<PNewsData> newsData, Picasso p) {
        this.mContext = mContext;
        this.newsData = newsData;
        this.p = p;
    }

    @Override
    public int getCount() {

        if(newsData != null) {
            return newsData.size();
        }else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);

        if(newsData != null && newsData.size() >= position) {
            final PNewsData news = newsData.get(position);


            ImageView imageView = itemView.findViewById(R.id.img_pager_item);

            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Utils.psLog("Pager News title :  " + news.title);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("news", news);

                    Intent intent = new Intent(container.getContext(), NewsDetailActivity.class);
                    intent.putExtra("news_bundle", bundle);
                    container.getContext().startActivity(intent);

                }
            });


            if (news.images.get(0).path != null) {

//                p.load(Config.APP_IMAGES_URL + news.images.get(0).path)
//                        //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                        .resize(MAX_WIDTH, MAX_WIDTH)
//                        .onlyScaleDown()
//                        .into(imageView);

                Utils.bindImage(itemView.getContext(), p, imageView, news.images.get(0), 1);
            }

            TextView tvNewsTitle = itemView.findViewById(R.id.news_title);
            tvNewsTitle.setTypeface(Utils.getTypeFace(container.getContext(), Utils.Fonts.ROBOTO));
            if (news.title != null) {
                tvNewsTitle.setText(news.title);
            }

        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
