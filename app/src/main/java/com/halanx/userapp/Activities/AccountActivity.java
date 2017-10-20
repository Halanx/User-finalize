package com.halanx.userapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.gson.GsonBuilder;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

public class AccountActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView tvFirstName, tvLastName, tvEmail, tvMobile, signout;
    TextView tvAddress;
    String mobileNumber;
    EditText line1,line2,line3;

    String addressDetails;




    Button edit;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvFirstName = (TextView) findViewById(R.id.tv_firstName_useraccount);
        tvLastName = (TextView) findViewById(R.id.tv_lastName_user_account);
        tvEmail = (TextView) findViewById(R.id.tv_email_user_account);
        tvAddress = (TextView) findViewById(R.id.tv_address_user_account);
        tvMobile = (TextView) findViewById(R.id.tv_mobile_user_account);
        edit = (Button) findViewById(R.id.edittext);

        signout = (TextView) findViewById(R.id.signout);
        
        String userInfo = getSharedPreferences("Login", Context.MODE_PRIVATE).getString("UserInfo", null);
        UserInfo user = new GsonBuilder().create().fromJson(userInfo, UserInfo.class);

        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, djangoBaseUrl +"users/detail/", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    tvFirstName.setText(response.getJSONObject("user").getString("first_name"));
                    tvLastName.setText(response.getJSONObject("user").getString("last_name"));
                    tvEmail.setText(response.getJSONObject("user").getString("email"));
                    tvMobile.setText(response.getString("PhoneNo").toString());
                    if(response.getString("Address")!="null") {
                        tvAddress.setText(response.getString("Address"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                return params;
            }

        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("done,","done");
                final Dialog dialog = new Dialog(AccountActivity.this);
                dialog.setContentView(R.layout.layout_custom_alert_dialogue);


                line1 = (EditText) dialog.findViewById(R.id.et1_dialogue);
                line2 = (EditText) dialog.findViewById(R.id.et2_dialogue);
                line3 = (EditText) dialog.findViewById(R.id.et3_dialogue);
                Button proceed = (Button) dialog.findViewById(R.id.btProceed_dialogue);
                Button cancel = (Button) dialog.findViewById(R.id.btCancel_dialogue);

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(line1.getText().equals(" ")||line2.getText().equals(" ")||line3.getText().equals(" ")){

                            Toast.makeText(getApplicationContext(), "Enter Your Address", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Address Details Saved", Toast.LENGTH_SHORT).show();
                            addressDetails = line1.getText().toString() + ", " + line2.getText().toString() + ", " + line3.getText().toString();
                            Log.d("TAG", addressDetails);
                            tvAddress.setText(addressDetails);
                            tvAddress.invalidate();
                            String url = djangoBaseUrl +"users/detail/";
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("Address", addressDetails);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Address", "Done");
                                    dialog.dismiss();

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
                                    params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));

                                    return params;
                                }

                            });

                            getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
                                    putString("Address", addressDetails).apply();
                            tvAddress.setText(getSharedPreferences("Login", Context.MODE_PRIVATE).getString("Address",null));

                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }});




        signout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
                        putBoolean("Loginned", false).apply();
                finishAndRemoveTask();
                startActivity(new Intent(AccountActivity.this, SigninActivity.class));
                LoginManager.getInstance().logOut();
                finish();

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AccountActivity.this,HomeActivity.class));
        finish();
    }
}
