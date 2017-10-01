package com.halanx.userapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.halanx.userapp.POJO.Resp;
import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;
import com.katepratik.msg91api.MSG91;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;

/**
 * Created by samarthgupta on 13/02/17.
 */

public class
RegisterActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword, inputFname, inputLname, inputMobile, inputIcode;
    Button btnRegister, btnVerify;

    //For taking response
    Resp resp;
    TextView already_member;

    ProgressBar pb,pbresend;
    ProgressBar progressRegister;
    String mobileNumber;
    Button btnOtpSubmit;
    Retrofit.Builder builder;
    MSG91 msg91 = new MSG91("156475AdUYanwCiKI35970f67d");
    String email, password, firstName, lastName, icode;
    EditText otp;
    String regId;
    TextInputLayout passworddata;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        msg91.validate();
        inputPassword = (EditText) findViewById(R.id.tv_password);
        btnVerify = (Button) findViewById(R.id.btn_verify);
        inputEmail = (EditText) findViewById(R.id.tv_email);
        inputFname = (EditText) findViewById(R.id.tv_firstName);
        inputLname = (EditText) findViewById(R.id.tv_lastName);
        inputMobile = (EditText) findViewById(R.id.tv_mobile);
        inputIcode = (EditText) findViewById(R.id.tv_inviteCode);
        progressRegister = (ProgressBar) findViewById(R.id.progressBar_register);
        passworddata = (TextInputLayout) findViewById(R.id.edittextlayout);

        already_member = (TextView) findViewById(R.id.textView10);

        already_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,SigninActivity.class));
                finish();
            }
        });


        //GIVING NULL POINT EXCEPTION
        if (getSharedPreferences("fbdata", Context.MODE_PRIVATE).getBoolean("fbloginned", false)) {

            inputFname.setText(getIntent().getStringExtra("first_name"));
            inputLname.setText(getIntent().getStringExtra("last_name"));
     //       inputEmail.setText(getIntent().getStringExtra("email"));
            passworddata.setVisibility(View.GONE);

//            getAccess token by getintent.getStringExtra("access_token")
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnVerify.setVisibility(View.GONE);
                progressRegister.setVisibility(View.VISIBLE);

                email = inputEmail.getText().toString().trim();
                firstName = inputFname.getText().toString().trim();
                lastName = inputLname.getText().toString().trim();
                mobileNumber = inputMobile.getText().toString().trim();
                icode = inputIcode.getText().toString().trim();


                if (getSharedPreferences("fbdata", Context.MODE_PRIVATE).getBoolean("fbloginned", false)) {

                   } else {
                    password = inputPassword.getText().toString().trim();
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                }
                if (mobileNumber.length() != 10) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    return;
                }
                else if (TextUtils.isEmpty(email)) {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Enter Email address!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!emailValidator(email)) {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    Toast.makeText(RegisterActivity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
                    return;
//                }
//                else if (password.length() < 6) {
//                    btnVerify.setVisibility(View.VISIBLE);
//                    progressRegister.setVisibility(View.GONE);
//
//
//                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
//                    return;
                } else if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Enter First and Last name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(mobileNumber)) {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mobileNumber.length() != 10) {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Internet available
                if (isNetworkAvailable(getApplicationContext())) {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);

                    dialog = new Dialog(RegisterActivity.this);
                    dialog.setContentView(R.layout.activity_verify);
                    dialog.setTitle("OTP has been sent to" + mobileNumber);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    JSONObject json = new JSONObject();
                    try {
                        json.put("FirstName",firstName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/getotp/" + mobileNumber+"/", json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("otp_response", String.valueOf(response));
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }));

                    final TextView tvResendOtp = (TextView) dialog.findViewById(R.id.resend);

                    otp = (EditText) dialog.findViewById(R.id.enterOTP);
                    btnOtpSubmit = (Button) dialog.findViewById(R.id.btnOTPsubmit);
                    TextView tvNumber = (TextView) dialog.findViewById(R.id.dialogue_number);
                    pb = (ProgressBar) dialog.findViewById(R.id.pb);
                    pbresend = (ProgressBar) dialog.findViewById(R.id.pbresend);

                    tvNumber.setText(mobileNumber);
                    btnOtpSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            progressRegister.setVisibility(View.GONE);

                            Log.d("data", String.valueOf(otp.getText()));
                            if(!String.valueOf(otp.getText()).equals("")) {
                                btnOtpSubmit.setVisibility(View.GONE);
                                pb.setVisibility(View.VISIBLE);
                                registration(otp.getText().toString());
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Please enter the OTP",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    tvResendOtp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pbresend.setVisibility(View.VISIBLE);
                            tvResendOtp.setVisibility(View.GONE);


                            JSONObject json = new JSONObject();
                            try {
                                json.put("FirstName",firstName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/getotp/" + mobileNumber+"/", json, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("otp_response", String.valueOf(response));
                                    tvResendOtp.setVisibility(View.VISIBLE);
                                    pbresend.setVisibility(View.GONE);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    tvResendOtp.setVisibility(View.VISIBLE);
                                    pbresend.setVisibility(View.GONE);
                                    progressRegister.setVisibility(View.GONE);



                                }
                            }));

                        }
                    });


                } else {
                    btnVerify.setVisibility(View.VISIBLE);
                    progressRegister.setVisibility(View.GONE);



                    Toast.makeText(RegisterActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();

                }


            }
        });


    }


    public void registration(String text) {

        //Put cart on server after account is created
        if (getSharedPreferences("fbdata", Context.MODE_PRIVATE).getBoolean("fbloginned", false)) {

            JSONObject jsonObject = new JSONObject();
            try{ jsonObject.put("PhoneNo", mobileNumber);
                jsonObject.put("otp",Integer.parseInt(text));
            }
            catch(Exception e){
            }
            try {
                Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/social/", jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response data", String.valueOf(response));
                        try {
                            getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
                                    putString("firstname", firstName).
                                    putString("lastname", lastName).
                                    putString("MobileNumber", mobileNumber).
                                    putBoolean("first_login", true).
                                    putBoolean("Loginned", true).apply();
                            getSharedPreferences("status", Context.MODE_PRIVATE).edit().
                                    putBoolean("first_login", true).apply();

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        btnOtpSubmit.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
                        finish();

                        dialog.dismiss();
                        btnOtpSubmit.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", String.valueOf(error));
                        Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        btnOtpSubmit.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);

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
            catch(Exception e){
                Log.d("error", String.valueOf(e));


            }


        }
        else{

            final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", "c" + mobileNumber);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("FirstName", firstName);
            jsonObject.put("LastName", lastName);
            jsonObject.put("PhoneNo", mobileNumber);
            jsonObject.put("otp",Integer.parseInt(text));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //Save user details in sharedPreferences
            Log.d("user", "done");
            Volley.newRequestQueue(RegisterActivity.this).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response1) {

                    Log.i("TAG", String.valueOf(response1));

                    try {
                        getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).edit().putString("token", "token " + response1.getString("key")).commit();
                        Volley.newRequestQueue(RegisterActivity.this).add(new JsonObjectRequest(Request.Method.GET, "https://api.halanx.com/users/detail/", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("data", String.valueOf(response));

                                try {
                                    getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
                                            putString("firstname", response.getJSONObject("user").getString("first_name")).
                                            putString("lastname", response.getJSONObject("user").getString("last_name")).
                                            putString("UserInfo", String.valueOf(response)).putString("MobileNumber", mobileNumber).
                                            putBoolean("first_login", true).
                                            putBoolean("Loginned", true).apply();
                                    getSharedPreferences("status", Context.MODE_PRIVATE).edit().
                                            putBoolean("first_login", true).apply();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                btnOtpSubmit.setVisibility(View.VISIBLE);
                                pb.setVisibility(View.GONE);

                                Log.i("TAG", String.valueOf(response));
                                Log.i("TAG", "Info" + getSharedPreferences("Login", Context.MODE_PRIVATE).getString("UserInfo", null));
                                startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
                                finish();


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                btnOtpSubmit.setVisibility(View.VISIBLE);
                                pb.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();

                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                try {
                                    params.put("Content-Type", "application/json");
                                    params.put("Authorization", "token " + response1.getString("key"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return params;
                            }

                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnOtpSubmit.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();

                    }


                    getSharedPreferences("status", Context.MODE_PRIVATE).edit().
                            putBoolean("first_login", true).apply();


                    startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
                    progressRegister.setVisibility(View.GONE);
                    finish();

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", String.valueOf(error));
                    Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    btnOtpSubmit.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);

                }
            }));
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
            btnOtpSubmit.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            dialog.dismiss();
        }
        }
    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginManager.getInstance().logOut();
    }

}



