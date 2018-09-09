package com.panaceasoft.restaurateur.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.models.AttributeRowData;
import com.panaceasoft.restaurateur.utilities.Utils;

/**
 * Created by Panacea-Soft on 26/6/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class SpinAdapter extends ArrayAdapter<AttributeRowData> {

    private Context context;
    private AttributeRowData[] values;


    public SpinAdapter(Context context, int textViewResourceId, AttributeRowData[] values) {

        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;

    }

    public int getCount(){
        return values.length;
    }

    public AttributeRowData getItem(int position) {
        return values[position];
    }

    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(values[position].getName());

        label.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
        label.setTextSize(16);
        label.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        //label.setTextSize(context.getResources().getDimension(R.dimen.spinner_caption));
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {

        TextView label = new TextView(context);
        if(position == 0) {
            label.setTextColor(Color.BLACK);
        }
        label.setText(values[position].getName());
        label.setTextSize(16);
        label.setTypeface(Utils.getTypeFace(context, Utils.Fonts.ROBOTO));
        Context context = getContext();


        int space1 = Utils.dpToPx(context, 8);
        label.setPadding(space1, space1, space1, space1);

        return label;
    }

}
