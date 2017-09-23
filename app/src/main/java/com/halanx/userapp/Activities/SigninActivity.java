package com.halanx.userapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.halanx.userapp.R;
import com.katepratik.msg91api.MSG91;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.halanx.userapp.Activities.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by samarthgupta on 12/02/17.
 */


public class SigninActivity extends AppCompatActivity {

    private EditText inputMobile, inputPassword;
    private ProgressBar progressBar;
    private TextView btnRegister;
    private Button btnLogin;
    SharedPreferences sharedPreferences;
    TextView forgot_password;
    String random;
    MSG91 msg91 = new MSG91("156475AdUYanwCiKI35970f67d");



    LoginButton fblogin;
    CallbackManager callbackManager;
    String mobile;
    String password;
    AccessToken accessToken;
    AlertDialog dial1 , dial2, dial3;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signin);
        msg91.validate();


        // curent version code
        try {
            androidOS = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("phone_data", androidOS);




        //dialogue box to show uppdate is there on plystore
        if(SplashActivity.flag){
            new AlertDialog.Builder(this)
                    .setTitle("Update")
                    .setMessage("Update your app to continue with all new features").setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.halanx.userapp&hl=en"));
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();

        }






        accessToken = AccessToken.getCurrentAccessToken();

        forgot_password = (TextView) findViewById(R.id.forgot_password);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                dial2 = new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(SigninActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create();
                dial2.show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }


        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        Boolean loginStatus = sharedPreferences.getBoolean("Loginned", false);

        if (loginStatus) {
            if (dial1 != null) {
                dial1.dismiss();
            }
            if (dial2 != null) {
                dial2.dismiss();
            }

            if (sharedPreferences.getBoolean("first_login", false)) {
                startActivity(new Intent(SigninActivity.this, HomeActivity.class));
                finish();
            } else {
                startActivity(new Intent(SigninActivity.this, MapsActivity.class));
                finish();
            }
        }


        //FACEBOOK SDK


        inputMobile = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        btnLogin = (Button) findViewById(R.id.email_sign_in_button);
        btnRegister = (TextView) findViewById(R.id.signUp);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);
                mobile = inputMobile.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(getApplicationContext(), "Enter mobile address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);


                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);

                    return;
                }


                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", "c" + mobile);
                    jsonObject.put("password", password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Volley.newRequestQueue(SigninActivity.this).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/rest-auth/login/", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("data", String.valueOf(response));
                            final String token;
                            try {
                                token = response.getString("key");
                                Log.d("key", token);

                                getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).edit().putString("token", "token " + token).commit();
                                Log.d("token_key", getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));
                                Volley.newRequestQueue(SigninActivity.this).add(new JsonObjectRequest(Request.Method.GET, "https://api.halanx.com/users/detail/", jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("data", String.valueOf(response));

                                        try {
                                            getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
                                                    putString("firstname", response.getJSONObject("user").getString("first_name")).
                                                    putString("email", response.getJSONObject("user").getString("email")).
                                                    putString("lastname", response.getJSONObject("user").getString("last_name")).
                                                    putString("UserInfo", String.valueOf(response)).putString("MobileNumber", mobile).
                                                    putBoolean("first_login", true).
                                                    putBoolean("Loginned", true).apply();
                                            getSharedPreferences("status", Context.MODE_PRIVATE).edit().
                                                    putBoolean("first_login", true).apply();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        Log.i("TAG", String.valueOf(response));
                                        Log.i("TAG", "Info" + getSharedPreferences("Login", Context.MODE_PRIVATE).getString("UserInfo", null));
                                        startActivity(new Intent(SigninActivity.this, MapsActivity.class));
                                        progressBar.setVisibility(View.INVISIBLE);
//
                                        finish();


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        btnLogin.setVisibility(View.VISIBLE);

                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("Content-Type", "application/json");
                                        params.put("Authorization", "token " + token);
                                        return params;
                                    }

                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);

                        }
                    }
                    ));

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);

                }

            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((inputMobile.getText().length() > 10) || (inputMobile.getText().length() < 10)) {

                    Toast.makeText(getApplicationContext(), "Please Enter the correct number", Toast.LENGTH_SHORT).show();
                } else {


                    mobile = inputMobile.getText().toString().trim();

                    final Dialog dialog = new Dialog(SigninActivity.this);
                    dialog.setContentView(R.layout.activity_verify);
                    dialog.setTitle("OTP has been sent to" + mobile);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    JSONObject json = new JSONObject();
                    try {
                        json.put("FirstName", "User");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/getotp/" + mobile + "/", json, new Response.Listener<JSONObject>() {
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

                    final EditText otp = (EditText) dialog.findViewById(R.id.enterOTP);
                    Button btnOtpSubmit = (Button) dialog.findViewById(R.id.btnOTPsubmit);
                    TextView tvNumber = (TextView) dialog.findViewById(R.id.dialogue_number);
                    final ProgressBar pb = (ProgressBar) dialog.findViewById(R.id.pb);
                    final ProgressBar pbresend = (ProgressBar) dialog.findViewById(R.id.pbresend);


                    tvNumber.setText(inputMobile.getText());
                    btnOtpSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String url = "https://api.halanx.com/users/loginotp/";
                            JSONObject json = new JSONObject();
                            try {
                                json.put("username", "c" + mobile);
                                json.put("password", Integer.parseInt(otp.getText().toString()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, url, json, new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    String token = null;

                                    Log.d("response", String.valueOf(response));
                                    try {
                                        token = response.getString("key");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    {
                                        dialog.dismiss();
                                        final Dialog dialog1 = new Dialog(SigninActivity.this);
                                        dialog1.setContentView(R.layout.forgot_password_layout);
                                        dialog1.setTitle("Set Your New Password");
                                        dialog1.setCanceledOnTouchOutside(false);
                                        dialog1.show();
                                        Window window = dialog1.getWindow();
                                        window.setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                                        final EditText new_password = (EditText) dialog1.findViewById(R.id.password);
                                        Button done = (Button) dialog1.findViewById(R.id.button_done);

                                        final String finalToken = token;
                                        done.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String url = "https://api.halanx.com/rest-auth/password/change/";
                                                JSONObject json = new JSONObject();

                                                try {
                                                    json.put("new_password1", String.valueOf(new_password.getText()).trim());
                                                    json.put("new_password2", String.valueOf(new_password.getText()).trim());

                                                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, url, json, new com.android.volley.Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            Toast.makeText(getApplicationContext(), "Password change", Toast.LENGTH_SHORT).show();
                                                            dialog1.dismiss();

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.d("ERROR", "error");

                                                        }
                                                    }) {
                                                        @Override
                                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("Content-Type", "application/json");
                                                            params.put("Authorization", "token " + finalToken);
                                                            return params;
                                                        }

                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("error", String.valueOf(error));

                                }
                            }));


                        }
                    });
                    tvResendOtp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pbresend.setVisibility(View.VISIBLE);
                            tvResendOtp.setVisibility(View.GONE);


                            JSONObject json = new JSONObject();
                            try {
                                json.put("FirstName", "User");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/getotp/" + mobile + "/", json, new Response.Listener<JSONObject>() {
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


                                }
                            }));


                        }

                    });




                }


//
//    private void requestForSpecificPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.GET_ACCOUNTS}, 101);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case 101:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //granted
//                  } else {
//                    //not granted
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("fbdata", Context.MODE_PRIVATE).edit().
                        putBoolean("fbloginned", false).apply();
                startActivity(new Intent(SigninActivity.this, RegisterActivity.class));

            }
        });
    }

}