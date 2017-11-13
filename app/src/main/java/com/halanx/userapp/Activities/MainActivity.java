package com.halanx.userapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.halanx.userapp.Fragments.HomeFragment;
import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;
import com.halanx.userapp.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView feed,profile,stores,qualities,notification;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feed = (ImageView) findViewById(R.id.newfeed);
        notification = (ImageView) findViewById(R.id.notification);
        profile = (ImageView) findViewById(R.id.profile);
        stores = (ImageView) findViewById(R.id.stores);
        qualities = (ImageView) findViewById(R.id.qualities);

        feed.setOnClickListener(this);
        qualities.setOnClickListener(this);
        notification.setOnClickListener(this);
        profile.setOnClickListener(this);
        stores.setOnClickListener(this);

        HomeFragment frg = new HomeFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, frg);
        fragmentTransaction.commit();






        ///////// get GCM ID   /////////
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //  txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.stores:
                HomeFragment frg = new HomeFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, frg);
                fragmentTransaction.commit();
                break;
        }

    }




    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        String url = ""+djangoBaseUrl+"users/detail/";
        final String finalToken = getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token",null);


        Log.i("Gcm", url);
        Log.i("Gcm", finalToken);
        JSONObject obj = new JSONObject();
        try {
            obj.put("GcmId", regId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("GcmId", "Done");

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                Log.d("token",finalToken);
                params.put("Authorization", finalToken);
                return params;
            }
        });

        Log.e("firebase", "Firebase reg id: " + regId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
