package com.panaceasoft.restaurateur.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.BasketActivity;
import com.panaceasoft.restaurateur.models.BasketData;
import com.panaceasoft.restaurateur.utilities.DBHandler;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

/**
 * Created by Panacea-Soft on 2/7/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class BasketAdapter extends BaseAdapter implements ListAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<BasketData> basketData;
    private Context mContext;
    private int itemQty = 0;
    private int loginUserId;
    private DBHandler db;
    private Double totalAmount = 0.0;
    private int selectedShopId;
    private Picasso p;

    public BasketAdapter(Activity activity, List<BasketData> basketData, int loginUserId, DBHandler dbHandler, int shopId, Picasso p) {
        this.activity = activity;
        this.basketData = basketData;
        this.db = dbHandler;
        this.loginUserId = loginUserId;
        this.selectedShopId = shopId;
        mContext = this.activity.getApplicationContext();
        this.p = p;

    }

    @Override
    public int getCount() {
        if (basketData != null) {
            return basketData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return basketData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {

            final ViewHolder holder;
            if (inflater == null) {
                inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if (inflater != null) {
                convertView = inflater.inflate(R.layout.basket_row, parent, false);
            }

            if (convertView != null) {
                holder = new ViewHolder();

                holder.txtItemTitle = convertView.findViewById(R.id.item_title);

                holder.txtItemTitle.startAnimation(AnimationUtils.loadAnimation(parent.getContext(), R.anim.fade_in));


                holder.txtItemPrice = convertView.findViewById(R.id.item_price);

                holder.txtItemPrice.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

                holder.txtItemSubTotal = convertView.findViewById(R.id.item_sub_total);

                holder.txtItemSubTotal.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

                holder.txtAttr = convertView.findViewById(R.id.item_attr);

                holder.txtAttr.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

                holder.txtItemQty = convertView.findViewById(R.id.item_qty);

                holder.txtItemQty.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));


                Context context = parent.getContext();
                if (context != null) {
                    holder.txtItemTitle.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                    holder.txtItemPrice.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                    holder.txtItemSubTotal.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                    holder.txtAttr.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                    holder.txtItemQty.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                }

                holder.btnDelete = convertView.findViewById(R.id.delete_btn);
                holder.btnDelete.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        doDeleteItem(v, position);

                    }
                });


                holder.btnIncrease = convertView.findViewById(R.id.increase_btn);
                holder.btnIncrease.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
                holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemQty = db.getQTYByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId()) + 1;

                        String qtyStr = "QTY : " + " " + itemQty;
                        holder.txtItemQty.setText(qtyStr);
                        //String tmpItemSubTotal = "Sub Total : " + String.valueOf(String.format(Locale.US, "%.2f", (Float.valueOf(basketData.get(position).getUnitPrice()) * itemQty)) + basketData.get(position).getCurrencySymbol());
                        String tmpItemSubTotal = "Sub Total : " + Utils.format(Double.valueOf(basketData.get(position).getUnitPrice()) * itemQty) + basketData.get(position).getCurrencySymbol();
                        holder.txtItemSubTotal.setText(tmpItemSubTotal);

                        db.updateBasketByIds(new BasketData(
                                basketData.get(position).getItemId(),
                                basketData.get(position).getShopId(),
                                loginUserId,
                                basketData.get(position).getName(),
                                basketData.get(position).getDesc(),
                                String.valueOf(basketData.get(position).getUnitPrice()),
                                basketData.get(position).getDiscountPercent(),
                                itemQty,
                                basketData.get(position).getImagePath(),
                                basketData.get(position).getCurrencySymbol(),
                                basketData.get(position).getCurrencyShortForm(),
                                basketData.get(position).getSelectedAttributeNames(),
                                basketData.get(position).getSelectedAttributeIds()
                        ), basketData.get(position).getId(), basketData.get(position).getShopId());

                        refreshTotalAmount();

                    }
                });


                holder.btnDecrease = convertView.findViewById(R.id.decrease_btn);
                holder.btnDecrease.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
                holder.btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (db.getQTYByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId()) != 1) {

                            itemQty = db.getQTYByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId()) - 1;

                            String qtyStr = "QTY : " + " " + itemQty;
                            holder.txtItemQty.setText(qtyStr);
                            String tmpItemSubTotal = "Sub Total : " + String.valueOf(String.format(Locale.US, "%.2f", (Float.valueOf(basketData.get(position).getUnitPrice()) * itemQty)) + basketData.get(position).getCurrencySymbol());
                            holder.txtItemSubTotal.setText(tmpItemSubTotal);

                            db.updateBasketByIds(new BasketData(
                                    basketData.get(position).getItemId(),
                                    basketData.get(position).getShopId(),
                                    loginUserId,
                                    basketData.get(position).getName(),
                                    basketData.get(position).getDesc(),
                                    String.valueOf(basketData.get(position).getUnitPrice()),
                                    basketData.get(position).getDiscountPercent(),
                                    itemQty,
                                    basketData.get(position).getImagePath(),
                                    basketData.get(position).getCurrencySymbol(),
                                    basketData.get(position).getCurrencyShortForm(),
                                    basketData.get(position).getSelectedAttributeNames(),
                                    basketData.get(position).getSelectedAttributeIds()
                            ), basketData.get(position).getId(), basketData.get(position).getShopId());

                            refreshTotalAmount();
                        }

                    }
                });

                BasketData basket = basketData.get(position);

                holder.txtItemTitle.setText(basket.getName());

                String itemPriceStr = this.mContext.getResources().getString(R.string.price) + " " + Utils.format(Double.valueOf(basket.getUnitPrice())) + basket.getCurrencySymbol();
                holder.txtItemPrice.setText(itemPriceStr);

                double calcuatedSubTotal = Float.parseFloat(basket.getUnitPrice()) * basket.getQty();
                //calcuatedSubTotal = Double.valueOf(String.format(Locale.US, "%.2f", calcuatedSubTotal));
                //Float itemPrice = Float.parseFloat(basket.getUnitPrice());
                String itemSubTotalStr = this.mContext.getResources().getString(R.string.sub_total) + " " + Utils.format(calcuatedSubTotal) + basket.getCurrencySymbol();
                holder.txtItemSubTotal.setText(itemSubTotalStr);

                String attrString = this.mContext.getResources().getString(R.string.attribute) + " " + basket.selected_attribute_names;
                holder.txtAttr.setText(attrString);

                String itemQtyStr = this.mContext.getResources().getString(R.string.qty) + " " + basket.getQty();
                holder.txtItemQty.setText(itemQtyStr);
                itemQty = basket.getQty();

                final ImageView imgItemPhoto = convertView.findViewById(R.id.thumbnail);

                if (basket.getImagePath() != null) {

//                // Version 1
//                p.load(Config.APP_IMAGES_URL + basket.getImagePath())
//                        //.transform(new BitmapTransform(MAX_WIDTH, MAX_WIDTH))
//                        .resize(MAX_WIDTH, MAX_HEIGHT)
//                        .onlyScaleDown()
//                        .into(imgItemPhoto);

                    Utils.bindImage(parent.getContext(), p, imgItemPhoto, basket.getImagePath(), 2);

                }

                convertView.setId(basketData.get(position).id);
            }

        } catch (Exception e) {
            Utils.psErrorLog("Error in convert view.", e);
        }


        return convertView;
    }

    private void doDeleteItem(View v, final int position) {

        Utils.psLog("Delete Basket Item");
        new AlertDialog.Builder(v.getRootView().getContext())
                .setTitle(Utils.activity.getString(R.string.app_name))
                .setMessage(Utils.activity.getString(R.string.want_to_remove))
                .setIcon(android.R.drawable.ic_menu_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.deleteBasketByKeyIds(basketData.get(position).getId(), basketData.get(position).getShopId());
                        basketData.remove(position);
                        notifyDataSetChanged();
                        refreshTotalAmount();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }

    private void refreshTotalAmount() {
        try {
            List<BasketData> basket = db.getAllBasketDataByShopId(selectedShopId);
            for (BasketData basketData : basket) {
                totalAmount += basketData.getQty() * Float.parseFloat(basketData.getUnitPrice());
            }
//            totalAmount = Double.valueOf(String.format(Locale.US, "%.2f", totalAmount));
            ((BasketActivity) activity).updateTotalAmount(totalAmount);
            totalAmount = 0.0;
        } catch (Exception e) {
            Utils.psErrorLog("Error refreshTotalAmount.", e);
        }
    }

    private static class ViewHolder {
        private TextView txtItemTitle;
        private TextView txtItemPrice;
        private TextView txtItemSubTotal;
        private TextView txtItemQty;
        private Button btnDelete;
        private Button btnIncrease;
        private Button btnDecrease;
        private TextView txtAttr;
    }

}
