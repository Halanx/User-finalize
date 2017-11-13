package com.halanx.userapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Adapters.CartsAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartsInfo;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;


public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    CartsAdapter adapterTemp;

    LinearLayout detailslayout, orderslayout, final_detail;

    Button btnCheckout, btnDelivery, btnconfirm;
    TextView tvSubtotal, tvTotal, tvDelivery;

    TextView tvSubscription;
    Button btDelAsap, btDelSchedule, btAddDetails;
    TextView btAddLocate;
    Boolean delivery_scheduled = false, delivery_address = false;
    ImageView ivMap;
    AlertDialog alert;
    ProgressBar progressBar;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;

    List<CartItem> activeItems;
    AlertDialog.Builder alertBuilder;
    boolean delivery = false;
    String taxes;
    String date, timings;
    String total,subtotal;
    TextView tax;
    LinearLayout order_detail_layout;
    RelativeLayout group_layout;
    LinearLayout admin,member;

    Switch onOffSwitch;
    AlertDialog dial3;

    String group_id;
    TextView groupid;
    Button share,leave,deletegroup;

    String role;

    String addressDetails;
    EditText line1, line2, line3;
    ProgressBar pb_orderdetails;

    Boolean isgroupcart=false;
    String first_name;
    TextView emptydata;
    TextView xcash;

    LinearLayout admin_empty;
    TextView member_empty;

    private Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);
        onOffSwitch = (Switch) findViewById(R.id.xcash_onoff);

        ivMap = (ImageView) findViewById(R.id.iv_map);
        btDelAsap = (Button) findViewById(R.id.bt_delivery_asap);
        btDelSchedule = (Button) findViewById(R.id.bt_delivery_schedule);
        btAddDetails = (Button) findViewById(R.id.bt_address_details);
        btAddLocate = (TextView) findViewById(R.id.bt_address_locate);
        pb_orderdetails = (ProgressBar) findViewById(R.id.progressbarorderdetails);

        admin_empty = (LinearLayout) findViewById(R.id.empty_cart_admin);
        member_empty = (TextView) findViewById(R.id.empty_cart_member);


        btnDelivery = (Button) findViewById(R.id.details);
        order_detail_layout = (LinearLayout) findViewById(R.id.orderdetaillayout);

        groupid = (TextView) findViewById(R.id.groupid);
        share = (Button) findViewById(R.id.share);
        group_layout = (RelativeLayout) findViewById(R.id.group_layout);

        admin  = (LinearLayout) findViewById(R.id.admin);
        member = (LinearLayout) findViewById(R.id.members);
        deletegroup = (Button) findViewById(R.id.deletegroup);
        leave = (Button) findViewById(R.id.leave);

        btnCheckout = (Button) findViewById(R.id.checkout);
        orderslayout = (LinearLayout) findViewById(R.id.orders);
        detailslayout = (LinearLayout) findViewById(R.id.detail);
        final_detail = (LinearLayout) findViewById(R.id.confirm_detail);

        tax = (TextView) findViewById(R.id.tax);
        xcash = (TextView) findViewById(R.id.xcash);


        btDelAsap.setOnClickListener(this);
        btDelSchedule.setOnClickListener(this);
        btAddLocate.setOnClickListener(this);
        btAddDetails.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);
        btnDelivery.setOnClickListener(this);

        final int[] cartId = new int[1];

        tvSubtotal = (TextView) findViewById(R.id.tv_cart_subtotal);
        tvTotal = (TextView) findViewById(R.id.tv_cart_total);
        tvDelivery = (TextView) findViewById(R.id.tv_cart_deliverycharge);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cart);

        btnconfirm = (Button) findViewById(R.id.confirm_details);
        btnconfirm.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);
        btnCheckout.setOnClickListener(this);
        // btnDelivery.setOnClickListener(this);



        if(HomeActivity.isaddress) {

            Log.d("addresdetails", HomeActivity.address);
            addressDetails = HomeActivity.address;
        }


            SharedPreferences sharedPref = getSharedPreferences("location", Context.MODE_PRIVATE);
        float latitude = sharedPref.getFloat("latitudeDelivery", 0);// LATITUDE
        float longitude = sharedPref.getFloat("longitudeDelivery", 0);// LONGITUDE
        if (latitude != 0 && longitude != 0) {


            Log.i("Url", "https://maps.googleapis.com/maps/api/staticmap?zoom=16" +
                    "&markers=color:red%7label:C%7C" + latitude + "," + longitude + "&size=400x200&" +
                    "key=AIzaSyCj7HXOZgUqjouFVydt4irrhu3cQLqXMbg");
            Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?zoom=16" +
                    "&markers=color:red%7Clabel:%7C" + latitude + "," + longitude + "&size=400x200&" +
                    "key=AIzaSyBnCtz4CuPtcZ-87zXLsYvH1BrkTTJ9eyw").into(ivMap);
        } else {
            ivMap.setVisibility(View.GONE);
        }


        if(HomeActivity.role.equals("admin")){
            admin.setVisibility(View.VISIBLE);
            member.setVisibility(View.GONE);
        }
        else{
            member.setVisibility(View.VISIBLE);
            admin.setVisibility(View.GONE);
        }



        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("status", String.valueOf(!b));

                String url = djangoBaseUrl + "carts/detail/";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("ApplyCashback", b);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Volley.newRequestQueue(CartActivity.this).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        try {
                            subtotal = String.valueOf(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("data").getDouble("Total"))));

                        Float getxcash = Float.parseFloat(String.valueOf(jsonObject.getDouble("xcash")));
