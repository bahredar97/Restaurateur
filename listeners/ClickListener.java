package com.panaceasoft.restaurateur.listeners;

import android.view.View;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public interface ClickListener {
    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}