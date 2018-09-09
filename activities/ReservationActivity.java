package com.panaceasoft.restaurateur.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 25/9/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ReservationActivity extends AppCompatActivity{

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar;
    //private Button btnDatePicker, btnTimePicker;
    private EditText txtDate, txtTime, txtName, txtEmail, txtPhone, txtNote;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private SharedPreferences pref;
    private int selectedShopId;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private CoordinatorLayout mainLayout;
    private TextView txtSelected;
    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        initUI();
        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }


    @Override
    public void onDestroy() {

        try {
            toolbar = null;
            pref = null;
            prgDialog.cancel();
            prgDialog = null;

            Utils.unbindDrawables(mainLayout);
            mainLayout = null;
            super.onDestroy();

        } catch (Exception e){
            super.onDestroy();
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        try {
            initToolbar();
            initProgressDialog();
            Button btnDatePicker, btnTimePicker, btnSubmit;

            mainLayout = findViewById(R.id.coordinator_layout);
            mainLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            btnDatePicker = findViewById(R.id.btn_date);
            btnDatePicker.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            btnSubmit = findViewById(R.id.btn_reservation_submit);
            btnSubmit.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            btnTimePicker = findViewById(R.id.btn_time);
            btnTimePicker.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtDate = findViewById(R.id.in_date);
            txtDate.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtTime = findViewById(R.id.in_time);
            txtTime.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtName = findViewById(R.id.input_name);
            txtName.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtEmail = findViewById(R.id.input_email);
            txtEmail.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtPhone = findViewById(R.id.input_phone);
            txtPhone.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtNote = findViewById(R.id.input_additional_information);
            txtNote.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));

            txtSelected = findViewById(R.id.selected);
            txtSelected.setTypeface(Utils.getTypeFace(getApplicationContext(), Utils.Fonts.ROBOTO));


            btnDatePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(ReservationActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    String dateStr = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                    txtDate.setText(dateStr);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });

            btnTimePicker.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(ReservationActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    String timeStr = hourOfDay + ":" + minute;
                                    txtTime.setText(timeStr);
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(inputValidation()) {
                        Utils.psLog("Ready To Submit");

                        final String URL = Config.APP_API_URL + Config.POST_RESERVATION;
                        Utils.psLog(URL);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("resv_date", txtDate.getText().toString().trim());
                        params.put("resv_time", txtTime.getText().toString().trim());
                        params.put("note", txtNote.getText().toString().trim());
                        params.put("shop_id", String.valueOf(selectedShopId));
                        params.put("user_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                        params.put("user_email", txtEmail.getText().toString().trim());
                        params.put("user_phone_no", txtPhone.getText().toString().trim());
                        params.put("user_name", txtName.getText().toString().trim());
                        Utils.psLog(" Params " + params);
                        doSubmit(URL, params);


                    } else {
                        showFailPopup();
                    }
                }
            });

        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }
    }

    private void initToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.reservation_title);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initProgressDialog() {
        try {
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initProgressDialog.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void bindData() {
        try {
            selectedShopId = getIntent().getIntExtra("selected_shop_id",0);
            String selectedShopName = getIntent().getStringExtra("selected_shop_name");
            txtSelected.setText(selectedShopName);

            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            txtName.setText(pref.getString("_login_user_name", ""));
            txtEmail.setText(pref.getString("_login_user_email", ""));
            txtPhone.setText(pref.getString("_login_user_phone", ""));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLog("Error in initData,", e);
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
     private boolean inputValidation() {

         if(txtDate.getText().toString().equals("")) {
            return false;
         }

         if(txtTime.getText().toString().equals("")) {
             return false;
         }

         if(txtName.getText().toString().equals("")) {
             return false;
         }

         if(txtEmail.getText().toString().equals("")) {
             return false;
         }

         if(txtPhone.getText().toString().equals("")) {
             return false;
         }

         if(txtNote.getText().toString().equals("")) {
             return false;
         }

        return true;
     }

    private void showFailPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry_title);
        builder.setMessage(R.string.reserve_fail);
        builder.setPositiveButton(R.string.OK, null);
        builder.show();
    }

    public void showSuccessPopup() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.reservation_title);
        builder.setMessage(R.string.reservation_success);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Utils.psLog("OK clicked.");
            }
        });
        builder.show();
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    public void doSubmit(String URL, final HashMap<String, String> params) {
        prgDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //  pb.setVisibility(view.GONE);

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                Utils.psLog(status);

                                showSuccessPopup();

                            } else {
                                showFailPopup();
                                Utils.psLog("Error in loading.");
                            }

                            prgDialog.cancel();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Utils.psLog(error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }

            }
        });

        req.setShouldCache(false);
        VolleySingleton.getInstance(this).addToRequestQueue(req);

    }

}