//                        Log.d("subtotal",String.valueOf(cart.getData().getTotal()));
//                        Log.d("subtotal",String.valueOf(cart.getXcash()));


                        if ((jsonObject.getJSONObject("data").getDouble("TotalWithExtras")-jsonObject.getDouble("xcash"))>0) {
                            total = String.valueOf(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("data").getDouble("TotalWithExtras")-jsonObject.getDouble("xcash"))));
                        }
                        else{
                            total = "0.0";
                        }
                        taxes = String.valueOf(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("data").getDouble("Taxes"))));
                     //   Log.d("subtotal",cart.getData().getTaxes().toString());

                        String del = String.valueOf(Float.parseFloat(String.valueOf(jsonObject.getJSONObject("data").getDouble("EstimatedDeliveryCharges"))));


                        tvSubtotal.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(subtotal)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        tax.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(taxes)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        tvTotal.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(total)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        tvDelivery.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(del)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        xcash.setText("-"+String.valueOf(String.valueOf(BigDecimal.valueOf(Double.parseDouble(String.valueOf(getxcash))).setScale(3, RoundingMode.HALF_UP).doubleValue())));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));
                        return params;
                    }

                });
                final String token = getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null);
                    Log.d("token",token);
                    Call<CartsInfo> callCart = client.getCartDetails(token);
                    callCart.enqueue(new Callback<CartsInfo>() {
                        @Override
                        public void onResponse(Call<CartsInfo> call, Response<CartsInfo> response) {
                            CartsInfo cart = response.body();
                            Log.d("response1", String.valueOf(cart));

                            Log.d("subtotal",String.valueOf(cart.getData().getId()));

                        }

                        @Override
                        public void onFailure(Call<CartsInfo> call, Throwable t) {
                            Log.d("errror", String.valueOf(t));

                        }
                    });
            }

        });


    final String token = getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");
        Log.d("token",token);
        String url = djangoBaseUrl+"users/detail/";
        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("response",response.getString("GroupCart"));
                    Log.d("responseabc", String.valueOf(response));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (response.getString("GroupCart").trim().equals("null")) {

                        group_layout.setVisibility(View.GONE);
                        Call<List<CartItem>> call = client.getUserCartItems(token);
                        call.enqueue(new Callback<List<CartItem>>() {
                            @Override
                            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                                Log.d("Response", String.valueOf(response));

                                List<CartItem> items = response.body();

                                if (!items.isEmpty()) {

                                    activeItems = new ArrayList<>();
                                    for (int i = 0; i < items.size(); i++) {

                                        if (!items.get(i).getRemovedFromCart()) {
                                            activeItems.add(items.get(i));
                                        }
                                    }

                                    //Displaying carts
                                    Log.d("TAG", "If");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    recyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
                                    adapterTemp = new CartsAdapter(activeItems, getApplicationContext(),false);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

                                    recyclerView.setAdapter(adapterTemp);
                                    recyclerView.setLayoutManager(layoutManager);

                                } else {
                                    if (!CartActivity.this.isFinishing()) {

                                        progressBar.setVisibility(View.INVISIBLE);
                                        alertBuilder = new AlertDialog.Builder(CartActivity.this);
                                        alertBuilder.setMessage("You have no items in your carts!");
                                        alertBuilder.setCancelable(false);

                                        alertBuilder.setPositiveButton(
                                                "Go back",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        finish();
                                                    }
                                                });
                                        alert = alertBuilder.create();
                                        alert.show();

                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<List<CartItem>> call, Throwable t) {

                            }
                        });
                    }

                    else{

                        JSONObject json = new JSONObject();
                        try {
                            json.put("room",response.getString("GroupCart").trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            mSocket = IO.socket("http://api.halanx.com:3700/");
                            mSocket.on(Socket.EVENT_CONNECT,onConnect);
                            //run
                            mSocket.connect();
                            mSocket.on("onMessage",onMessage);
                            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
                            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                            mSocket.emit("join",json );

//                            try {
////                                json.put("text","hello");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            mSocket.emit("onMessage",json );

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        //hogya


                        group_layout.setVisibility(View.VISIBLE);
                        group_id = getApplicationContext().getSharedPreferences("groupCode",Context.MODE_PRIVATE).getString("groupcode",null);


                        groupid.setText(group_id);




                        share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);

        /*
        Set a MIME type for the content you're sharing. This will determine which applications
        the chooser list presents to your users. Plain text, HTML, images and videos are among
        the common types to share. The following Java code demonstrates sending plain text.
         */
                                sharingIntent.setType("text/plain");
                                String shareBody ="Hey, "+ HomeActivity.first_name+" here. I have invited you for group order with group ID:"+
                                        groupid.getText().toString()+" on Halanx. Please add items to cart, and I will pay on ur behalf, and get ₹ 20 H-Cash\n" +
                                        "Download app: "+ "https://play.google.com/store/apps/details?id=com.halanx.userapp ";

                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Halanx : Grocery and Food delivery");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                            }
                        });



                        Call<List<CartItem>> call = client.getGroupCartItems(token);
                        call.enqueue(new Callback<List<CartItem>>() {
                            @Override
                            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {

                                Log.d("Response", String.valueOf(response.message()));
try {
    List<CartItem> items = response.body();
    Log.d("data", String.valueOf(items));
    role = HomeActivity.role;
    Log.d("role", HomeActivity.role);
    if (role.equals("admin")) {
        if (!items.isEmpty()) {

            activeItems = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {

                if (!items.get(i).getRemovedFromCart()) {
                    activeItems.add(items.get(i));
                }
            }
            //Displaying carts
            Log.d("TAG", "If");
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
            adapterTemp = new CartsAdapter(activeItems, getApplicationContext(), true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setAdapter(adapterTemp);
            recyclerView.setLayoutManager(layoutManager);

        }
        else
            {
                progressBar.setVisibility(View.INVISIBLE);
                admin_empty.setVisibility(View.VISIBLE);
                btnDelivery.setVisibility(View.GONE);
        }
    } else if (role.equals("member")) {
        if (!items.isEmpty()) {
        Log.d("myself", "If");
        activeItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getAddedBy() != null)
                if ((!items.get(i).getRemovedFromCart()) && (items.get(i).getAddedBy().getId() == HomeActivity.user_id)) {
                    activeItems.add(items.get(i));
                }
        }

        btnDelivery.setVisibility(View.GONE);
        //Displaying carts
        Log.d("TAG", "If");
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
        adapterTemp = new CartsAdapter(activeItems, getApplicationContext(), true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setAdapter(adapterTemp);
        recyclerView.setLayoutManager(layoutManager);
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            member_empty.setVisibility(View.VISIBLE);
            btnDelivery.setVisibility(View.GONE);
        }
    } else if (items.isEmpty()) {
        if (!CartActivity.this.isFinishing()) {
            progressBar.setVisibility(View.INVISIBLE);
            if (role.equals("admin")) {
                admin_empty.setVisibility(View.VISIBLE);
            }else if (role.equals("member")) {
            }
            }
    }
}catch (Exception e){
    Log.e("error",e.toString());
}
                            }
                            @Override
                            public void onFailure(Call<List<CartItem>> call, Throwable t) {

                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }}, new com.android.volley.Response.ErrorListener() {
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

        deletegroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = djangoBaseUrl + "carts/deletegroup/";
                Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        startActivity(new Intent(CartActivity.this,HomeActivity.class));
                        finish();

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

            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = djangoBaseUrl + "carts/leavegroup/"+groupid;
                Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        startActivity(new Intent(CartActivity.this,HomeActivity.class));
                        finish();

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

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                date = data.getStringExtra("date");
                timings = data.getStringExtra("time_selected");

                delivery_scheduled = true;
                Log.d("timingsdata", String.valueOf(date));
                Log.d("datedata", String.valueOf(timings));

            }
        }

    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("error", "connected");
               //     Toast.makeText(getApplicationContext(),

