package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItemPost;
import com.halanx.userapp.POJO.ProductInfo;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView rvFavs;
    String mobileNumber;
    ProgressBar pbFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mobileNumber = getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null);
        pbFav = (ProgressBar) findViewById(R.id.pb_fav);

        pbFav.setVisibility(View.VISIBLE);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        DataInterface cl = retrofit.create(DataInterface.class);
        Call<UserInfo> call = cl.getUserInfo(mobileNumber);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {


                pbFav.setVisibility(View.GONE);
                List<ProductInfo> favPros = response.body().getFavItems();
                if (favPros != null && favPros.size() > 0) {
                    rvFavs = (RecyclerView) findViewById(R.id.rv_favorites);
                    FavsAdapter adapter = new FavsAdapter(favPros);
                    RecyclerView.LayoutManager man = new LinearLayoutManager(FavouritesActivity.this);
                    rvFavs.setLayoutManager(man);
                    rvFavs.setHasFixedSize(true);
                    rvFavs.setAdapter(adapter);


                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(FavouritesActivity.this);
                    builder.setTitle("You have no favorites!");
                    builder.setMessage("Products added to favorites will be displayed here");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

                    AlertDialog dial = builder.create();
                    dial.show();
                }

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.e("Fav", "Fail");
            }
        });

    }

    private class FavsAdapter extends RecyclerView.Adapter<FavsAdapter.FavsHolder> {
        int pos;
        List<ProductInfo> favPros = new ArrayList<>();

        public FavsAdapter(List<ProductInfo> favPros) {
            this.favPros = favPros;
        }

        @Override
        public FavsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FavsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_favs_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(FavsHolder holder, int position) {

            holder.favName.setText(favPros.get(position).getProductName());
            Picasso.with(FavouritesActivity.this).load(favPros.get(position).getProductImage()).into(holder.favImage);
            String price = "Rs. " + favPros.get(position).getPrice();
            holder.favPrice.setText(price);


        }

        @Override
        public int getItemCount() {
            return favPros.size();
        }

        public class FavsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView favImage,btnDelete;
            TextView favName, favPrice;
            Spinner favQuantity;
            Button btAddCart;

            public FavsHolder(View itemView) {
                super(itemView);
                btnDelete = (ImageView) itemView.findViewById(R.id.bt_product_delete);
                favImage = (ImageView) itemView.findViewById(R.id.iv_product_image);
                favName = (TextView) itemView.findViewById(R.id.tv_product_name);
                favPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
                favQuantity = (Spinner) itemView.findViewById(R.id.sp_product_quantity);
                btAddCart = (Button) itemView.findViewById(R.id.bt_add_cart);

                btnDelete.setOnClickListener(this);
                btAddCart.setOnClickListener(this);


            }

            @Override
            public void onClick(View view) {


                pos = getAdapterPosition();
                switch (view.getId()) {
                    case R.id.bt_product_delete: {

                        String url = "https://api.halanx.com/users/favs/" + mobileNumber + "/0/";
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("LastItem", favPros.get(pos).getId());



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Volley.newRequestQueue(FavouritesActivity.this).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                favPros.remove(pos);
                                notifyDataSetChanged();
                                Toast.makeText(FavouritesActivity.this, "Removed from Favourites", Toast.LENGTH_SHORT).show();

                            }
                        }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(FavouritesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            }
                        }));

                        break;
                    }


                    case R.id.bt_add_cart: {

                        CartItemPost item = new CartItemPost(Long.parseLong(mobileNumber),favQuantity.getSelectedItemPosition()+1.0, favPros.get(pos).getId(), null);
                        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                                addConverterFactory(GsonConverterFactory.create());

                        Retrofit retrofit = builder.build();
                        DataInterface client = retrofit.create(DataInterface.class);

                        Call<CartItemPost> call = client.putCartItemOnServer(item);
                        call.enqueue(new Callback<CartItemPost>() {
                            @Override
                            public void onResponse(Call<CartItemPost> call, Response<CartItemPost> response) {


                                Toast.makeText(FavouritesActivity.this, "Added item " + favPros.get(pos).getProductName() + "to your cart!", Toast.LENGTH_SHORT).show();
                                btAddCart.setText("Added to cart");
                                btAddCart.setTextColor(Color.parseColor("#fafafa"));
                                btAddCart.setBackgroundColor(Color.parseColor("#b6413f"));

                            }

                            @Override
                            public void onFailure(Call<CartItemPost> call, Throwable t) {

                            }
                        });
                        break;
                    }
                }
            }

        }


    }
}
