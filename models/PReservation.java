package com.panaceasoft.restaurateur.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Panacea-Soft on 28/9/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PReservation implements Parcelable {

    public int id;
    public String resv_date;
    public String resv_time;
    public String note;
    public int shop_id;
    public int user_id;
    public String user_email;
    public String user_phone_no;
    public String user_name;
    public int status_id;
    public String added;
    public String status;

    protected PReservation(Parcel in) {
        id = in.readInt();
        resv_date = in.readString();
        resv_time = in.readString();
        note = in.readString();
        shop_id = in.readInt();
        user_id = in.readInt();
        user_email = in.readString();
        user_phone_no = in.readString();
        user_name = in.readString();
        status_id = in.readInt();
        added = in.readString();
        status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(resv_date);
        dest.writeString(resv_time);
        dest.writeString(note);
        dest.writeInt(shop_id);
        dest.writeInt(user_id);
        dest.writeString(user_email);
        dest.writeString(user_phone_no);
        dest.writeString(user_name);
        dest.writeInt(status_id);
        dest.writeString(added);
        dest.writeString(status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PReservation> CREATOR = new Parcelable.Creator<PReservation>() {
        @Override
        public PReservation createFromParcel(Parcel in) {
            return new PReservation(in);
        }

        @Override
        public PReservation[] newArray(int size) {
            return new PReservation[size];
        }
    };

}
