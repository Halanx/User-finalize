package com.halanx.userapp.Activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
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
import com.halanx.userapp.app.Config;
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
    String name, email;
    String[] nameSplit;
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

        accessToken = AccessToken.getCurrentAccessToken();

//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Location services not enabled");  // GPS not found
//            builder.setMessage("Kindly enable the location services to proceed"); // Want to enable?
//            builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    finish();
//                }
//            });
//            dial1 = builder.create();
//            dial1.show();
//        }

//        int PERMISSION_ALL = 1;
//        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE};
//
//        if(!hasPermissions(this, PERMISSIONS)){
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//        }

        forgot_password = (TextView) findViewById(R.id.forgot_password);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
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
            if(dial1!=null){
            dial1.dismiss();}
            if(dial2!=null){
            dial2.dismiss();}

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
                    jsonObject.put("username","c"+ mobile);
                    jsonObject.put("password",password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Volley.newRequestQueue(SigninActivity.this).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/rest-auth/login/",jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.d("data", String.valueOf(response));
                                    final String token;
                                    try {
                                        token = response.getString("key");
                                        Log.d("key", token);

                                        getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).edit().putString("token","token "+token).commit();
                                        Log.d("token_key",getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                                        Volley.newRequestQueue(SigninActivity.this).add(new JsonObjectRequest(Request.Method.GET, "https://api.halanx.com/users/detail/",jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("data", String.valueOf(response));
                                                data();
                                                try {
                                                    getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
                                                            putString("firstname",response.getJSONObject("user").getString("first_name")).
                                                            putString("lastname",response.getJSONObject("user").getString("last_name")).
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
                                                Toast.makeText(getApplicationContext(),"Invalid username/password",Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                btnLogin.setVisibility(View.VISIBLE);

                                            }
                                        }) {
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("Content-Type", "application/json");
                                                params.put("Authorization", "token "+token);
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

                                    Toast.makeText(getApplicationContext(),"Invalid username/password",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setVisibility(View.VISIBLE);

                                }
                            }
                            ));



            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((inputMobile.getText().length() > 10) || (inputMobile.getText().length() < 10)) {

                    Toast.makeText(getApplicationContext(), "Please Enter the correct number", Toast.LENGTH_SHORT).show();
                } else {


                        random = sendOtp();
                        mobile = inputMobile.getText().toString().trim();

                        final Dialog dialog = new Dialog(SigninActivity.this);
                        dialog.setContentView(R.layout.activity_verify);
                        dialog.setTitle("OTP has been sent to" + inputMobile.getText());
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                        final TextView tvResendOtp = (TextView) dialog.findViewById(R.id.resend);

                        final EditText otp = (EditText) dialog.findViewById(R.id.enterOTP);
                        Button btnOtpSubmit = (Button) dialog.findViewById(R.id.btnOTPsubmit);
                        TextView tvNumber = (TextView) dialog.findViewById(R.id.dialogue_number);

                        tvNumber.setText(inputMobile.getText());
                        btnOtpSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String url = "https://api.halanx.com/users/loginotp/";
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("username","c"+mobile);
                                    json.put("password",Integer.parseInt(otp.getText().toString()));
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
                                            Toast.makeText(SigninActivity.this, "User Verified", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            Toast.makeText(SigninActivity.this, "User Verified", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            final Dialog dialog1 = new Dialog(getApplicationContext());
                                            dialog1.setContentView(R.layout.forgot_password_layout);
                                            final EditText new_password = (EditText) dialog1.findViewById(R.id.password);
                                            Button done = (Button) dialog1.findViewById(R.id.button_done);

                                            final String finalToken = token;
                                            done.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    String url = "https://api.halanx.com/rest-auth/password/change/";
                                                    JSONObject json = new JSONObject();

                                                    try {
                                                        json.put("new_password1",new_password);
                                                        json.put("new_password2",new_password);

                                                        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, url, json, new com.android.volley.Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                Toast.makeText(getApplicationContext(),"Password change",Toast.LENGTH_SHORT).show();
                                                                dialog1.dismiss();

                                                            }
                                                        }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.d("ERROR" ,"error");

                                                            }
                                                        })
                                                        {
                                                            @Override
                                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                                Map<String, String> params = new HashMap<String, String>();
                                                                params.put("Content-Type", "application/json");
                                                                params.put("Authorization", "token "+ finalToken);
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


                    }

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


        //-----------

