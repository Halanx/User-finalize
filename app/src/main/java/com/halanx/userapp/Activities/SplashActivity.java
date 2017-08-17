package com.halanx.userapp.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;

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
      //          data();
              startActivity(new Intent(SplashActivity.this,SigninActivity.class));
                finish();
            }
        },2000);
    }
    @SuppressLint("HardwareIds")
    private void data(){

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String Imei = telephonyManager.getDeviceId();
        Log.d("phone_data", Imei);

        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        Log.d("phone_data", locale);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
         String regId = pref.getString("regId", null);
        Log.d("phone_data", regId);


        Location location = null;
        String latitude = String.valueOf(location.getLatitude());
        Log.d("phone_data", latitude);

        String longitude = String.valueOf(location.getLongitude());
        Log.d("phone_data", longitude);



        @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d("phone_data", ip);


        WifiInfo wInfo = wm.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        Log.d("phone_data", macAddress);


        String network_type = getNetworkClass(getApplicationContext());
        Log.d("phone_data", network_type);


        String androidOS = Build.VERSION.RELEASE;
        Log.d("phone_data", androidOS);


        String phone_make = Build.BRAND;
        Log.d("phone_data", phone_make);

        String phone_model = Build.MODEL;

        Log.d("phone_data", phone_model);


        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        Log.d("phone_data", sdkVersion);




        ActivityManager actManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        String ram = String.valueOf(memInfo.totalMem);
        Log.d("phone_data", ram);


        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] social_id = manager.getAccounts();
        Log.d("phone_data", String.valueOf(social_id));



        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        String   storage_space  = String.valueOf((statFs.getBlockCount() * statFs.getBlockSize()));
        Log.d("phone_data", storage_space);

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
}
