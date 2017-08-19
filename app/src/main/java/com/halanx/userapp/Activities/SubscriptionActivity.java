package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.halanx.userapp.Adapters.CartsAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

public class SubscriptionActivity extends AppCompatActivity {

    String mobile;
    List<CartItem> activeItems;
    ProgressBar progressBar;
    RecyclerView rvSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsciption_actvity);

        progressBar = (ProgressBar) findViewById(R.id.pb_subscription);
        rvSubscription = (RecyclerView) findViewById(R.id.rv_subscription);

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
            return new SubscriptionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subscription_recycler,parent));
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
                tvName = (TextView) findViewById(R.id.tv_cart_item_name);
                tvDesc = (TextView) findViewById(R.id.tv_cart_item_description);
                tvPrice = (TextView) findViewById(R.id.tv_cart_item_price);
                etQuant = (EditText) findViewById(R.id.tv_cart_item_quantity);
                rvInc = (RelativeLayout) findViewById(R.id.tv_cart_item_increment);
                rvDec = (RelativeLayout) findViewById(R.id.tv_cart_item_decrement);
                cbAdd = (CheckBox) findViewById(R.id.cb_cart_item);

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
