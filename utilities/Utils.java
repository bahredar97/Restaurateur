package com.panaceasoft.restaurateur.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.models.PImageData;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.graphics.Matrix;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class Utils {

    public static MainActivity activity;
    private static Typeface fromAsset;
    private static Fonts currentTypeface;

    public Utils(Context context){
        activity = (MainActivity) context;
    }

    //public static DecimalFormat df = new DecimalFormat(Config.DECIMAL_PLACES_FORMAT);
    public static String format(double value){
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern(Config.DECIMAL_PLACES_FORMAT);
        return df.format(value);

    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    public static Point getScreenSize(){
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        try {
//            display.getSize(size);
//        }catch (NoSuchMethodError e) {
//            // For lower than api 11
//            size.x = display.getWidth();
//            size.y = display.getHeight();
//        }
//        return size;
//    }

    public static boolean isAndroid_5_0(){
        String version = Build.VERSION.RELEASE;
        if(version != "" && version != null){
            String[] versionDetail = version.split("\\.");
            Log.d("TEAMPS", "0 : " + versionDetail[0] + " 1 : " + versionDetail[1]);
            if(versionDetail[0].equals("5")){
                if(versionDetail[1].equals("0") || versionDetail[1].equals("00")){
                    return true;
                }
            }
        }

        return false;
    }

    public static void psLog(String log){
        Log.d("TEAMPS", log);
    }

    public static boolean isGooglePlayServicesOK(Activity activity) {

        final int GPS_ERRORDIALOG_REQUEST = 9001;

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, activity, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(activity, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static int getScreenHeight(Context context) {
//        Version 1 Code
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        return displaymetrics.heightPixels;

        if(context != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.heightPixels;
        }else {
            return 0;
        }
    }

    public static int getScreenWidth(Context context) {

//        Version 1 Code
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        return displaymetrics.widthPixels;

        if(context != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return displayMetrics.widthPixels;
        }else {
            return 0;
        }
    }

    public static void bindImage(Context context, Picasso p, ImageView imageView, PImageData imageData, int column) {

        // Screen Width
        int SCREEN_WIDTH = getScreenWidth(context);

        // Width Calculate on Column
        int MAX_WIDTH = SCREEN_WIDTH / column;

        // Final Image Size
        int finalWidth ;
        int finalHeight ;

        // Original Image Size
        int imageWidth = (int) imageData.width;
        int imageHeight = (int) imageData.height;

        if(imageWidth > MAX_WIDTH) {
            finalWidth = MAX_WIDTH;
            Float f = ((float)MAX_WIDTH / (float)imageWidth);
            finalHeight = (int) (f * imageHeight);
        }else {
            finalWidth = imageWidth;
            finalHeight = imageHeight;
        }

        if(finalHeight > 0 && finalWidth > 0 ) {
            p.load(Config.APP_IMAGES_URL + imageData.path)
                    .transform(new BitmapTransform(finalWidth, finalHeight))
                    .placeholder(R.drawable.ps_icon)
                    .into(imageView);
        }
    }

    public static void bindImage(Context context, Picasso p, ImageView imageView, String imagePath, int column) {

        // Screen Width
        int SCREEN_WIDTH = getScreenWidth(context);
        int SCREEN_HEIGHT = getScreenWidth(context);

        // Width Calculate on Column
        int finalWidth = SCREEN_WIDTH / column;
        int finalHeight = SCREEN_HEIGHT / column;

        if(finalHeight > 0 && finalWidth > 0 ) {
            p.load(Config.APP_IMAGES_URL + imagePath)
                    .transform(new BitmapTransform(finalWidth, finalHeight))
                    .placeholder(R.drawable.ps_icon)
                    .into(imageView);
        }
    }

    public static boolean isEmailFormatValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void saveBitmapImage(Context context, Bitmap b, String picName){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            Log.d("TEAMPS", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("TEAMPS", "io exception");
            e.printStackTrace();
        }

    }

    public static Bitmap loadBitmapImage(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            Log.d("TEAMPS", "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d("TEAMPS", "io exception");
            e.printStackTrace();
        }
        return b;
    }

    public static Typeface getTypeFace(Context context, Fonts fonts) {

        if(context == null) {
            return null;
        }

        if(currentTypeface == fonts) {
            if (fromAsset == null) {
                if(fonts == Fonts.NOTO_SANS) {
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf");
                }else if(fonts == Fonts.ROBOTO){
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                }
            }
        }else{
            if(fonts == Fonts.NOTO_SANS){
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf");
            }else if(fonts == Fonts.ROBOTO){
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            }else{
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            }

            //fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Italic.ttf");
            currentTypeface = fonts;
        }
        return fromAsset;
    }

    public static SpannableString getSpannableString(Context context, String str) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new PSTypefaceSpan("", Utils.getTypeFace(context, Fonts.ROBOTO)), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public enum Fonts{
        ROBOTO,
        NOTO_SANS
    }

    public static Bitmap getUnRotatedImage(String imagePath, Bitmap rotatedBitmap)
    {
        int rotate = 0;
        try
        {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix,
                true);
    }

    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[4].getLineNumber();
    }

    private static String getClassName(Object obj) {
        return ""+ ((Object) obj).getClass();
    }

    public static void psErrorLog(String log, Object obj){
        try {
            Log.d("TEAMPS", log);
            Log.d("TEAMPS", "Line : " + getLineNumber());
            Log.d("TEAMPS", "Class : " + getClassName(obj));
        }catch (Exception ee){
            Log.e("TEAMPS", "psErrorLog: ",ee );
        }
    }

    public static void psErrorLogE(String log, Exception e) {
        try {
            StackTraceElement l = e.getStackTrace()[0];
            Log.d("TEAMPS", log);
            Log.d("TEAMPS", "Line : " + l.getLineNumber());
            Log.d("TEAMPS", "Method : " + l.getMethodName());
            Log.d("TEAMPS", "Class : " + l.getClassName());
        }catch (Exception ee){
            Log.e("TEAMPS", "psErrorLogE: ",ee );
        }

    }

    public static String removeLastChar(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    public static Drawable buildCounterDrawable(int count, int backgroundImageId, Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.cart_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = view.findViewById(R.id.count);
            textView.setText(String.valueOf(count));

        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void unbindDrawables(View view) {
        try{
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }

                if(! (view instanceof AdapterView)) {
                    ((ViewGroup) view).removeAllViews();
                }
            }}catch (Exception e){
            Utils.psErrorLogE("Error in Unbind", e);
        }
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Utils.psLog("Permission is granted");
                return true;
            } else {
                Utils.psLog("Permission is revoked");
                ActivityCompat.requestPermissions( activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Utils.psLog("Permission is granted");
            return true;
        }
    }


}
