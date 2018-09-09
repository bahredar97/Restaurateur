package com.panaceasoft.restaurateur.adapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PTransactionData;
import com.panaceasoft.restaurateur.utilities.Utils;

import java.util.List;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>  {
    private Activity activity;
    private int lastPosition = -1;
    private List<PTransactionData> pTransactionDataList;

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llTransactionRow;
        TextView tvTransactionId;
        TextView tvTotalAmount;
        TextView tvStatus;



        TransactionViewHolder(View itemView) {
            super(itemView);
            llTransactionRow = itemView.findViewById(R.id.ll_transaction_row);
            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);

            Context context = llTransactionRow.getContext();
            if(context != null) {
                tvTransactionId.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvTotalAmount.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvStatus.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }

    public TransactionAdapter(Context context, List<PTransactionData> cities){
        this.activity = (Activity) context;
        this.pTransactionDataList = cities;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_row, parent, false);
        return new TransactionViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final TransactionViewHolder holder, int position) {
        final PTransactionData transactionData = pTransactionDataList.get(position);

        Double transactionTotalAmount = Double.valueOf(transactionData.total_amount) + Double.valueOf(transactionData.flat_rate_shipping) - Double.valueOf(transactionData.coupon_discount_amount);

        String transactionIdStr = activity.getResources().getString(R.string.transaction_id) +" "+ String.valueOf(transactionData.id);
        holder.tvTransactionId.setText(transactionIdStr);
        //holder.tvTotalAmount.setText(activity.getResources().getString(R.string.total_amount) +" " + String.format(Locale.US, "%.2f", transactionTotalAmount) + transactionData.currency_symbol);

        String totalAmountStr = activity.getResources().getString(R.string.total_amount) +" " + Utils.format(transactionTotalAmount) + transactionData.currency_symbol;
        holder.tvTotalAmount.setText(totalAmountStr);

        String statusStr = activity.getResources().getString(R.string.transaction_status) + " " + transactionData.transaction_status;
        holder.tvStatus.setText(statusStr);

        holder.tvTransactionId.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvTotalAmount.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvStatus.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        setAnimation(holder.llTransactionRow, position);

    }

    @Override
    public int getItemCount() {

        if(pTransactionDataList != null) {
            return pTransactionDataList.size();
        }
        return 0;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            @SuppressLint("PrivateResource") Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }else{
            lastPosition = position;
        }
    }

}
