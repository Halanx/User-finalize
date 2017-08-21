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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartsInfo;
import com.halanx.userapp.POJO.SubscriptionInfo;
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
    LinearLayout start_date, recharge, repeat,address;
    ImageView map;
    TextView change_location;
    TextView tvSubtotal, tvTotal, tvDelivery, tax;
    Button details, confirm_detail, checkout;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;
    Boolean everyday =false;
    Boolean mon =false;
    Boolean tues =false;
    Boolean wed =false;
    Boolean thurs =false;
    Boolean fri =false;
    Boolean sat =false;
    Boolean sun =false;

    Boolean bool_three = false;
    Boolean bool_seven = false;
    Boolean bool_thirty = false;
    Boolean bool_fifteen  = false;
    String add;

    EditText line1,line2,line3;
    String total, subtotal, taxes;

    LinearLayout orderslayout, detailslayout, final_detail;

    static Integer quantityList[];
    static List<Boolean> isItemChecked;
    RadioButton daily, monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    RadioButton three, seven, fifteen, thirty; RadioGroup rgRecharge;
    SubscriptionInfo subscriptionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsciption_actvity);


        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);
        subscriptionInfo = new SubscriptionInfo();

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
        address = (LinearLayout) findViewById(R.id.bt_address_details);


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
        address.setOnClickListener(this);


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
        Call<List<CartItem>> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class).getUserCartItems(mobile);
        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {

                List<CartItem> items = response.body();


                if (!items.isEmpty()) {
                    Log.i("Cart", items.size() + "");
                    activeItems = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {

                        if (!items.get(i).getRemovedFromCart()) {
                            activeItems.add(items.get(i));

                        }
                    }

                    //Displaying carts
                    Log.d("TAG", activeItems.size() + "");
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
                Log.i("Cart", t.toString());
            }
        });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.repeat:
                final Dialog dialRepeat = new Dialog(SubscriptionActivity.this);
                dialRepeat.setContentView(R.layout.repeat_dialog_box);



                final RadioButton daily,monday,tuesday,wednesday,thursday,friday,saturday,sunday;

                daily = (RadioButton) dialRepeat.findViewById(R.id.daily);
                monday = (RadioButton) dialRepeat.findViewById(R.id.monday);
                tuesday = (RadioButton) dialRepeat.findViewById(R.id.tuesday);
                wednesday = (RadioButton) dialRepeat.findViewById(R.id.wednesday);
                thursday = (RadioButton) dialRepeat.findViewById(R.id.thursday);
                friday = (RadioButton) dialRepeat.findViewById(R.id.friday);
                saturday = (RadioButton) dialRepeat.findViewById(R.id.saturday);
                sunday = (RadioButton) dialRepeat.findViewById(R.id.sunday);


                daily.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(everyday){
                            everyday = false;
                            daily.setChecked(false);
                            mon = false;
                            tues = false;
                            wed = false;
                            thurs = false;
                            fri = false;
                            sat = false;

                            sunday.setChecked(false);
                            monday.setChecked(false);
                            tuesday.setChecked(false);
                            wednesday.setChecked(false);
                            thursday.setChecked(false);
                            friday.setChecked(false);
                            saturday.setChecked(false);
                        }
                        else{
                            everyday = true;
                            mon = true;
                            tues = true;
                            wed = true;
                            thurs = true;
                            fri = true;
                            sat = true;

                            sunday.setChecked(true);
                            monday.setChecked(true);
                            tuesday.setChecked(true);
                            wednesday.setChecked(true);
                            thursday.setChecked(true);
                            friday.setChecked(true);
                            saturday.setChecked(true);
                            sunday.setChecked(true);


                        }
                    }
                });
                 monday.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if(mon){
                             monday.setChecked(false);
                             mon = false;
                         }
                         else{
                             monday.setChecked(true);
                             mon =true;
                         }

                     }
                 });
                tuesday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tues){
                            tuesday.setChecked(false);
                            tues = false;
                        }
                        else{
                            tuesday.setChecked(true);
                            tues =true;
                        }

                    }
                });

                wednesday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(wed){
                            wednesday.setChecked(false);
                            wed = false;
                        }
                        else{
                            wednesday.setChecked(true);
                            wed =true;
                        }

                    }
                });

                thursday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(thurs){
                            thursday.setChecked(false);
                            thurs = false;
                        }
                        else{
                            thursday.setChecked(true);
                            thurs =false;
                        }

                    }
                });

                friday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(fri){
                            friday.setChecked(false);
                            fri = false;
                        }
                        else{
                            friday.setChecked(true);
                            fri =true;
                        }

                    }
                });

                saturday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(sat){
                            saturday.setChecked(false);
                            sat = false;
                        }
                        else{
                            saturday.setChecked(true);
                            sat =true;
                        }

                    }
                });
                sunday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(sun){
                            sunday.setChecked(false);
                            sun = false;


                        }
                        else{
                            sunday.setChecked(true);
                            sun =true;
                        }
                    }
                });



                Button proceed = (Button) dialRepeat.findViewById(R.id.btProceed_dialogue);
                Button cancel = (Button) dialRepeat.findViewById(R.id.btCancel_dialogue);
                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mon){
                            subscriptionInfo.setOnMonday(true);
                        }

                        if(tues){
                            subscriptionInfo.setOnTuesday(true);
                        }

                        if(wed){
                            subscriptionInfo.setOnWednesday(true);
                        }

                        if(thurs){
                            subscriptionInfo.setOnThursday(true);
                        }

                        if(fri){
                            subscriptionInfo.setOnFriday(true);
                        }

                        if(sat){
                            subscriptionInfo.setOnSaturday(true);
                        }

                        if(sun){
                            subscriptionInfo.setOnSunday(true);
                        }

                        dialRepeat.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialRepeat.dismiss();
                    }
                });
                dialRepeat.show();

                break;

            case R.id.bt_address_details:
                 final Dialog dialog = new Dialog(SubscriptionActivity.this);
                dialog.setContentView(R.layout.layout_custom_alert_dialogue);


                line1 = (EditText) dialog.findViewById(R.id.et1_dialogue);
                line2 = (EditText) dialog.findViewById(R.id.et2_dialogue);
                line3 = (EditText) dialog.findViewById(R.id.et3_dialogue);
                add = getSharedPreferences("location",Context.MODE_PRIVATE).getString("addressDelivery",null);
                if(add!=null) {
                    String[] Detail_add = add.split(",");
                    line2.setText(Detail_add[1]);
                    line3.setText(Detail_add[2]);
                }

                Button cont = (Button) dialog.findViewById(R.id.btProceed_dialogue);
                Button dis = (Button) dialog.findViewById(R.id.btCancel_dialogue);

                cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((line1.getText().equals(" ") || line2.getText().equals(" ") || line3.getText().equals(" "))) {

                            Toast.makeText(getApplicationContext(), "Enter Your Address", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Address Details Saved", Toast.LENGTH_SHORT).show();
                            String addressDetails = line1.getText().toString() + ", " + line2.getText().toString() + ", " + line3.getText().toString();
                            Log.d("TAG", addressDetails);
                            dialog.dismiss();
                        }
                    }
                });

                dis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                dialog.show();


                break;

            case R.id.recharge:

                final Dialog dialRecharge = new Dialog(SubscriptionActivity.this);
                dialRecharge.setContentView(R.layout.recharge_dialog_box);


                three = (RadioButton) dialRecharge.findViewById(R.id.time3);
                seven = (RadioButton) dialRecharge.findViewById(R.id.time7);
                fifteen = (RadioButton) dialRecharge.findViewById(R.id.time15);
                thirty = (RadioButton) dialRecharge.findViewById(R.id.time30);
                rgRecharge = (RadioGroup) dialRecharge.findViewById(R.id.rg_recharge);

                three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(bool_three){
                            three.setChecked(false);
                            bool_three=false;
                        }
                        else {
                            three.setChecked(true);
                            bool_three=true;
                        }
                    }
                });
                seven.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(bool_seven){
                            seven.setChecked(false);
                            bool_seven=false;
                        }
                        else {
                            seven.setChecked(true);
                            bool_seven=true;

                        }
                    }
                });
                fifteen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bool_fifteen){
                        fifteen.setChecked(false);
                        bool_fifteen=false;
                    }
                    else {
                        fifteen.setChecked(true);
                        bool_fifteen=true;

                    }
                }
            });
                thirty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bool_thirty){
                        thirty.setChecked(false);
                        bool_thirty=false;
                    }
                    else {
                        thirty.setChecked(true);
                        bool_thirty=true;

                    }
                }
            });


                Button contin = (Button) dialRecharge.findViewById(R.id.btProceed_dialogue);
                Button dismiss = (Button) dialRecharge.findViewById(R.id.btCancel_dialogue);

                contin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch (rgRecharge.getCheckedRadioButtonId()){
                            case R.id.time3 :
                                subscriptionInfo.setDeliveriesLeft(3);
                                break;

                            case R.id.time7:
                                subscriptionInfo.setDeliveriesLeft(7);
                                break;

                            case R.id.time15 :
                                subscriptionInfo.setDeliveriesLeft(15);
                                break;

                            case R.id.time30 :
                                subscriptionInfo.setDeliveriesLeft(30);
                                break;

                        }

                        dialRecharge.dismiss();

                    }
                });

                dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialRecharge.dismiss();
                    }
                });

                dialRecharge.show();

                break;


            case R.id.start_date_selection:

                startActivityForResult(new Intent(SubscriptionActivity.this, ScheduleActivity.class), 1);
                break;


            case R.id.bt_address_locate:

                Intent intentMap = new Intent(SubscriptionActivity.this, MapsActivity.class);
