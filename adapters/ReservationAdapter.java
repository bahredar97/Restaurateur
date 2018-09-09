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
import com.panaceasoft.restaurateur.models.PReservation;
import com.panaceasoft.restaurateur.utilities.Utils;

import java.util.List;

/**
 * Created by Panacea-Soft on 28/9/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>{

    private Activity activity;
    private int lastPosition = -1;
    private List<PReservation> reservationDataList;

    static class ReservationViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llReservationRow;
        TextView tvResevId;
        TextView tvResvDate;
        TextView tvResvTime;
        TextView tvResvStatus;
        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvUserPhone;
        TextView tvUserNote;


        ReservationViewHolder(View itemView) {
            super(itemView);
            llReservationRow = itemView.findViewById(R.id.ll_transaction_row);
            tvResevId = itemView.findViewById(R.id.tv_resv_id);
            tvResvDate = itemView.findViewById(R.id.tv_resv_date);
            tvResvTime = itemView.findViewById(R.id.tv_resv_time);
            tvResvStatus = itemView.findViewById(R.id.tv_resv_status);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserPhone = itemView.findViewById(R.id.tv_user_phone);
            tvUserNote = itemView.findViewById(R.id.tv_user_note);

            Context context = itemView.getContext();
            if(context != null) {
                tvResevId.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvResvDate.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvResvTime.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvResvStatus.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvUserName.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvUserEmail.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvUserPhone.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
                tvUserNote.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
            }
        }
    }


    public ReservationAdapter(Context context, List<PReservation> resvs){
        this.activity = (Activity) context;
        this.reservationDataList = resvs;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_row, parent, false);
        return new ReservationViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final ReservationViewHolder holder, int position) {
        final PReservation resvData = reservationDataList.get(position);
        String reservationIdStr = activity.getResources().getString(R.string.reservation_id) +" "+ String.valueOf(resvData.id);
        holder.tvResevId.setText(reservationIdStr);

        String reservationDateStr = activity.getResources().getString(R.string.reservation_date) +" " + resvData.resv_date;
        holder.tvResvDate.setText(reservationDateStr);

        String reservationTimeStr = activity.getResources().getString(R.string.reservation_time) + " " + resvData.resv_time;
        holder.tvResvTime.setText(reservationTimeStr);

        String reservationStatusStr = activity.getResources().getString(R.string.reservation_status) + " " + resvData.status;
        holder.tvResvStatus.setText(reservationStatusStr);

        String reservationUserNameStr = activity.getResources().getString(R.string.user_name) + " " + resvData.user_name;
        holder.tvUserName.setText(reservationUserNameStr);

        String reservationEmailStr = activity.getResources().getString(R.string.user_email) + " " + resvData.user_email;
        holder.tvUserEmail.setText(reservationEmailStr);

        String reservationPhoneStr = activity.getResources().getString(R.string.user_phone_no) + " " + resvData.user_phone_no;
        holder.tvUserPhone.setText(reservationPhoneStr);

        String reservationUserStr = activity.getResources().getString(R.string.user_note) + " " + resvData.note;
        holder.tvUserNote.setText(reservationUserStr);

        holder.tvResevId.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvResvDate.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvResvTime.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvResvStatus.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvUserName.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvUserEmail.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvUserPhone.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));
        holder.tvUserNote.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.fade_in));

        setAnimation(holder.llReservationRow, position);

    }

    @Override
    public int getItemCount() {

        if(reservationDataList != null) {
            return reservationDataList.size();
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
