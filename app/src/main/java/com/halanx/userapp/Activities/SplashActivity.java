package com.halanx.userapp.Activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {


    String Imei = "null";
    String locale = "null";
    String regId = "null";
    String latitude = "null";
    String longitude = "null";
    String ip = "null";
    String macAddress = "null";
    String network_type = "null";
    String androidOS = "null";
    String phone_make = "null";
    String phone_model = "null";
    String sdkVersion = "null";
    String ram ="null";
    String   storage_space  = "null";
    List<String> accounts = new ArrayList<>();
    String installed_apps = "null";
    String mcc2 = "null";
    String mcc = "null";
    String mnc = "null";
    String mnc2 = "null";
    String processor_vendor = "null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
              //      data();
                }
                startActivity(new Intent(SplashActivity.this,SigninActivity.class));
                finish();
            }
        },2000);



    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("HardwareIds")
    private void data(){

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Imei = telephonyManager.getDeviceId();
            Log.d("phone_data", Imei);
        }
        catch (Exception e){

        }

        try {
            locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
            Log.d("phone_data", locale);

        }
        catch (Exception e){

        }
try{
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
         regId = pref.getString("regId", "null");
        Log.d("phone_data", regId);
    }
        catch (Exception e){

    }


    try
    {
    Location location = null;
       latitude = String.valueOf(location.getLatitude());
        Log.d("phone_data", latitude);
    }
        catch (Exception e){

    }
try{
    Location location = new Location(String.valueOf(this));
        longitude = String.valueOf(location.getLongitude());
        Log.d("phone_data", longitude);

    }
        catch (Exception e){

    }


      try{  @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d("phone_data", ip);
      }
      catch (Exception e){

      }

try{
    @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

    WifiInfo wInfo = wm.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        Log.d("phone_data", macAddress);
}
catch (Exception e){

}


try{
        network_type = getNetworkClass(getApplicationContext());
        Log.d("phone_data", network_type);

}
catch (Exception e){

}
try{
        androidOS = Build.VERSION.RELEASE;
        Log.d("phone_data", androidOS);

}
catch (Exception e){

}
try{
        phone_make = Build.BRAND;
        Log.d("phone_data", phone_make);
}
catch (Exception e){

}
try{
        phone_model = Build.MODEL;

        Log.d("phone_data", phone_model);
}
catch (Exception e){

}
try{

        sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        Log.d("phone_data", sdkVersion);

}
catch (Exception e){

}
try{


        ActivityManager actManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        ram = String.valueOf(memInfo.totalMem);
        Log.d("phone_data", ram);

}
catch (Exception e){

}
try{

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] social_id = manager.getAccounts();
        for (int i=0;i<social_id.length;i++){
            accounts.add(String.valueOf(social_id[i]));
        }
        Log.d("phone_data", String.valueOf(accounts));
}
catch (Exception e){

}


try{
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        storage_space  = String.valueOf((statFs.getBlockCount() * statFs.getBlockSize()));
        Log.d("phone_data", storage_space);
}
catch (Exception e){

}


try{

    installed_apps = getInstalledAppList();
    Log.d("phone_data", installed_apps);

}
catch (Exception e){

}

try{
    mcc =getMCC();
    Log.d("phone_data", mcc);

}
catch(Exception e){
        }


        try{

            mcc2 = getMCC2();
            Log.d("phone_data", mcc2);

        }
        catch(Exception e){
        }

        try{

            mnc = getMNC();
            Log.d("phone_data", mnc);

        }
        catch(Exception e){
        }

        try{

            mnc2 = getMNC2();
            Log.d("phone_data", mnc2);

        }
        catch(Exception e){
        }


        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("apps_installed","apps");
            jsonObject.put("country_code", locale);
            jsonObject.put("gcm_id", regId);
            jsonObject.put("gps_latitude", latitude);
            jsonObject.put("gps_longitude", longitude);
            jsonObject.put("imei_id",Imei);
            jsonObject.put("ip_address", ip);
            jsonObject.put("mac_id", macAddress);
            jsonObject.put("mcc_code_1", mcc);
            jsonObject.put("mcc_code_2", mcc2);
            jsonObject.put("mnc_code_1", mnc);
            jsonObject.put("mnc_code_2", mnc2);
            jsonObject.put("network_type", network_type);
            jsonObject.put("os_version",androidOS );
            jsonObject.put("phone_make", phone_make);
            jsonObject.put("phone_model", phone_model);
            jsonObject.put("phone_os", sdkVersion);
            jsonObject.put("processor_vendor", processor_vendor);
            jsonObject.put("ram", ram);
            jsonObject.put("social_id", accounts);
            jsonObject.put("storage_space", storage_space);

            Log.d("jsonobject", String.valueOf(jsonObject));

             String url = "http://ec2-54-215-199-153.us-west-1.compute.amazonaws.com:8080/addPhoneData";

                Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, url,jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("resp", String.valueOf(response));
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("volleyerror", String.valueOf(error));
                }
            }));
        }
        catch (Exception e){
            Log.d("exception", String.valueOf(e));
        }

    }


    public String getInstalledAppList(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = getApplicationContext().getPackageManager().queryIntentActivities( mainIntent, 0);
        Log.d("installed apps", String.valueOf(pkgAppsList));
        return String.valueOf(pkgAppsList);
    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }
    public String getMNC(){


        TelephonyManager tel = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mnc = networkOperator.substring(3);
        }
        return mnc;
    }

    public String getMCC(){



        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tel = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tel.getNetworkOperator();

            if (!TextUtils.isEmpty(networkOperator)) {
                mcc = networkOperator.substring(0, 3);
            }
        }
        return mcc;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public String getMNC2(){



        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionManager subManager = (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subManager.getActiveSubscriptionInfoCount() >= 2) {

                if (subManager.getActiveSubscriptionInfo(1)!= null) {

                    mnc = String.valueOf(subManager.getActiveSubscriptionInfo(1).getMnc());
                }
            }
        }
        return mnc;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public String getMCC2(){

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionManager subManager = (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            if (subManager.getActiveSubscriptionInfoCount() >= 2) {
                if (subManager.getActiveSubscriptionInfo(1)!= null)
                {
                    mcc = String.valueOf(subManager.getActiveSubscriptionInfo(1).getMcc());
                }
            }
        }
        return mcc;
    }
}
