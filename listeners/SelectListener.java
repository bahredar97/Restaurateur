package com.panaceasoft.restaurateur.listeners;

import android.view.View;

/**
 * Created by Panacea-Soft on 7/25/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public interface SelectListener {
    public void Select(View view, int position, CharSequence text);
    public void Select(View view, int position, CharSequence text, int id);
    public void Select(View view, int position, CharSequence text, int id, float additionalPrice);
}
