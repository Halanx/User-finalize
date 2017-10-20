package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartItemPost;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;
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

import static com.halanx.userapp.Activities.HomeActivity.itemCount;
import static com.halanx.userapp.GlobalAccess.testUrl;


public class ItemDisplayActivity extends AppCompatActivity {
    EditText etQuantity;
    TextView plus, minus;
    Boolean already = false;
    Button cart;
    int i;
    String val;
    ImageView iv_fav, iv_productImage;
    TextView tv_productName, tv_productPrice;
    Boolean isFav;
    Integer productID;
    TextView itemcount;
    int cartId;
    ProgressBar add_cart;

    String productName, productFeatures, productImage;
    Double productPrice;
    String mobileNumber;
    List<CartItem> Citems;
    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;
    Boolean group_member;
    String groupid;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String token = getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("MobileNumber", null);

        builder = new Retrofit.Builder().baseUrl(testUrl).
                addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();

        client = retrofit.create(DataInterface.class);



        Call<UserInfo> call = client.getUserInfo(token);
        call.enqueue(new Callback<com.halanx.userapp.POJO.UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {

                if (String.valueOf(response.body().getgroupcart()).equals(null)){
                    group_member =false;
                }
                else{
                    group_member = true;
                    groupid = response.body().getgroupcart();
                }


            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }

        });


        //GET PRODUCT DATA VIA INTENT
        productName = getIntent().getStringExtra("Name");
        productPrice = getIntent().getDoubleExtra("Price", 0.0);
        productFeatures = getIntent().getStringExtra("Features");
        productImage = getIntent().getStringExtra("Image");
        productID = getIntent().getIntExtra("ID", -10);

        setTitle(productName);
        setContentView(R.layout.activity_item_display);

        itemcount = (TextView) findViewById(R.id.itemcount);

        add_cart = (ProgressBar) findViewById(R.id.pb_addtocart);
        etQuantity = (EditText) findViewById(R.id.quantity);
        etQuantity.setText("1");
        plus = (TextView) findViewById(R.id.increment);
        minus = (TextView) findViewById(R.id.decrement);
        cart = (Button) findViewById(R.id.bt_add_to_cart);
        iv_fav = (ImageView) findViewById(R.id.imgFav);

        iv_productImage = (ImageView) findViewById(R.id.product_image);
        tv_productName = (TextView) findViewById(R.id.item_name);
        tv_productPrice = (TextView) findViewById(R.id.item_price);

        if (!(productImage.isEmpty())) {
            Picasso.with(getApplicationContext()).load(productImage).into(iv_productImage);
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.fav_48).into(iv_productImage);
        }

        tv_productName.setText(productName);
        String price = "₹ " + Double.toString(productPrice);
        tv_productPrice.setText(price);
        isFav = false;


        val = etQuantity.getText().toString();
        try {
            i = Integer.parseInt(val);
        } catch (NumberFormatException a) {
            a.printStackTrace();
        }

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i < 10) {
                    i++;
                    val = Integer.toString(i);
                    etQuantity.setText(val);
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i != 0) {
                    i--;
                    val = Integer.toString(i);
                    etQuantity.setText(val);
                }
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!already) {

                    add_cart.setVisibility(View.VISIBLE);
                    cart.setVisibility(View.GONE);
                    addCartItem();

                } else {
                    Toast.makeText(getApplicationContext(), "Already added to cart", Toast.LENGTH_SHORT).show();
                }

            }
        });
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFav) {
                    isFav = true;
                    Picasso.with(getApplicationContext()).load(R.drawable.fav_filled_48).into(iv_fav);
                    productFav(productID, "1");

                } else {
                    isFav = false;
                    Picasso.with(getApplicationContext()).load(R.drawable.fav_48).into(iv_fav);
                    productFav(productID, "0");

                }

            }
        });


    }



    private void productFav(Integer productID, final String option) {
        //option is 0 or 1 -
        //1 for adding , 0 for removing

        String url = ""+testUrl+"/users/favs/" + option + "/";
        JSONObject obj = new JSONObject();
        try {
            obj.put("LastItem", productID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Volley.newRequestQueue(ItemDisplayActivity.this).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (option.equals("1")) {
                } else {
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ItemDisplayActivity.this, "Network error", Toast.LENGTH_SHORT).show();
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


    void addCartItem() {


        if (!group_member) {

            Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, "" + testUrl + "/carts/detail/", null, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        cartId = response.getInt("id");


                        final String token = getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", "token1");
                        Log.d("token", token);
                        CartItemPost item = new CartItemPost(cartId, Double.parseDouble(val), productID, null);


                        Call<CartItemPost> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(testUrl).build().create(DataInterface.class).putCartItemOnServer(item, token);
                        call.enqueue(new Callback<CartItemPost>() {
                            @Override
                            public void onResponse(Call<CartItemPost> call, Response<CartItemPost> response) {

                                Log.d("done", "donepb");

                                add_cart.setVisibility(View.GONE);
                                cart.setVisibility(View.VISIBLE);
                                cart.setText("Added to cart");

                                Call<List<CartItem>> callItems = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(testUrl).build().create(DataInterface.class)
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
                            }

                            @Override
                            public void onFailure(Call<CartItemPost> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
    }
}
