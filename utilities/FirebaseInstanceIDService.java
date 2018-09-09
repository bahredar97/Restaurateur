package com.panaceasoft.restaurateur.utilities;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.panaceasoft.restaurateur.Config;
import com.panaceasoft.restaurateur.utilities.Utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Panacea-Soft on 24/10/16.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Utils.psLog("token : " + token);

    }

}
