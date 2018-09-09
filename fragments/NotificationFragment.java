package com.panaceasoft.restaurateur.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.utilities.Utils;

import android.app.ProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class NotificationFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private View view;
    private ToggleButton tgNoti;
    private String regId = "";
    private SharedPreferences pref;
    RequestParams params = new RequestParams();
    ProgressDialog prgDialog;
    private String serviceNotAvaiString;
    private String jsonStatusSuccessString;
    private String gcmRegisterSuccessString;
    private String gcmUnregisterSuccessString;
    private String gcmCannotConnectString;
    private String gcmSomethingWrongString;
    private String gcmRegisterNotSuccessString;
    private String gcmRequestNotFoundString;

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getContext() != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            view = inflater.inflate(R.layout.fragment_notification, container, false);
        }

        initData();

        initUI();

        return view;
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        try {

            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            serviceNotAvaiString = getResources().getString(R.string.service_not_available);
            gcmRegisterSuccessString = getResources().getString(R.string.gcm_register_success);
            gcmUnregisterSuccessString = getResources().getString(R.string.gcm_unregister_success);
            gcmRegisterNotSuccessString = getResources().getString(R.string.gcm_register_not_success);
            gcmRequestNotFoundString = getResources().getString(R.string.request_not_found);
            gcmSomethingWrongString = getResources().getString(R.string.something_wrong);
            gcmCannotConnectString = getResources().getString(R.string.cannot_connect);


        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        tgNoti = view.findViewById(R.id.toggle_noti);
        Button btnSubmit = view.findViewById(R.id.button_submit);
        TextView txtMessage = view.findViewById(R.id.latest_push_message);

        tgNoti.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        btnSubmit.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        txtMessage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        if (pref.getBoolean("_push_noti_setting", false)) {
            tgNoti.setChecked(true);
        } else {
            tgNoti.setChecked(false);
        }

        if (!pref.getString("_push_noti_message", "").equals("")) {
            txtMessage.setText(pref.getString("_push_noti_message", ""));
        } else {
            txtMessage.setText(" N.A ");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tgNoti.isChecked()) {
                    getTokenInBackground("reg");
                } else {
                    getTokenInBackground("unreg");
                }

            }
        });

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    @SuppressLint("StaticFieldLeak")
    private void getTokenInBackground(final String status) {
        prgDialog.show();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                regId = FirebaseInstanceId.getInstance().getToken();
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Utils.psLog(" Msg Val " + msg);
                if (!regId.equals("")) {
                    submitToServer(status, regId);
                } else {
                    hideProgress();
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            serviceNotAvaiString,
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    private void submitToServer(final String toggleStatus, String token) {


        String URL;
        if (toggleStatus.equals("reg")) {
            URL = Config.APP_API_URL + Config.POST_FCM_REGISTER;
        } else {
            URL = Config.APP_API_URL + Config.POST_FCM_UNREGISTER;
        }
        params.put("reg_id", token);
        params.put("platformName", "android");

        Utils.psLog(" params " + params);


        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        hideProgress();

                        Utils.psLog("Server Resp : " + response);

                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {
                                if (toggleStatus.equals("reg")) {
                                    if(getContext() != null) {
                                        Toast.makeText(
                                                getContext(),
                                                gcmRegisterSuccessString,
                                                Toast.LENGTH_LONG).show();

                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("_push_noti_setting", true);
                                        editor.apply();
                                    }

                                } else {
                                    Toast.makeText(
                                            getContext(),
                                            gcmUnregisterSuccessString,
                                            Toast.LENGTH_LONG).show();

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("_push_noti_setting", true);
                                    editor.apply();
                                }
                            } else {
                                if(getContext() != null) {
                                    Toast.makeText(
                                            getContext(),
                                            gcmRegisterNotSuccessString,
                                            Toast.LENGTH_LONG).show();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        hideProgress();

                        if(getContext() != null) {
                            if (statusCode == 404) {
                                Toast.makeText(getContext(),
                                        gcmRequestNotFoundString,
                                        Toast.LENGTH_LONG).show();
                            } else if (statusCode == 500) {
                                Toast.makeText(getContext(),
                                        gcmSomethingWrongString,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(
                                        getContext(),
                                        gcmCannotConnectString,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

    }

    private void hideProgress() {
        prgDialog.hide();
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
}
