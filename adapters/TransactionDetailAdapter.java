package com.panaceasoft.restaurateur.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.PTransactionData;
import com.panaceasoft.restaurateur.models.PTransactionDetailsData;
import com.panaceasoft.restaurateur.utilities.Utils;
import java.util.List;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class TransactionDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Activity activity;
    private List<PTransactionDetailsData> pTransactionDetailDataList;
    private PTransactionData pTransactionData;

    private static final int VIEW_ITEM = 1;
    private static final int VIEW_HEADER = 0;

    private class TransactionItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llTransactionRow;
        TextView tvItemName;
        TextView tvPrice;
        TextView tvQty;


        private TransactionItemViewHolder(View itemView) {
            super(itemView);
            llTransactionRow = itemView.findViewById(R.id.ll_transaction_row);
            tvItemName = itemView.findViewById(R.id.tv_transaction_id);
            tvPrice = itemView.findViewById(R.id.tv_total_amount);
            tvQty = itemView.findViewById(R.id.tv_status);

            Context context = llTransactionRow.getContext();
            if(context != null) {
                tvItemName.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvPrice.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvQty.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }

    private class TransactionHeaderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llTransactionRow;
        TextView tvTransactionId;
        TextView tvTotalAmount;
        TextView tvShippingAmount;
        TextView tvCouponDiscountAmount;
        TextView tvStatus;
        TextView tvPhone;
        TextView tvEmail;
        TextView tvBillingAddress;
        TextView tvDeliveryAddress;


        private TransactionHeaderViewHolder(View itemView) {
            super(itemView);
            llTransactionRow = itemView.findViewById(R.id.ll_transaction_row);
            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvShippingAmount = itemView.findViewById(R.id.tv_shipping_cost);
            tvCouponDiscountAmount = itemView.findViewById(R.id.tv_coupon_discount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvBillingAddress = itemView.findViewById(R.id.tv_billing_address);
            tvDeliveryAddress = itemView.findViewById(R.id.tv_deliver_address);

            Context context = llTransactionRow.getContext();

            if(context != null) {
                tvTransactionId.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvTotalAmount.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvShippingAmount.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvCouponDiscountAmount.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvStatus.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvPhone.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvEmail.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvBillingAddress.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvDeliveryAddress.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }

    public TransactionDetailAdapter(Context context, List<PTransactionDetailsData> pTransactionDetailDataList, PTransactionData pTransactionData){
        this.activity = (Activity) context;
        this.pTransactionDetailDataList = pTransactionDetailDataList;
        this.pTransactionData = pTransactionData;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if(viewType == VIEW_HEADER ) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_detail_row_header, parent, false);
            return new TransactionHeaderViewHolder(v);

        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_detail_row, parent, false);
            return new TransactionItemViewHolder(v);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {



        if(holder instanceof TransactionHeaderViewHolder){

            TransactionHeaderViewHolder tHolder = (TransactionHeaderViewHolder) holder;

            Double transactionTotalAmount = Double.valueOf(pTransactionData.total_amount) + Double.valueOf(pTransactionData.flat_rate_shipping) - Double.valueOf(pTransactionData.coupon_discount_amount);

            String transactionStr = activity.getResources().getString(R.string.transaction_id) +" "+ String.valueOf(pTransactionData.id);
            tHolder.tvTransactionId.setText(transactionStr);
            //tHolder.tvTotalAmount.setText(activity.getResources().getString(R.string.total_amount) +" " + String.format(Locale.US, "%.2f", transactionTotalAmount) + pTransactionData.currency_symbol);

            String totalAmountStr = activity.getResources().getString(R.string.total_amount) +" " + Utils.format(transactionTotalAmount) + pTransactionData.currency_symbol;
            tHolder.tvTotalAmount.setText(totalAmountStr);

            String shippingAmountStr = activity.getResources().getString(R.string.shipping_cost) +" : " + Utils.format(Double.valueOf(pTransactionData.flat_rate_shipping)) + pTransactionData.currency_symbol;
            tHolder.tvShippingAmount.setText(shippingAmountStr);

            String couponDiscountAmountStr = activity.getResources().getString(R.string.coupon_discount_amount) +" : " + Utils.format(Double.valueOf(pTransactionData.coupon_discount_amount)) + pTransactionData.currency_symbol;
            tHolder.tvCouponDiscountAmount.setText(couponDiscountAmountStr);

            String statusStr = activity.getResources().getString(R.string.transaction_status) + " " + pTransactionData.transaction_status;
            tHolder.tvStatus.setText(statusStr);

            String phoneStr = activity.getResources().getString(R.string.transaction_phone) + " " + pTransactionData.phone;
            tHolder.tvPhone.setText(phoneStr);

            String emailStr = activity.getResources().getString(R.string.transaction_email) + " " + pTransactionData.email;
            tHolder.tvEmail.setText(emailStr);

            String billingAddressStr = activity.getResources().getString(R.string.transaction_billing_address) + " " + pTransactionData.billing_address;
            tHolder.tvBillingAddress.setText(billingAddressStr);

            String deliveryAddressStr = activity.getResources().getString(R.string.transaction_deliver_address) + " " + pTransactionData.delivery_address;
            tHolder.tvDeliveryAddress.setText(deliveryAddressStr);

            tHolder.tvTransactionId.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvTotalAmount.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvShippingAmount.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvCouponDiscountAmount.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvStatus.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvPhone.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvEmail.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvBillingAddress.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvDeliveryAddress.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        }else{

            int cPosition = position  - 1;

            TransactionItemViewHolder tHolder = (TransactionItemViewHolder) holder;

            final PTransactionDetailsData transactionData = pTransactionDetailDataList.get(cPosition);
            //tHolder.tvItemName.setText(activity.getResources().getString(R.string.transaction_item_name) +" "+ String.valueOf(transactionData.item_name));

            if(transactionData.item_attribute.isEmpty()) {
                String itemNameStr = activity.getResources().getString(R.string.transaction_item_name) + " " + String.valueOf(transactionData.item_name);
                tHolder.tvItemName.setText(itemNameStr);
            } else {
                String itemNameStr = activity.getResources().getString(R.string.transaction_item_name) + " " + String.valueOf(transactionData.item_name) + "(" + String.valueOf(transactionData.item_attribute) + ")";
                tHolder.tvItemName.setText(itemNameStr);
            }

            String priceStr = activity.getResources().getString(R.string.transaction_item_price) +" " + Utils.format(Double.valueOf(transactionData.unit_price)) + " " + pTransactionData.currency_symbol;
            tHolder.tvPrice.setText(priceStr);

            String qtyStr = activity.getResources().getString(R.string.transaction_qty) + " " + transactionData.qty;
            tHolder.tvQty.setText(qtyStr);

            tHolder.tvItemName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvPrice.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
            tHolder.tvQty.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_HEADER: VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        if(pTransactionDetailDataList != null) {
            return pTransactionDetailDataList.size() + 1;
        }
        return 0;
    }


}
