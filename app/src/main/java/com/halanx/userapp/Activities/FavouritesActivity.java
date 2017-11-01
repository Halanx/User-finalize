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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    int cartId;
    private static int restQuantity[];

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
        Call<UserInfo> call = cl.getUserInfo(getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token",null));
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
            restQuantity = new int[favPros.size()];
            for (int i = 0; i < favPros.size(); i++) {
                restQuantity[i] = 1;
            }


        }

        @Override
        public FavsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FavsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_favs_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(final FavsHolder holder, final int position) {

            holder.favName.setText(favPros.get(position).getProductName());
            Picasso.with(FavouritesActivity.this).load(favPros.get(position).getProductImage()).into(holder.favImage);
            String price = "Rs. " + favPros.get(position).getPrice();
            holder.favPrice.setText(price);

            holder.rvInc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (restQuantity[position] < 10) {
                        restQuantity[position]++;
                        holder.etRestQuan.setText(String.valueOf(restQuantity[position]));
                    }
                }
            });
            holder.rvDec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (restQuantity[position] > 1) {
                        restQuantity[position]--;
                        holder.etRestQuan.setText(String.valueOf(restQuantity[position]));
                    }
                }
            });


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
            EditText etRestQuan;
            RelativeLayout rvInc, rvDec;

            public FavsHolder(View itemView) {
                super(itemView);
                btnDelete = (ImageView) itemView.findViewById(R.id.bt_product_delete);
                favImage = (ImageView) itemView.findViewById(R.id.iv_product_image);
                favName = (TextView) itemView.findViewById(R.id.tv_product_name);
                favPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
                favQuantity = (Spinner) itemView.findViewById(R.id.sp_product_quantity);
                btAddCart = (Button) itemView.findViewById(R.id.bt_add_cart);
                etRestQuan = (EditText) itemView.findViewById(R.id.restQuantity);
                rvDec = (RelativeLayout) itemView.findViewById(R.id.restDecrement);
                rvInc = (RelativeLayout) itemView.findViewById(R.id.restIncrement);

                btnDelete.setOnClickListener(this);
                btAddCart.setOnClickListener(this);


            }

            @Override
            public void onClick(View view) {


                pos = getAdapterPosition();
                switch (view.getId()) {
                    case R.id.bt_product_delete: {

                        String url = djangoBaseUrl +"users/favs/0/";
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
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type", "application/json");
                                params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                                return params;
                            }

                        });

                        break;
                    }


                    case R.id.bt_add_cart: {


                        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, djangoBaseUrl +"carts/detail/", null, new com.android.volley.Response.Listener<JSONObject>() {
                                                                                @Override
                                                                                public void onResponse(JSONObject response) {
                                                                                    try {
                                                                                        cartId = response.getJSONObject("data").getInt("id");
                                                                                        final String token = getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", "token1");
                                                                                        Log.d("token", token);
                                                                                        Log.d("token", String.valueOf(cartId));
                                                                                        Log.d("token", String.valueOf(etRestQuan.getText()));
                                                                                        Log.d("token", String.valueOf(favPros.get(pos).getId()));

                                                                                        CartItemPost item = new CartItemPost(cartId,Double.parseDouble(String.valueOf(etRestQuan.getText())), favPros.get(pos).getId(), null);

                                                                                        Call<CartItemPost> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class).putCartItemOnServer(item, token);
                                                                                        call.enqueue(new Callback<CartItemPost>() {
                                                                                            @Override
                                                                                            public void onResponse(Call<CartItemPost> call, Response<CartItemPost> response) {

                                                                                                btAddCart.setText("Added to cart");
                                                                                                btAddCart.setTextColor(Color.parseColor("#fafafa"));
                                                                                                btAddCart.setBackgroundColor(Color.parseColor("#b6413f"));
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
                                                                            })
                                                                            {
                                                                                @Override
                                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                                    params.put("Content-Type", "application/json");
                                                                                    params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));
                                                                                    return params;
                                                                                }
                                                                            }

                        );

                        break;
                    }
                }
            }

        }


    }
}
