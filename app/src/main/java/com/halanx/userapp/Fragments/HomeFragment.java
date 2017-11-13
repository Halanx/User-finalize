package com.halanx.userapp.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.GsonBuilder;
import com.halanx.userapp.Activities.CartActivity;
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Activities.MapsActivity;
import com.halanx.userapp.Activities.SplashActivity;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;
import com.halanx.userapp.app.Config;
import com.halanx.userapp.util.NotificationUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

public class HomeFragment extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;
    Toolbar toolbar = null;

    ImageView userImage;
    TextView nametv;
    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;

    static String role="none";
    ImageView groupcart;
    ImageView cart, locationButton;
    public static RelativeLayout cartItems;
    public static TextView itemCount;
    public static int backPress =0;
    String group_id = "null";
    static int cartId;
    static int user_id;
    static String group_id1 ;
    EditText groupCode;
    Dialog dialAddMoney;

    static Boolean isaddress = false;
    static String address = " ";
    String token;
    static double promotionalbalance;

    io.socket.client.Socket socket;

    static String first_name;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String cartid;

    List<CartItem> items;
    AppBarLayout barLayout;
    AlertDialog.Builder alertdialog;

    public static int storeID;
    public static String storeName;
    public static String storeLogo;
    public static int storePosition;
    public static String storeCat;
    public static int position = 1;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        barLayout = (AppBarLayout) v.findViewById(R.id.app_bar);

        getActivity().getIntent().setAction("Already created");

        //dialogue box to show uppdate is there on plystore
        if(SplashActivity.flag){
            alertdialog = new AlertDialog.Builder(getActivity());
            alertdialog.setTitle("Update")
                    .setMessage("Update your app to continue with all new features").setCancelable(false)
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.halanx.userapp&hl=en"));
                            startActivity(intent);
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }


        dialAddMoney = new Dialog(getActivity());
        dialAddMoney.setContentView(R.layout.groupcart);
        groupCode = (EditText) dialAddMoney.findViewById(R.id.group_code);


        token = getActivity().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");
        Log.d("token",token);
        String url = djangoBaseUrl+"users/detail/";

        Volley.newRequestQueue(getActivity()).add(new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    user_id = response.getInt("id");
                    first_name = response.getJSONObject("user").getString("first_name");
                    Log.d("mobilenumber",response.getString("PhoneNo"));
                    getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).edit().putString("MobileNumber",response.getString("PhoneNo").trim()).apply();
                    promotionalbalance = response.getDouble("PromotionalBalance");

                    if (response.getString("Address")!="null"){
                        isaddress = true;

                        address = response.getString("Address").trim();
                    }
                    else{
                        address=" ";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    group_id = response.getString("GroupCart");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Volley.newRequestQueue(getActivity()).add(new JsonObjectRequest(Request.Method.GET, djangoBaseUrl + "carts/detail/", null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response123", String.valueOf(response));
                        try
                        {
                            cartId = response.getJSONObject("data").getInt("id");
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(group_id!="null") {
                            if (cartId == Integer.parseInt(group_id))
                            {
                                role = "admin";
                            }
                            else {
                                role = "member";
                            }
                        }
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
                        params.put("Authorization", getActivity().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                        return params;
                    }

                });


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
                params.put("Authorization", getActivity().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                return params;
            }

        });









        cart = (ImageView) v.findViewById(R.id.imageButton_cart);
        locationButton = (ImageView) v.findViewById(R.id.imageButton_location);
        cart.setOnClickListener(this);
        locationButton.setOnClickListener(this);


        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);

        cartItems = (RelativeLayout) v.findViewById(R.id.cartitems);
        itemCount = (TextView) v.findViewById(R.id.itemcount);

        if (position==1||position==3) {

            StoresFragment frg = new StoresFragment();
            frg.passData(getActivity(), itemCount);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frag_container, frg);
            fragmentTransaction.commit();

        }
        else if(position==2){
            MainFragment frg = new MainFragment();
            frg.passdata(itemCount, String.valueOf(StoresFragment.storesearchdata));
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frag_container, frg);
            fragmentTransaction.commit();

        }


        Call<List<CartItem>> callItems = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class)
                .getUserCartItems(token);
        callItems.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                List<CartItem> items = response.body();


                Log.d("items", String.valueOf(items));

                if (items != null && items.size() > 0) {
                    //Accesss views?
                    Log.d("itemcount", String.valueOf(items.size()));
                    HomeActivity.cartItems.setVisibility(View.VISIBLE);
                    itemCount.setText(String.valueOf(items.size()));
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {

            }
        });

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //FB LOGIN
        NavigationView navigationView = (NavigationView) v.findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        nametv = (TextView) view.findViewById(R.id.nametv);
        userImage = (ImageView) view.findViewById(R.id.userimage);
        String userInfo = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("UserInfo", null);
        UserInfo user = new GsonBuilder().create().fromJson(userInfo, UserInfo.class);

        nametv.setText("Hi " + getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("firstname", null) + " !");

        Call<UserInfo> userCall = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.
                create()).build().create(DataInterface.class).getUserInfo(getActivity().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
        userCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                UserInfo info = response.body();
                Log.d("usercart1",String.valueOf(info.getgroupcart()));
                cartid = info.getgroupcart();
                Log.d("usercart1",String.valueOf(info.getgroupcart()));
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.i("Err",t.toString());
            }
        });

        //IF NO INTERNET
        if (!isNetworkAvailable()) {
            if (!getActivity().isFinishing()){
                new AlertDialog.Builder(getActivity())
                        .setTitle("No internet connection")
                        .setMessage("You are not connected to the internet").setCancelable(false)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }

                        })
                        .show();
            }
        }


        if (getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null) != null) {
            nametv.setText("Hi " + getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("firstname", null) + " !");

        } else {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FB_DATA", Context.MODE_PRIVATE);
            String fName = sharedPreferences.getString("fbName", "halanx");
            Log.d("fname", fName);
            String fEmail = sharedPreferences.getString("fbEmail", "halanx");
            Log.d("femail", fEmail);
            String image = sharedPreferences.getString("fbPic", "halanx");
            Log.d("fimage", image);
            nametv.setText(fName);
            Picasso.with(getActivity()).load(image).into(userImage);
        }

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

                    Toast.makeText(getActivity(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //  txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        final String mobileNumber = sharedPreferences.getString("MobileNumber", null);
        token = getActivity().getSharedPreferences("TokenKey", Context.MODE_PRIVATE).getString("token", null);


        sharedPreferences = getActivity().getSharedPreferences("status", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("first_login", false)) {

            ViewTarget target = new ViewTarget(R.id.imageButton_location, getActivity());
            ShowcaseView sv = new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setTarget(target)
                    .setContentTitle("DELIVERY LOCATION")
                    .setContentText("Change your delivery location and search for nearby stores by just a singe Tap ")
                    .withHoloShowcase()
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();
            sv.setButtonText("OK");



        }
        getActivity().getSharedPreferences("status", Context.MODE_PRIVATE).edit().
                putBoolean("first_login", false).apply();

    
        return v;

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.imageButton_cart:

                startActivity(new Intent(getActivity(), CartActivity.class));
                getActivity().getApplicationContext().getSharedPreferences("groupcart", Context.MODE_PRIVATE).edit().putBoolean("groupcart", false).apply();
                break;

            case R.id.imageButton_location:
                startActivity(new Intent(getActivity(), MapsActivity.class));
                break;


        }
    }

    @Override
    public void onResume() {
        super.onResume();



        Call<List<CartItem>> callItems = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class)
                .getUserCartItems(token);
        callItems.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                List<CartItem> items = response.body();


                Log.d("items", String.valueOf(items));

                if (items != null && items.size() > 0) {
                    //Accesss views?
                    Log.d("itemcount", String.valueOf(items.size()));
                    HomeActivity.cartItems.setVisibility(View.VISIBLE);
                    itemCount.setText(String.valueOf(items.size()));
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {

            }
        });


        String action = getActivity().getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Log.v("Example", "Force restart");
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getActivity().getIntent().setAction(null);

        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getActivity());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

        getActivity().getSharedPreferences("groupData",Context.MODE_PRIVATE).edit().putString("goupcode",groupCode.getText().toString().trim()).apply();

    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        String url = ""+djangoBaseUrl+"users/detail/";
        final String finalToken = getActivity().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null);


        Log.i("Gcm", url);
        Log.i("Gcm", finalToken);
        JSONObject obj = new JSONObject();
        try {
            obj.put("GcmId", regId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Volley.newRequestQueue(getActivity()).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
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

        Log.e(TAG, "Firebase reg id: " + regId);
}

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dialAddMoney.dismiss();
    }

}
