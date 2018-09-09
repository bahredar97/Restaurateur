package com.panaceasoft.restaurateur.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 9/4/17.
 * Contact Email : teamps.is.cool@gmail.com
 * Website : http://www.panacea-soft.com
 */

public class PAboutData implements Parcelable {

    public int id;
    public String title;
    public String description;
    public String email;
    public String phone;
    public String website;


    public ArrayList<PImageData> images;

    protected PAboutData(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        email = in.readString();
        phone = in.readString();
        website = in.readString();


        if (in.readByte() == 0x01) {
            images = new ArrayList<>();
            in.readList(images, PImageData.class.getClassLoader());
        } else {
            images = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(website);


        if (images == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(images);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PAboutData> CREATOR = new Parcelable.Creator<PAboutData>() {
        @Override
        public PAboutData createFromParcel(Parcel in) {
            return new PAboutData(in);
        }

        @Override
        public PAboutData[] newArray(int size) {
            return new PAboutData[size];
        }
    };

}
