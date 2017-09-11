package com.halanx.userapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.halanx.userapp.Adapters.CartsAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartsInfo;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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

    String addressDetails;
    EditText line1, line2, line3;
    ProgressBar pb_orderdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);

        ivMap = (ImageView) findViewById(R.id.iv_map);
        btDelAsap = (Button) findViewById(R.id.bt_delivery_asap);
        btDelSchedule = (Button) findViewById(R.id.bt_delivery_schedule);
        btAddDetails = (Button) findViewById(R.id.bt_address_details);
        btAddLocate = (TextView) findViewById(R.id.bt_address_locate);
        tvSubscription = (TextView) findViewById(R.id.tv_subs);
        pb_orderdetails = (ProgressBar) findViewById(R.id.progressbarorderdetails);

        btnDelivery = (Button) findViewById(R.id.details);
        order_detail_layout = (LinearLayout) findViewById(R.id.orderdetaillayout);


        btnCheckout = (Button) findViewById(R.id.checkout);
        orderslayout = (LinearLayout) findViewById(R.id.orders);
        detailslayout = (LinearLayout) findViewById(R.id.detail);
        final_detail = (LinearLayout) findViewById(R.id.confirm_detail);

        tax = (TextView) findViewById(R.id.tax);

        btDelAsap.setOnClickListener(this);
        btDelSchedule.setOnClickListener(this);
        btAddLocate.setOnClickListener(this);
        btAddDetails.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);
        btnDelivery.setOnClickListener(this);
        tvSubscription.setOnClickListener(this);


        tvSubtotal = (TextView) findViewById(R.id.tv_cart_subtotal);
        tvTotal = (TextView) findViewById(R.id.tv_cart_total);
        tvDelivery = (TextView) findViewById(R.id.tv_cart_deliverycharge);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cart);

        btnconfirm = (Button) findViewById(R.id.confirm_details);
        btnconfirm.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);
        btnCheckout.setOnClickListener(this);
        // btnDelivery.setOnClickListener(this);

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




        final String token = getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");
        Log.d("token",token);

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
                    adapterTemp = new CartsAdapter(activeItems, getApplicationContext());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setAdapter(adapterTemp);
                    recyclerView.setLayoutManager(layoutManager);

                } else {
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


                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                }


            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {

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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bt_delivery_asap:


                delivery = true;
                delivery_scheduled = false;
                btDelAsap.setBackground(getDrawable(R.color.red));
                btDelSchedule.setBackground(getDrawable(R.drawable.my_button_bg));


                break;

            case R.id.bt_delivery_schedule:

                delivery = true;
                btDelAsap.setBackground(getDrawable(R.drawable.my_button_bg));
                btDelSchedule.setBackground(getDrawable(R.color.red));
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
               if(add!=null) {
                   String[] Detail_add = add.split(",");
                   line2.setText(Detail_add[1]);
                   line3.setText(Detail_add[2]);
               }

                Button proceed = (Button) dialog.findViewById(R.id.btProceed_dialogue);
                Button cancel = (Button) dialog.findViewById(R.id.btCancel_dialogue);

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((line1.getText().equals(" ") || line2.getText().equals(" ") || line3.getText().equals(" "))) {

                            Toast.makeText(getApplicationContext(), "Enter Your Address", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Address Details Saved", Toast.LENGTH_SHORT).show();
                            addressDetails = line1.getText().toString() + ", " + line2.getText().toString() + ", " + line3.getText().toString();
                            Log.d("TAG", addressDetails);
                            dialog.dismiss();
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
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

                if (detailslayout.getVisibility() == View.GONE) {

                    detailslayout.startAnimation(slideUp);
                    detailslayout.setVisibility(View.VISIBLE);
                    orderslayout.setVisibility(View.GONE);

                    btnDelivery.setVisibility(View.GONE);
                    btnconfirm.setVisibility(View.VISIBLE);




                }

                break;


            }

            case R.id.tv_subs:
                startActivity(new Intent(CartActivity.this,SubscriptionActivity.class));
                break;


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

                String token = getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null);
                Log.d("token",token);
                Call<CartsInfo> callCart = client.getCartDetails(token);
                callCart.enqueue(new Callback<CartsInfo>() {
                    @Override
                    public void onResponse(Call<CartsInfo> call, Response<CartsInfo> response) {
                        CartsInfo cart = response.body();

                        Log.d("subtotal",cart.getSubtotal().toString());
                        subtotal = cart.getSubtotal().toString();
                        total = cart.getTotal().toString();
                        taxes = cart.getTaxes().toString();

                        String del = cart.getDeliveryCharges().toString();
                        tvSubtotal.setText(subtotal);
                        tax.setText(taxes);
                        tvTotal.setText(total);
                        tvDelivery.setText(del);

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
}






