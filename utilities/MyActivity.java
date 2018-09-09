package com.panaceasoft.restaurateur.utilities;

import android.content.Context;

import com.rey.material.widget.Spinner;

/**
 * Created by Panacea-Soft on 26/6/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class MyActivity {

    private Context context;
    public MyActivity(Context context) {
        this.context = context;
    }

    public Spinner getSpinner() {
        return new Spinner(context);
    }

//    public ArrayAdapter getAdapter(int resId, String[] values) {
//        return new ArrayAdapter(context, resId, values);
//    }

}
