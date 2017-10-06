package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ReferEarnActivity extends AppCompatActivity {

    ImageButton ib_frag_share;
    TextView tvShare;
    TextView referal;
    String referalcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);

        referal = (TextView) findViewById(R.id.referal_code);
    //    ib_frag_share = (ImageButton) findViewById(R.id.ib_frag_share);
        tvShare = (TextView) findViewById(R.id.tv_share);
//        ib_frag_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                shareIt();
//            }
//        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt();
            }
        });

        String userInfo = getSharedPreferences("Login", Context.MODE_PRIVATE).getString("UserInfo", null);
        UserInfo user = new GsonBuilder().create().fromJson(userInfo, UserInfo.class);

  //      referal.setText(user.getMyreferalCode());


        JSONObject jsonObject = new JSONObject();
        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, "https://api.halanx.com/users/detail/", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    referalcode = String.valueOf(response.get("MyReferralCode"));
                    referal.setText(String.valueOf(response.get("MyReferralCode")));
                    Log.d("code", String.valueOf(response.get("PromotionalBalance")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                return params;
            }

        });

    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

        /*
        Set a MIME type for the content you're sharing. This will determine which applications
        the chooser list presents to your users. Plain text, HTML, images and videos are among
        the common types to share. The following Java code demonstrates sending plain text.
         */
        sharingIntent.setType("text/plain");

        /*
       You can pass various elements of your sharing content to the send Intent, including subject,
       text / media content, and addresses to copy to in the case of mobile sharing. This Java code
       builds a string variable to hold the body of the text content to share:
   */

        String shareBody = "Get grocery and food delivered from your favorite Stores and " +
                "restaurants in as little as an hour. Download app now : " +
                "https://play.google.com/store/apps/details?id=com.halanx.userapp " +"with my referral code " + referal.getText() ;

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Halanx : Grocery and Food delivery");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

        /*
        You can't share the same content in a text message or tweet that you could send using
        mobile. For this reason it's best to keep your sharing content as general as possible, so
        that the function will be as effective for Twitter and Facebook as it is for Gmail and mobile.
         */


    }
}