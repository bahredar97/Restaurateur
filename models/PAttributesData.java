package com.panaceasoft.restaurateur.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 21/5/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PAttributesData implements Parcelable {
    public int id;
    public int shop_id;
    public String name;
    public String added;
    public String detailString;

    public ArrayList<PAttributesDetailsData> details;

    protected PAttributesData(Parcel in) {
        id = in.readInt();
        shop_id = in.readInt();
        name = in.readString();
        added = in.readString();
        detailString = in.readString();

        if (in.readByte() == 0x01) {
            details = new ArrayList<PAttributesDetailsData>();
            in.readList(details, PAttributesData.class.getClassLoader());
        } else {
            details = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(shop_id);
        dest.writeString(name);
        dest.writeString(added);
        dest.writeString(detailString);

        if (details == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(details);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PAttributesData> CREATOR = new Parcelable.Creator<PAttributesData>() {
        @Override
        public PAttributesData createFromParcel(Parcel in) {
            return new PAttributesData(in);
        }

        @Override
        public PAttributesData[] newArray(int size) {
            return new PAttributesData[size];
        }
    };
}