//        fblogin = (LoginButton) findViewById(R.id.login_button);
//        fblogin.setReadPermissions(Arrays.asList(
//                "public_profile", "email", "user_birthday", "user_friends", ""));
//        callbackManager = CallbackManager.Factory.create();
//
//        fblogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(final LoginResult loginResult) {
//                Log.d("Facebook", "1");
//
//
//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                Log.d("Facebook", "2");
//
//                                Log.v("LoginActivity", String.valueOf(loginResult.getAccessToken().getToken()));
//                                Log.d("fb_data", String.valueOf(object));
//
//                                try {// Application code
//
//                                    name = object.getString("name");
//                                    Log.d("FB NAME", name);
//
//                                    email = object.getString("email");
//                                    Log.d("FB NAME", email);
////
//                                    nameSplit = name.trim().split("\\s+");
//                                    Log.d("updatedata", nameSplit[0] + "," + nameSplit[1]);
//
//                                    getSharedPreferences("fbdata", Context.MODE_PRIVATE).edit().
//                                            putBoolean("fbloginned", true).apply();
//
//                                    String url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/users";
//
//
//
//
//
//                                    Volley.newRequestQueue(SigninActivity.this).add(new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
//                                        @Override
//                                        public void onResponse(String response) {
//                                            Log.e("Response", response);
//                                            if (response.isEmpty()) {
//                                                Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
//                                                intent.putExtra("first_name", nameSplit[0]);
//                                                intent.putExtra("last_name", nameSplit[1]);
//                                                intent.putExtra("access_token", loginResult.getAccessToken().getToken());
//                                                intent.putExtra("email", email);
//                                                startActivity(intent);
//                                            } else {
//                                                getSharedPreferences("Login", Context.MODE_PRIVATE).edit().
//                                                        putString("UserInfo", response).putString("MobileNumber", mobile).
//                                                        putBoolean("first_login", true).
//                                                        putBoolean("Loginned", true).apply();
//
//                                                getSharedPreferences("status", Context.MODE_PRIVATE).edit().
//                                                        putBoolean("first_login", true).apply();
//
//                                                startActivity(new Intent(SigninActivity.this, MapsActivity.class));
//                                                finish();
//
//                                            }
//                                        }
//                                    }, new com.android.volley.Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            Log.d("Facebook", "3");
//                                        }
//                                    }));
//
//
////                                   // 01/31/1980 format
//                                } catch (JSONException e) {
//                                    Log.d("Facebook", "4");
//                                    Log.d("catch", e.toString());
//                                    e.printStackTrace();
//                                }
//                            }
//
//                        });
//
//                Bundle parameters = new Bundle();
//
//                parameters.putString("fields", "id,name,email,gender,birthday");
//                request.setParameters(parameters);
//                request.executeAsync();
//
//
////                Toast.makeText(SigninActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
//
//            }
//

//            @Override
//            public void onCancel() {
//                // App code
//                Log.v("LoginActivity", "cancel");
//                Log.d("Facebook", "5");
//
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.d("Facebook ex", exception + " " + exception.getCause());
//                Log.d("Facebook", "6");
//            }
//        });

//--------------
    }


    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.GET_ACCOUNTS}, 101);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                  } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    String sendOtp() {


        JSONObject json = new JSONObject();
        try {
            json.put("FirstName","User");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, "https://api.halanx.com/users/getotp/" + mobile+"/", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("otp_response", String.valueOf(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));




//        Toast.makeText(getApplicationContext(), random, Toast.LENGTH_SHORT).show();


        return "sent";



    }


































    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("HardwareIds")
    public void data(){

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
            jsonObject.put("phone_number",mobile);

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