package com.panaceasoft.restaurateur.uis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.listeners.SelectListener;
import com.panaceasoft.restaurateur.models.PShopData;
import com.panaceasoft.restaurateur.utilities.Utils;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 7/25/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class PSPopupSingleSelectView extends LinearLayout {

    public SelectListener onSelectListener;
    private RelativeLayout mLayout;
    private TextView mTextView;
    private int selectedIndex = 0;
    private CharSequence[] items;
    private String title = "";
    private ArrayList<PShopData> pShopDatas;

    public PSPopupSingleSelectView(Context context) {
        super(context);
        Utils.psLog("1***");
        initUI(context);
    }

    public PSPopupSingleSelectView(Context context, String title, ArrayList<PShopData> pShopDatas, String s) {
        super(context, null);
        Utils.psLog("3***");
        this.title = title;
        setItemsWithPCityData(pShopDatas);
        initUI(context);

    }

    public PSPopupSingleSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Utils.psLog("4***");
        initUI(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UIPopup,
                0, 0);

        try {
            items = a.getTextArray(R.styleable.UIPopup_items);
            title = a.getString(R.styleable.UIPopup_pTitle);

        } finally {
            a.recycle();
        }
    }

    /**
     * Inflate the UI for the layout
     *
     * @param context for the view
     */
    private void initUI(Context context) {
        Utils.psLog("initUI" + context.toString());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ui_based_pop_up_chooser_view, this);
        onFinishInflateCustom();
    }


    protected void onFinishInflateCustom() {
        super.onFinishInflate();
        Utils.psLog("Inflate ***");
        mLayout = findViewById(R.id.mLayout);
        mTextView = findViewById(R.id.mText);

        if (!title.equals("")) {
            mTextView.setText(title);
        }

        mLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(pShopDatas != null && pShopDatas.size() > 0) {
//                        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
//                                .title(title)
//                                .items(items)
//                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
//                                    @Override
//                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                        /**
//                                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
//                                         * returning false here won't allow the newly selected radio button to actually be selected.
//                                         **/
//
//                                        mTextView.setText(text);
//                                        selectedIndex = which;
//
//                                        onSelectListener.Select(view, which, text);
//
//                                        if (pShopDatas != null && pShopDatas.size() > 0) {
//                                            onSelectListener.Select(view, which, text, pShopDatas.get(which).id);
//                                        }
//                                        return true;
//                                    }
//                                })
//                                .positiveText("Choose")
//                                .show();
//
//                        dialog.setSelectedIndex(selectedIndex);

                        new AlertDialog.Builder(getContext())
                                .setTitle(title)
                                .setSingleChoiceItems(items, selectedIndex, null)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                        String selectedText = (String)((AlertDialog)dialog).getListView().getItemAtPosition(selectedPosition);
                                        // Do something useful withe the position of the selected radio button

                                        Utils.psLog("Selected : "+ selectedPosition);
                                        mTextView.setText(selectedText);
                                        selectedIndex = selectedPosition;

                                        onSelectListener.Select(null, selectedPosition, selectedText);

                                        if (pShopDatas != null && pShopDatas.size() > 0) {
                                            onSelectListener.Select(null, selectedPosition, selectedText, pShopDatas.get(selectedPosition).id);
                                        }
                                    }
                                })

                                .show();

                    }else{
                        Toast.makeText(getContext(), "There is no city to select.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Utils.psErrorLogE("Error in Popup Dialog." , e);
                }
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CharSequence[] getItems() {
        return items;
    }

    public void setItems(CharSequence[] items) {
        this.items = items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public SelectListener getOnSelectListener() {
        return onSelectListener;
    }

    public void setOnSelectListener(SelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }



    public void setItemsWithPCityData(ArrayList<PShopData> pShopDatas) {
        int i = 0;
        this.pShopDatas = pShopDatas;
        try {
            this.items = new CharSequence[pShopDatas.size()];
            Utils.psLog("setup ***" + items.length);

            if(pShopDatas != null && pShopDatas.size() > 0){
                for (PShopData attDetail : pShopDatas) {
                    this.items[i++] = attDetail.name.toString();
                }
            }
        } catch (Exception e) {
            Utils.psLog("Error");
        }

    }
}
