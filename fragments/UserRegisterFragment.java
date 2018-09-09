package com.panaceasoft.restaurateur.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.R;
import com.panaceasoft.restaurateur.activities.MainActivity;
import com.panaceasoft.restaurateur.activities.UserRegisterActivity;
import com.panaceasoft.restaurateur.utilities.Utils;
import com.panaceasoft.restaurateur.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class UserRegisterFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private View view;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private String userName;
    private String email;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private LinearLayout mainLayout;

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
        view = inflater.inflate(R.layout.fragment_user_register, container, false);

        initData();

        initUI();

        return view;
    }


    @Override
    public void onDestroy() {
        try {
            Utils.unbindDrawables(mainLayout);
            //GlobalData.shopdata = null;
            super.onDestroy();
        }catch (Exception e){
            super.onDestroy();
        }
    }
    /*------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **-----------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

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
        mainLayout = this.view.findViewById(R.id.nav_register);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));

        txtName = this.view.findViewById(R.id.input_name);
        txtEmail = this.view.findViewById(R.id.input_email);
        txtPassword = this.view.findViewById(R.id.input_password);
        Button btnRegister = this.view.findViewById(R.id.button_register);
        Button btnCancel = this.view.findViewById(R.id.button_cancel);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancel();
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

    private void doCancel() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openFragment(R.id.nav_profile);
        } else if (getActivity() instanceof UserRegisterActivity) {
            getActivity().finish();
        }
    }

    private boolean inputValidation() {

        if(getActivity() != null) {
            if (txtName.getText().toString().equals("")) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.name_validation_message,
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (txtEmail.getText().toString().equals("")) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (txtPassword.getText().toString().equals("")) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.password_validation_message,
                        Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }else {
            return false;
        }
    }

    private void doRegister() {

        if (inputValidation()) {

            final String URL = Config.APP_API_URL + Config.POST_USER_REGISTER;
            Utils.psLog(URL);

            userName = txtName.getText().toString().trim();
            email = txtEmail.getText().toString().trim();

            HashMap<String, String> params = new HashMap<>();
            params.put("username", txtName.getText().toString().trim());
            params.put("email", txtEmail.getText().toString().trim());
            params.put("password", txtPassword.getText().toString().trim());
            params.put("about_me", "");
            doSubmit(URL, params);

        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String status = response.getString("status");
                            Utils.psLog(status);
                            if (status.equals(jsonStatusSuccessString)) {

                                String user_id = response.getString("data");

                                if(getActivity() != null) {
                                    //Save Login User Info
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("_login_user_id", Integer.parseInt(user_id));
                                    editor.putString("_login_user_name", userName);
                                    editor.putString("_login_user_email", email);
                                    editor.putString("_login_user_about_me", "");
                                    editor.putString("_login_user_photo", "default_user_profile.png");
                                    editor.apply();

                                    // Update Menu
                                    Utils.activity.bindMenu();
                                    //Utils.activity.loadProfileImage("default_user_profile.png");
                                    // Show profile Menu
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).openFragment(R.id.nav_profile_login);
                                    }

                                    prgDialog.cancel();
                                    showSuccessPopup();
                                }

                            } else {
                                prgDialog.cancel();
                                Utils.psLog("Register Fail");
                                showFailPopup();

                            }


                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Register Fail");
                            showFailPopup();
                        } catch (Exception e){
                            Utils.psErrorLog("Error in loading.", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    prgDialog.cancel();
                    Utils.psLog("Error: " + error.getMessage());
                }catch (Exception e) {
                    Utils.psErrorLog("onErrorResponse", e);
                }
            }
        });

        req.setShouldCache(false);
        // add the request object to the queue to be executed
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(req);

    }

    private void showFailPopup() {
        if(getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.register);
            builder.setMessage(R.string.login_fail);
            builder.setPositiveButton(R.string.OK, null);
            builder.show();
        }
    }

    private void showSuccessPopup() {
        if(getActivity() != null) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.register);
            builder.setMessage(R.string.register_success);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openFragment(R.id.nav_profile);
                    } else if (getActivity() instanceof UserRegisterActivity) {
                        //getActivity().finish();
                        ((UserRegisterActivity) getActivity()).successfullyRegisterd();
                    }
                    Utils.psLog("OK clicked.");
                }
            });
            builder.show();
        }

    }

    /*------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
}

