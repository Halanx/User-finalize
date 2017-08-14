package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartItemPost;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;


public class ItemDisplayActivity extends AppCompatActivity implements View.OnClickListener {
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

    String productName, productFeatures, productImage;
    Double productPrice;
    String mobileNumber;
    List<CartItem> Citems;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString("MobileNumber", null);

        //GET PRODUCT DATA VIA INTENT
        productName = getIntent().getStringExtra("Name");
        productPrice = getIntent().getDoubleExtra("Price", 0.0);
        productFeatures = getIntent().getStringExtra("Features");
        productImage = getIntent().getStringExtra("Image");
        productID = getIntent().getIntExtra("ID", -10);

        setTitle(productName);
        setContentView(R.layout.activity_item_display);

        itemcount = (TextView) findViewById(R.id.itemcount);

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

        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        cart.setOnClickListener(this);
        iv_fav.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.increment:

                if (i < 10) {
                    i++;
                    val = Integer.toString(i);
                    etQuantity.setText(val);
                }
                break;

            case R.id.decrement:
                if (i != 0) {
                    i--;
                    val = Integer.toString(i);
                    etQuantity.setText(val);
                }

                break;
            case R.id.bt_add_to_cart:

                if (!already) {

                    addCartItem();

                } else {
                    Toast.makeText(getApplicationContext(), "Already added to cart", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imgFav:

                if (!isFav) {
                    isFav = true;
                    Picasso.with(getApplicationContext()).load(R.drawable.fav_filled_48).into(iv_fav);
                    productFav(productID, "1");

                } else {
                    isFav = false;
                    Picasso.with(getApplicationContext()).load(R.drawable.fav_48).into(iv_fav);
                    productFav(productID, "0");

                }

                break;


        }
    }


    private void productFav(Integer productID, final String option) {
        //option is 0 or 1 -
        //1 for adding , 0 for removing

        String url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/users/favs/" + mobileNumber + "/" + option + "/";
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
        }));
    }


    void addCartItem() {


        CartItemPost item = new CartItemPost(Long.parseLong(mobileNumber), Double.parseDouble(val), productID, null);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        final DataInterface client = retrofit.create(DataInterface.class);

        Call<CartItemPost> call = client.putCartItemOnServer(item);
        call.enqueue(new Callback<CartItemPost>() {
            @Override
            public void onResponse(Call<CartItemPost> call, Response<CartItemPost> response) {
                already = true;

                cart.setText("Added to cart");
            }
            @Override
            public void onFailure(Call<CartItemPost> call, Throwable t) {

                Toast.makeText(ItemDisplayActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }


        });
    }
}
