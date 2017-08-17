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

import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {


    String Imei = null;
    String locale = null;
    String regId = null;
    String latitude = null;
    String longitude = null;
    String ip = null;
    String macAddress = null;
    String network_type = null;
    String androidOS = null;
    String phone_make = null;
    String phone_model = null;
    String sdkVersion = null;
    String ram =null;
    String   storage_space  = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    data();
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
            String Imei = telephonyManager.getDeviceId();
            Log.d("phone_data", Imei);
        }
        catch (Exception e){

        }

        try {
            String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
            Log.d("phone_data", locale);

        }
        catch (Exception e){

        }
try{
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
         String regId = pref.getString("regId", null);
        Log.d("phone_data", regId);
    }
        catch (Exception e){

    }


    try
    {
    Location location = null;
        String latitude = String.valueOf(location.getLatitude());
        Log.d("phone_data", latitude);
    }
        catch (Exception e){

    }
try{
    Location location = new Location(String.valueOf(this));
        String longitude = String.valueOf(location.getLongitude());
        Log.d("phone_data", longitude);

    }
        catch (Exception e){

    }


      try{  @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d("phone_data", ip);
      }
      catch (Exception e){

      }

try{
    @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

    WifiInfo wInfo = wm.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        Log.d("phone_data", macAddress);
}
catch (Exception e){

}


try{
        String network_type = getNetworkClass(getApplicationContext());
        Log.d("phone_data", network_type);

}
catch (Exception e){

}
try{
        String androidOS = Build.VERSION.RELEASE;
        Log.d("phone_data", androidOS);

}
catch (Exception e){

}
try{
        String phone_make = Build.BRAND;
        Log.d("phone_data", phone_make);
}
catch (Exception e){

}
try{
        String phone_model = Build.MODEL;

        Log.d("phone_data", phone_model);
}
catch (Exception e){

}
try{

        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        Log.d("phone_data", sdkVersion);

}
catch (Exception e){

}
try{


        ActivityManager actManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        String ram = String.valueOf(memInfo.totalMem);
        Log.d("phone_data", ram);

}
catch (Exception e){

}
try{
    List<String> accounts = new ArrayList<>();
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
        String   storage_space  = String.valueOf((statFs.getBlockCount() * statFs.getBlockSize()));
        Log.d("phone_data", storage_space);
}
catch (Exception e){

}


try{

    String installed_apps = getInstalledAppList();
    Log.d("phone_data", installed_apps);

}
catch (Exception e){

}

try{
    String mcc =getMCC();
    Log.d("phone_data", mcc);

}
catch(Exception e){
        }


        try{

            String mcc2 = getMCC2();
            Log.d("phone_data", mcc2);

        }
        catch(Exception e){
        }

        try{

            String mnc = getMNC();
            Log.d("phone_data", mnc);

        }
        catch(Exception e){
        }

        try{

            String mnc2 = getMNC2();
            Log.d("phone_data", mnc2);

        }
        catch(Exception e){
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

        String mnc = "";

        TelephonyManager tel = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mnc = networkOperator.substring(3);
        }
        return mnc;
    }

    public String getMCC(){

        String mcc = "";


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

        String mnc = "";


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

        String mcc = "";

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
