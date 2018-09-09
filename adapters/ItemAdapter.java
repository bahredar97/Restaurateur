package com.panaceasoft.restaurateur.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PItemData;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Panacea-Soft on 17/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PItemData> mDataset;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    public boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public Picasso p;
    private final int VIEW_ITEM = 1;
    private Activity activity;

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public ItemAdapter(Context context, final List<PItemData> myDataSet, RecyclerView recyclerView, Picasso p) {
        this.activity = (Activity) context;
        mDataset = myDataSet;
        this.p = p;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    try {
                        if (myDataSet != null) {
                            if (myDataSet.size() > 0) {
                                if (newState == 1) {
                                    totalItemCount = staggeredGridLayoutManager.getItemCount();

                                    // for staggeredGridLayoutManager
                                    int[] arr = new int[totalItemCount];
                                    int[] lastVisibleItem2 = staggeredGridLayoutManager.findLastVisibleItemPositions(arr);

                                    int greatestItem = 0;
                                    for (int aLastVisibleItem2 : lastVisibleItem2) {
                                        if (aLastVisibleItem2 > greatestItem) {
                                            greatestItem = aLastVisibleItem2;
                                        }
                                    }
                                    if (!loading && totalItemCount <= (greatestItem + visibleThreshold)) {
                                        // End has been reached
                                        // Do something
                                        if (onLoadMoreListener != null) {
                                            onLoadMoreListener.onLoadMore();
                                        }
                                        loading = true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Utils.psErrorLog("Error in scroll state change. ", e);
                    }

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = staggeredGridLayoutManager.getItemCount();

                    int greatestItem = 0;
                    if (totalItemCount == 1) {
                        greatestItem = 1;
                    } else {

                        try {
                            // for staggeredGridLayoutManager
                            int[] arr = new int[totalItemCount];
                            int[] lastVisibleItem2 = staggeredGridLayoutManager.findLastVisibleItemPositions(arr);

                            greatestItem = 0;
                            for (int aLastVisibleItem2 : lastVisibleItem2) {
                                if (aLastVisibleItem2 > greatestItem) {
                                    greatestItem = aLastVisibleItem2;
                                }

                            }

                        } catch (Exception e) {
                            Utils.psErrorLogE("In onScrolled ItemAdapter.", e);
                        }
                    }
                    if (!loading && totalItemCount <= (greatestItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return mDataset.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof MyViewHolder) {

            if (mDataset != null && mDataset.size() > 0) {

                ((MyViewHolder) holder).title.setText(mDataset.get(position).name);

                if (mDataset.get(position).images != null && mDataset.get(position).images.size() > 0) {

                    if (mDataset.get(position).images != null && mDataset.get(position).images.get(0).path != null) {

                        Utils.psLog(Config.APP_IMAGES_URL + mDataset.get(position).images.get(0).path);

//                        // Version 1
//                        p.load(Config.APP_IMAGES_URL + mDataset.get(position).images.get(0).path)
//                                .transform(new BitmapTransform(finalWidth, finalHeight))
//                                .placeholder(R.drawable.ps_icon)
//                                .into(((MyViewHolder) holder).icon);

                        MyViewHolder myViewHolder = (MyViewHolder) holder;

                        Utils.bindImage(myViewHolder.title.getContext(), p, myViewHolder.icon, mDataset.get(position).images.get(0), 2);

                    }
                }
                ((MyViewHolder) holder).likeCount.setText(mDataset.get(position).like_count);
                ((MyViewHolder) holder).reviewCount.setText(mDataset.get(position).review_count);

                ((MyViewHolder) holder).title.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
                ((MyViewHolder) holder).likeCount.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
                ((MyViewHolder) holder).reviewCount.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
                ((MyViewHolder) holder).icon.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            }

        } else {
            // For staggeredGridLayout Manager only
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else {
            return 0;
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void updateItemLikeAndReviewCount(int itemID, String likeCount, String reviewCount) {
        try {
            if (mDataset != null) {
                for (int i = 0; i < mDataset.size(); i++) {
                    if (mDataset.get(i).id == itemID) {
                        mDataset.get(i).like_count = likeCount;
                        mDataset.get(i).review_count = reviewCount;
                    }
                }
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Update Like and Review.", e);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;
        TextView likeCount;
        TextView reviewCount;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_name);
            icon = itemView.findViewById(R.id.item_image);
            likeCount = itemView.findViewById(R.id.like_count);
            reviewCount = itemView.findViewById(R.id.review_count);

            Context context = title.getContext();
            if (context != null) {
                title.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                likeCount.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                reviewCount.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }
}


