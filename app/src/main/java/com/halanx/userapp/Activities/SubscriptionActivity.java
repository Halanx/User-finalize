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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener {

    String mobile;
    List<CartItem> activeItems;
    ProgressBar progressBar;
    RecyclerView rvSubscription;
    LinearLayout start_date,recharge,repeat;
    ImageView map;
    TextView change_location;
    TextView tvSubtotal,tvTotal,tvDelivery,tax;
    Button details,confirm_detail,checkout;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;


    String total,subtotal,taxes;

    LinearLayout orderslayout,detailslayout,final_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsciption_actvity);


        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);

        progressBar = (ProgressBar) findViewById(R.id.pb_subscription);
        rvSubscription = (RecyclerView) findViewById(R.id.rv_subscription);
        start_date = (LinearLayout) findViewById(R.id.start_date_selection);
        recharge = (LinearLayout) findViewById(R.id.recharge);
        repeat = (LinearLayout) findViewById(R.id.repeat);
        details = (Button) findViewById(R.id.details);
        confirm_detail = (Button) findViewById(R.id.confirm_details);
        checkout = (Button) findViewById(R.id.checkout);
        map = (ImageView) findViewById(R.id.iv_map);
        change_location = (TextView) findViewById(R.id.bt_address_locate);


        orderslayout = (LinearLayout) findViewById(R.id.orders);
        detailslayout = (LinearLayout) findViewById(R.id.detail);
        final_detail = (LinearLayout) findViewById(R.id.detail_confirm);




        tvSubtotal = (TextView) findViewById(R.id.tv_cart_subtotal);
        tvTotal = (TextView) findViewById(R.id.tv_cart_total);
        tvDelivery = (TextView) findViewById(R.id.tv_cart_deliverycharge);
        tax = (TextView) findViewById(R.id.tax);




        start_date.setOnClickListener(this);
        recharge.setOnClickListener(this);
        repeat.setOnClickListener(this);
        details.setOnClickListener(this);
        checkout.setOnClickListener(this);
        confirm_detail.setOnClickListener(this);
        change_location.setOnClickListener(this);


        SharedPreferences sharedPref = getSharedPreferences("location", Context.MODE_PRIVATE);
        float latitude = sharedPref.getFloat("latitudeDelivery", 0);// LATITUDE
        float longitude = sharedPref.getFloat("longitudeDelivery", 0);// LONGITUDE
        if (latitude != 0 && longitude != 0) {


            Log.i("Url", "https://maps.googleapis.com/maps/api/staticmap?zoom=16" +
                    "&markers=color:red%7label:C%7C" + latitude + "," + longitude + "&size=400x200&" +
                    "key=AIzaSyCj7HXOZgUqjouFVydt4irrhu3cQLqXMbg");
            Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?zoom=16" +
                    "&markers=color:red%7Clabel:%7C" + latitude + "," + longitude + "&size=400x200&" +
                    "key=AIzaSyBnCtz4CuPtcZ-87zXLsYvH1BrkTTJ9eyw").into(map);
        } else {
            map.setVisibility(View.GONE);
        }



        mobile = getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null);
        Call <List<CartItem>> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class).getUserCartItems(mobile);
        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {

                List<CartItem> items = response.body();


                if (!items.isEmpty()) {
                    Log.i("Cart",items.size()+"");
                    activeItems = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {

                        if (!items.get(i).getRemovedFromCart()) {
                            activeItems.add(items.get(i));

                        }
                    }

                    //Displaying carts
                    Log.d("TAG", activeItems.size()+"");
                    progressBar.setVisibility(View.GONE);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SubscriptionActivity.this);
                    rvSubscription.setAdapter(new SubscriptionAdapter(activeItems));
                    rvSubscription.setLayoutManager(layoutManager);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);


                    AlertDialog.Builder builder = new AlertDialog.Builder(SubscriptionActivity.this);
                    builder.setMessage("You have no items in your carts!");
                    builder.setCancelable(false);

                    builder.setPositiveButton(
                            "Go back",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });


                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.i("Cart",t.toString());
            }
        });




    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.repeat:
                final Dialog dialog = new Dialog(SubscriptionActivity.this);
                dialog.setContentView(R.layout.repat_dialog_box);


                RadioButton daily,monday,tuesday,wednesday,thursday,friday,saturday,sunday;

                daily = (RadioButton) dialog.findViewById(R.id.daily);
                monday = (RadioButton) dialog.findViewById(R.id.monday);
                tuesday = (RadioButton) dialog.findViewById(R.id.tuesday);
                wednesday = (RadioButton) dialog.findViewById(R.id.webview);
                thursday = (RadioButton) dialog.findViewById(R.id.thursday);
                friday = (RadioButton) dialog.findViewById(R.id.friday);
                saturday = (RadioButton) dialog.findViewById(R.id.saturday);
                sunday = (RadioButton) dialog.findViewById(R.id.sunday);




                Button proceed = (Button) dialog.findViewById(R.id.btProceed_dialogue);
                Button cancel = (Button) dialog.findViewById(R.id.btCancel_dialogue);
                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
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

            case R.id.recharge:


                RadioButton three,seven,fifteen,thirty;




                final Dialog dialog1 = new Dialog(SubscriptionActivity.this);
                dialog1.setContentView(R.layout.recharge_dialog_box);



                three = (RadioButton) dialog1.findViewById(R.id.time3);
                three = (RadioButton) dialog1.findViewById(R.id.time7);
                three = (RadioButton) dialog1.findViewById(R.id.time15);
                three = (RadioButton) dialog1.findViewById(R.id.time30);
                Button contin = (Button) dialog1.findViewById(R.id.btProceed_dialogue);
                Button dismiss = (Button) dialog1.findViewById(R.id.btCancel_dialogue);
                contin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

                dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });


                dialog1.show();


                break;


            case R.id.start_date_selection:

                startActivityForResult(new Intent(SubscriptionActivity.this,ScheduleActivity.class),1);
                break;


        case R.id.bt_address_locate:

        Intent intentMap = new Intent(SubscriptionActivity.this, MapsActivity.class);
        intentMap.putExtra("fromCart", true);
        startActivity(intentMap);

        break;

        case R.id.checkout:


            final Dialog dialog2 = new Dialog(SubscriptionActivity.this);
            dialog2.setContentView(R.layout.add_ammount_dialog_box);


            Button pay = (Button) dialog2.findViewById(R.id.btProceed_dialogue);
            Button exit = (Button) dialog2.findViewById(R.id.btCancel_dialogue);
            EditText amount = (EditText) dialog2.findViewById(R.id.et1_dialogue);

            
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SubscriptionActivity.this,PaymentActivity.class));
                    finish();

                }
            });

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                }
            });


            dialog2.show();


            break;
        case R.id.details: {
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
    //        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

                detailslayout.startAnimation(slideUp);
                detailslayout.setVisibility(View.VISIBLE);
                orderslayout.setVisibility(View.GONE);
                details.setVisibility(View.GONE);
                confirm_detail.setVisibility(View.VISIBLE);
            break;
        }

        case R.id.confirm_details:


        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String mobileNumber = sharedPreferences.getString("MobileNumber", null);
        Call<CartsInfo> callCart = client.getCartDetails(mobileNumber);
        callCart.enqueue(new Callback<CartsInfo>() {
            @Override
            public void onResponse(Call<CartsInfo> call, Response<CartsInfo> response) {
                CartsInfo cart = response.body();

                subtotal = cart.getSubtotal().toString();
                total = cart.getTotal().toString();
                taxes = cart.getTaxes().toString();

                String del = cart.getDeliveryCharges().toString();
                tvSubtotal.setText(subtotal);
                tax.setText(taxes);
                tvTotal.setText(total);
                tvDelivery.setText(del);

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
            details.setVisibility(View.GONE);
            checkout.setVisibility(View.VISIBLE);
        }
    }

    }




    class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionHolder>{

        List<CartItem> cartItems;
        Integer quantityList[];
        List<Boolean> isItemChecked;


        public SubscriptionAdapter(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            final int j = this.cartItems.size();
            Log.i("Cart",j+"");
            quantityList = new Integer[j];
            isItemChecked = new ArrayList<>();

            for (int i = 0; i<this.cartItems.size();i++){
                quantityList[i]=1;
//                isItemChecked.set(i,false);
            }

        }

        @Override
        public SubscriptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SubscriptionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subscription_recycler,parent,false));
        }

        @Override
        public void onBindViewHolder(SubscriptionHolder holder, int position) {

            holder.tvName.setText(cartItems.get(position).getItem().getProductName());
            holder.tvDesc.setText(cartItems.get(position).getItem().getFeatures());
            holder.tvPrice.setText("Rs. "+Double.toString(cartItems.get(position).getItem().getPrice()));

        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public class SubscriptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            TextView tvName, tvDesc, tvPrice;
            EditText etQuant;
            RelativeLayout rvInc, rvDec;
            CheckBox cbAdd;

            public SubscriptionHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.tv_cart_item_name);
                tvDesc = (TextView) itemView.findViewById(R.id.tv_cart_item_description);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_cart_item_price);
                etQuant = (EditText) itemView.findViewById(R.id.tv_cart_item_quantity);
                rvInc = (RelativeLayout) itemView.findViewById(R.id.tv_cart_item_increment);
                rvDec = (RelativeLayout) itemView.findViewById(R.id.tv_cart_item_decrement);
                cbAdd = (CheckBox) itemView.findViewById(R.id.cb_cart_item);

                rvDec.setOnClickListener(this);
                rvInc.setOnClickListener(this);

                cbAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        int pos = getAdapterPosition();
                        isItemChecked.set(pos,b);
                    }
                });
            }


            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();

                if (view==rvInc) {

                    if (quantityList[position] < 10) {
                        quantityList[position]++;
                        etQuant.setText(String.valueOf(quantityList[position]));
                    }

                } else if (view == rvDec) {

                    if (quantityList[position] > 1) {
                        quantityList[position]--;
                        etQuant.setText(String.valueOf(quantityList[position]));
                    }
                }

            }
        }
    }

}
