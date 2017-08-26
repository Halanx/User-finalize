package com.halanx.userapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.halanx.userapp.R;

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


}
