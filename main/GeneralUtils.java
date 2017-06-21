package com.example.orcam.mymebasicapp.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by urias on 6/4/17.
 */

public class GeneralUtils {

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    public static String responseParser(String res) {
        String resStr = "";
        try {
            JSONObject resJson = new JSONObject(res.toString());
            resStr = resJson.getString("response");
        } catch(JSONException e){
            System.err.println("ControlLogic.java: Exception when converting response to json: " + res.toString());
        }
        return resStr;
    }

    public static void setProgressDialog(String title, String msg, ProgressDialog mProgress) {
        mProgress.setTitle(title);
        mProgress.setMessage(msg);
        mProgress.setIndeterminate(false);
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
    }

    public static void setAlertDialo(String msg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