//                intentMap.putExtra("fromCart", true);
                startActivity(intentMap);

                break;

            case R.id.checkout:


                final Dialog dialog2 = new Dialog(SubscriptionActivity.this);
                dialog2.setContentView(R.layout.add_ammount_dialog_box);


                Button pay = (Button) dialog2.findViewById(R.id.btProceed_dialogue);
                Button exit = (Button) dialog2.findViewById(R.id.btCancel_dialogue);
                EditText amount = (EditText) dialog2.findViewById(R.id.et1_dialogue);

                dialog2.show();
                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String objJson = new GsonBuilder().create().toJson(subscriptionInfo);
                        dialog2.dismiss();
                        startActivity(new Intent(SubscriptionActivity.this, PaymentActivity.class).putExtra("Subscription",objJson));
                        finish();

                    }
                });

                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });

                break;
            case R.id.details: {
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

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

                if (final_detail.getVisibility() == View.GONE) {

                    final_detail.startAnimation(slideUp);
                    final_detail.setVisibility(View.VISIBLE);
                    detailslayout.setVisibility(View.GONE);
                    details.setVisibility(View.GONE);
                    checkout.setVisibility(View.VISIBLE);
                }
        }

    }


    class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionHolder> {

        List<CartItem> cartItems;



        public SubscriptionAdapter(List<CartItem> cartItems) {
            this.cartItems = cartItems;

            quantityList = new Integer[cartItems.size()];
            isItemChecked = new ArrayList<>();

            for(int i = 0; i<cartItems.size();i++){
                quantityList[i] = 1;
                isItemChecked.add(i,false);
            }
        }

        @Override
        public SubscriptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SubscriptionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subscription_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(SubscriptionHolder holder, int position) {

            holder.tvName.setText(cartItems.get(position).getItem().getProductName());
            holder.tvDesc.setText(cartItems.get(position).getItem().getFeatures());
            holder.tvPrice.setText("Rs. " + Double.toString(cartItems.get(position).getItem().getPrice()));

        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public class SubscriptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
                        isItemChecked.set(pos, b);
                    }
                });
            }


            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();

                if (view == rvInc) {

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
