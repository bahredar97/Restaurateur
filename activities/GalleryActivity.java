package com.panaceasoft.restaurateur.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PAboutData;
import com.panaceasoft.restaurateur.models.PImageData;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.models.PNewsData;
import com.panaceasoft.restaurateur.uis.ExtendedViewPager;
import com.panaceasoft.restaurateur.uis.TouchImageView;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class GalleryActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private TextView txtImgDesc;
    private static ArrayList<PImageData> imageArray;


    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        initData();

        initUI();


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    class TouchImageAdapter extends PagerAdapter {

        Picasso p;

        private TouchImageAdapter(Picasso p){
            this.p = p;
        }

        @Override
        public int getCount() {
            if(imageArray != null) {
                return imageArray.size();
            }

            return 0;

        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {

            TouchImageView imgView = new TouchImageView(container.getContext());
            if(imageArray != null) {
                if (position >= imageArray.size()) {
                    position = position % imageArray.size();
                }

//                p.load(Config.APP_IMAGES_URL + imageArray.get(position).path)
//                        //.transform(new BitmapTransform(MAX_WIDTH, MAX_WIDTH))
//                        .resize(MAX_WIDTH, MAX_HEIGHT)
//                        .onlyScaleDown()
//                        .into(imgView);

                Utils.bindImage(container.getContext(), p, imgView,imageArray.get(position), 1);

                container.addView(imgView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            }
            return imgView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {
        try {

            Picasso p = new Picasso.Builder(this)
                    .memoryCache(new LruCache(1))
                    .build();

            ExtendedViewPager mViewPager = findViewById(R.id.view_pager);
            mViewPager.setAdapter(new TouchImageAdapter(p));
            txtImgDesc = findViewById(R.id.img_desc);

            if(imageArray != null){
                if(imageArray.size() > 0) {
                    txtImgDesc.setText(imageArray.get(0).description);
                }
            }else{
                txtImgDesc.setText("");
            }
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                public void onPageScrollStateChanged(int arg0) {

                }

                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                public void onPageSelected(int currentPage) {

                    if(imageArray != null) {
                        if (currentPage >= imageArray.size()) {
                            currentPage = currentPage % imageArray.size();
                        }

                        txtImgDesc.setText(imageArray.get(currentPage).description);
                    }
                    //currentPage is the position that is currently displayed.

                }

            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            Bundle bundle = getIntent().getBundleExtra("images_bundle");
            String from =bundle.getString("from");
            PItemData itemData;
            PNewsData newsData;
            PAboutData aboutData;
            if(from != null ) {
                switch (from) {
                    case "item":
                        itemData = bundle.getParcelable("images");
                        if (itemData != null) {
                            imageArray = itemData.images;
                        }
                        break;
                    case "about":
                        aboutData = bundle.getParcelable("images");
                        if(aboutData != null) {
                            imageArray = aboutData.images;
                        }
                        break;
                    default:
                        newsData = bundle.getParcelable("images");
                        if (newsData != null) {
                            imageArray = newsData.images;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

}