//                            "connect", Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("data", "diconnected");
            //        Toast.makeText(getApplicationContext(),
  //                          "disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("error", "Error connecting");
//                    Toast.makeText(getApplicationContext(),
//                            "error_connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    finish();
                    startActivity(getIntent());
                    JSONObject json = null;
                    try {
                        json = new JSONObject(String.valueOf(args[0]));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    {

                        Log.e("error", String.valueOf(json));
//                        Toast.makeText(getApplicationContext(),
//                                json.toString(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    };



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_delivery_asap:


                delivery = true;
                delivery_scheduled = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btDelAsap.setBackground(getDrawable(R.color.dark_red));
                    btDelSchedule.setBackground(getDrawable(R.drawable.my_button_bg));
                }


                break;

            case R.id.bt_delivery_schedule:

                delivery = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btDelAsap.setBackground(getDrawable(R.drawable.my_button_bg));
                    btDelSchedule.setBackground(getDrawable(R.color.dark_red));
                }
                Intent intent = new Intent(CartActivity.this, ScheduleActivity.class);

                startActivityForResult(intent, 1);

                break;

            case R.id.bt_address_details:
                delivery_address = true;
                final Dialog dialog = new Dialog(CartActivity.this);
                dialog.setContentView(R.layout.layout_custom_alert_dialogue);


                line1 = (EditText) dialog.findViewById(R.id.et1_dialogue);
                line2 = (EditText) dialog.findViewById(R.id.et2_dialogue);
                line3 = (EditText) dialog.findViewById(R.id.et3_dialogue);
                String add = getSharedPreferences("location",Context.MODE_PRIVATE).getString("addressDelivery",null);

                Button proceed = (Button) dialog.findViewById(R.id.btProceed_dialogue);
                Button cancel = (Button) dialog.findViewById(R.id.btCancel_dialogue);

                if(HomeActivity.isaddress){

                    Log.d("addresdetails",HomeActivity.address);
                    String data[] = addressDetails.split(",",3);
                    line1.setText(String.valueOf(data[0]).trim());
                    line2.setText(String.valueOf(data[1]).trim());
                    line3.setText(String.valueOf(data[2]).trim());

                }

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((String.valueOf(line1.getText()).equals("") || String.valueOf(line2.getText()).equals("") || String.valueOf(line3.getText()).equals(""))) {

                            Toast.makeText(getApplicationContext(), "Enter Your Address", Toast.LENGTH_SHORT).show();

                        } else {
                            try {
                                Toast.makeText(getApplicationContext(), "Address Details Saved", Toast.LENGTH_SHORT).show();
                                addressDetails = String.valueOf(line1.getText()).trim() + "," + String.valueOf(line2.getText()).trim() + "," + String.valueOf(line3.getText()).toString().trim();
                                Log.d("TAG", addressDetails);
                                dialog.dismiss();

//                                Log.d("detailsaddd", addressDetails);
//                                Log.d("detailsaddd", HomeActivity.address);


                                if (addressDetails.trim().equals(HomeActivity.address.trim())) {
                                    dialog.dismiss();
                                } else {
                                    dial3 = new AlertDialog.Builder(CartActivity.this)
                                            .setTitle("Address")
                                            .setMessage("Want to save this address for delivery !")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    Toast.makeText(getApplicationContext(), "Address Details Saved", Toast.LENGTH_SHORT).show();
                                                    addressDetails = line1.getText().toString() + "," + line2.getText().toString() + "," + line3.getText().toString();
                                                    Log.d("TAG", addressDetails);
                                                    String url = djangoBaseUrl + "users/detail/";
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
                                                            dial3.dismiss();

                                                        }
                                                    }, new com.android.volley.Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                        }
                                                    }) {
                                                        @Override
                                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("Content-Type", "application/json");
                                                            params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));

                                                            return params;
                                                        }

                                                    });
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dial3.dismiss();
                                                }
                                            }).create();
                                    dial3.show();
                                }

                            }
                            catch(Exception e){
                                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                            }

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


                break;
            case R.id.bt_address_locate:

                Intent intentMap = new Intent(CartActivity.this, MapsActivity.class);
                intentMap.putExtra("fromCart", true);
                startActivity(intentMap);
                break;
            case R.id.checkout:


                Intent intentCheckout = new Intent(CartActivity.this, PaymentActivity.class).putExtra("isOrder",true);
                intentCheckout.putExtra("AddressDetails", addressDetails);
                if (delivery_scheduled) {
                    intentCheckout.putExtra("Date", date);
                    intentCheckout.putExtra("Timings", timings);
                }

                intentCheckout.putExtra("deliveryScheduled", delivery_scheduled);
                intentCheckout.putExtra("total_cost",total);
                startActivity(intentCheckout);
                finish();
                break;
            case R.id.details: {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

                if (detailslayout.getVisibility() == View.GONE) {

                    detailslayout.startAnimation(slideUp);
                    detailslayout.setVisibility(View.VISIBLE);
                    orderslayout.setVisibility(View.GONE);

                    btnDelivery.setVisibility(View.GONE);
                    btnconfirm.setVisibility(View.VISIBLE);
                }

                break;


            }



            case R.id.confirm_details:

                pb_orderdetails.setVisibility(View.VISIBLE);
                order_detail_layout.setVisibility(View.INVISIBLE);
                if (!delivery) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("Please select a delivery time");
                    builder.setCancelable(true);
                    AlertDialog dial = builder.create();
                    dial.show();
                    return;
                }

                if (!delivery_address) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("Please select a delivery address");
                    builder.setCancelable(true);
                    AlertDialog dial = builder.create();
                    dial.show();
                    return;
                }

                final String token = getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null);
                Log.d("token",token);
                Call<CartsInfo> callCart = client.getCartDetails(token);
                callCart.enqueue(new Callback<CartsInfo>() {
                    @Override
                    public void onResponse(Call<CartsInfo> call, Response<CartsInfo> response) {
                        CartsInfo cart = response.body();
                        Log.d("response1", String.valueOf(cart));

                        onOffSwitch.setChecked(cart.getData().getXcash_onoff());

                        Log.d("subtotal",String.valueOf(cart.getXcash()));
                        subtotal = String.valueOf(Float.parseFloat(String.valueOf(cart.getData().getSubtotal())));
                        Float getxcash = Float.parseFloat(cart.getXcash().toString());
                        Log.d("subtotal",String.valueOf(cart.getData().getTotal()));
                        Log.d("subtotal",String.valueOf(cart.getXcash()));



                        if ((cart.getData().getTotal()-getxcash)>0) {
                            total = String.valueOf(Float.parseFloat(String.valueOf(cart.getData().getTotal()-getxcash)));
                        }
                        else{
                            total = "0.0";
                        }
                        taxes = String.valueOf(Float.parseFloat(cart.getData().getTaxes().toString()));
                        Log.d("subtotal",cart.getData().getTaxes().toString());


                        String del = String.valueOf(Float.parseFloat(cart.getData().getDeliveryCharges().toString()));
                        tvSubtotal.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(subtotal)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        tax.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(taxes)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        tvTotal.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(total)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        tvDelivery.setText(String.valueOf(BigDecimal.valueOf(Double.parseDouble(del)).setScale(3, RoundingMode.HALF_UP).doubleValue()));
                        xcash.setText("-"+String.valueOf(String.valueOf(BigDecimal.valueOf(Double.parseDouble(String.valueOf(getxcash))).setScale(3, RoundingMode.HALF_UP).doubleValue())));
                        pb_orderdetails.setVisibility(View.GONE);
                        order_detail_layout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<CartsInfo> call, Throwable t) {
                        Log.d("errror", String.valueOf(t));
                    }
                });
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

                if (final_detail.getVisibility() == View.GONE) {
                    final_detail.startAnimation(slideUp);
                    final_detail.setVisibility(View.VISIBLE);
                    detailslayout.setVisibility(View.GONE);
                    btnDelivery.setVisibility(View.GONE);
                    btnCheckout.setVisibility(View.VISIBLE);
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cart_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        startActivity(new Intent(CartActivity.this,SubscriptionActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




}






