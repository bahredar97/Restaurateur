package com.panaceasoft.restaurateur.utilities;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int toolbarHeight;
    private int showHeight;
    private int hideHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            this.toolbarHeight = Utils.getToolbarHeight(context);

            showHeight = -256 + (toolbarHeight + (toolbarHeight / 2));
            hideHeight = -(toolbarHeight * 2);
        } catch (Exception e) {
            Utils.psErrorLog("ScrollingFABBehavior", e);
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        try {
            if (dependency instanceof AppBarLayout) {

                // Show on Top
                boolean showOnTop = true;

                if (showOnTop) {
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {

                        if (dependency.getY() < showHeight) {
                            fab.setVisibility(View.VISIBLE);
                        } else if (dependency.getY() > hideHeight) {
                            if (fab.getVisibility() == View.VISIBLE) {
                                fab.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        fab.setVisibility(View.VISIBLE);
                    }
                } else {
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {

                        if (dependency.getY() > showHeight) {
                            fab.setVisibility(View.VISIBLE);
                        } else if (dependency.getY() <= hideHeight) {
                            if (fab.getVisibility() == View.VISIBLE) {
                                fab.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("onDependentViewChanged", e);
        }
        return true;
    }
}

