package com.halanx.userapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.halanx.userapp.Activities.HomeActivity;
import com.halanx.userapp.Activities.ItemDisplayActivity;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartItemPost;
import com.halanx.userapp.POJO.GroupItemPost;
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

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

/**
 * Created by Nishant on 15/08/17.
 */

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.ProductViewHolder>{

    JSONObject products,specific_detail;
    private static int restQuantity[];
    private Context c;
    public String mobileNumber;
    Boolean already= false;

    ProductSearchAdapter.ProductViewHolder holder;
    TextView itemCount;
    String data;
    int cartId;
    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;
    String groupid;
    Boolean group_member;
    int proId;



    public ProductSearchAdapter(JSONObject products, Context c, String storeCat, String mobileNumber, TextView itemCount) throws JSONException {
        this.products = products;
        Log.d("jsonobject", String.valueOf(products));
        this.c = c;

        this.mobileNumber = mobileNumber;
        this.itemCount = itemCount;
        Log.d("textvie", (String) itemCount.getText());
        restQuantity = new int[products.getInt("ProductSize")];
        for (int i = 0; i < products.getInt("ProductSize"); i++) {
            restQuantity[i] = 1;
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_recycler, parent, false);
        ProductViewHolder holder = new ProductViewHolder(view, products, c,itemCount);
        return holder;

    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {

        try {
            final String token = c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");

            builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).
                    addConverterFactory(GsonConverterFactory.create());
            retrofit = builder.build();

            client = retrofit.create(DataInterface.class);


            Call<UserInfo> call = client.getUserInfo(token);
            call.enqueue(new Callback<com.halanx.userapp.POJO.UserInfo>() {
                @Override
                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {

                    if (String.valueOf(response.body().getgroupcart()).equals("null")){
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



            if (products.getString("StoreId").equals("1")) {
                holder.cvProducts.setVisibility(View.VISIBLE);
                holder.cvRest.setVisibility(View.GONE);

                String url = djangoBaseUrl +"products/" + products.getString("Id");
                Log.d("stringurl",url);
                JSONObject obj = new JSONObject();

                Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.GET, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        specific_detail = response;
                        Log.d("specific detail", String.valueOf(specific_detail));
                        try {
                            data = specific_detail.getString("ProductImage");
                            Picasso.with(c).load(data).into(holder.productImage);
                            Log.d("data",data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }));
                try {
                    holder.productName.setText(products.getString("ProductName"));
                    if (products.getString("Features") != null) {
                        holder.description.setVisibility(View.VISIBLE);
                        holder.description.setText(products.getString("Features"));
                    }
                    holder.productPrice.setText("₹ " + String.valueOf(products.get("Price")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Picasso.with(c).load(R.drawable.fav_48).into(holder.productImage);
                holder.cvProducts.setVisibility(View.GONE);
                holder.cvRest.setVisibility(View.VISIBLE);
                try {
                    if(products.get("Features")!=null){
                        holder.description.setVisibility(View.VISIBLE);
                        holder.description.setText(products.getString("Features"));
                    }
                    holder.tvRestName.setText(products.getString("ProductName"));
                    holder.tvRestPrice.setText("₹ " + String.valueOf(products.getString("Price")));
                   holder.etRestQuan.setText(String.valueOf(restQuantity[position]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.cvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //        holder.pb_addtocart.setVisibility(View.VISIBLE);
          //      holder.tvCart.setVisibility(View.GONE);
                //Add to cart
                Log.d("position_selected", String.valueOf(position));
                Double val = Double.parseDouble(holder.etRestQuan.getText().toString());
                try {
                    proId = products.getInt("Id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Long mob = Long.parseLong(mobileNumber);
                //              addCartItem(val, proId);

                Log.d("group_member", String.valueOf(group_member));
                if(!group_member) {

                    final Double finalVal1 = val;
                    Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.GET, "" + djangoBaseUrl + "carts/detail/", null, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                cartId = response.getInt("id");


                                final String token = c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", "token1");
                                Log.d("token", token);
                                CartItemPost item = new CartItemPost(cartId, finalVal1, proId, null);


                                Call<CartItemPost> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class).putCartItemOnServer(item, token);
                                call.enqueue(new Callback<CartItemPost>() {
                                    @Override
                                    public void onResponse(Call<CartItemPost> call, Response<CartItemPost> response) {

                                        Log.d("done", "donepb");

//                                        holder.pb_addtocart.setVisibility(View.GONE);
                                        holder.tvCart.setVisibility(View.VISIBLE);
                                        holder.tvCart.setText("Added to cart");

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
                                                    notifyDataSetChanged();

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
                                        Toast.makeText(c, "Failed to add to cart", Toast.LENGTH_SHORT).show();
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
                            params.put("Authorization", c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", null));
                            return params;
                        }

                    });

                }
                else if (group_member){

                    // GROUP CART

                    final String token = c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token", "token1");
                    Log.d("token", token);
                    Log.d("dataitems", String.valueOf(val)+","+proId);
                    GroupItemPost item = new GroupItemPost(val, proId, null);


                    Call<GroupItemPost> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class).putGroupItemOnServer(item, token);
                    call.enqueue(new Callback<GroupItemPost>() {
                        @Override
                        public void onResponse(Call<GroupItemPost> call, Response<GroupItemPost> response) {

                            Log.d("done", String.valueOf(groupid));

                        //    holder.pb_addtocart.setVisibility(View.GONE);
                            holder.tvCart.setVisibility(View.VISIBLE);
                            holder.tvCart.setText("Added to cart");


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
                                        notifyDataSetChanged();

                                    } else {
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<CartItem>> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<GroupItemPost> call, Throwable t) {
                            Toast.makeText(c, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

        });
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
        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    holder.productFav(Integer.valueOf(products.getString("Id")), "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.cvProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ItemDisplayActivity.class);
                try {

                    Log.d(("item_data"), String.valueOf(products));
                    intent.putExtra("Name", products.getString("ProductName"));
                    intent.putExtra("Price", products.getDouble("Price"));
                    if (products.getString("Features") != null) {
                        intent.putExtra("Features", products.getString("Features"));

                    }
                    intent.putExtra("Image", data);

                    intent.putExtra("ID", products.getInt("Id"));
                    c.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return 1;
    }



    public class ProductViewHolder extends RecyclerView.ViewHolder {

        CardView cvProducts, cvRest;
        ImageView productImage;
        TextView productName, productPrice;
        JSONObject products;
        Context c;

        TextView description;
        //For rest
        TextView tvRestName, tvRestPrice;
        CardView cvAddCart;
        EditText etRestQuan;
        RelativeLayout rvInc, rvDec;
        TextView tvCart;
        ImageView ivFav;
        TextView itemCount;

        public ProductViewHolder(View itemView, JSONObject products, Context c, TextView itemCount) {
            super(itemView);
            this.products = products;
            this.c = c;
            this.itemCount = itemCount;


            cvProducts = (CardView) itemView.findViewById(R.id.cvProducts);
            cvRest = (CardView) itemView.findViewById(R.id.cvRest);
            productImage = (ImageView) itemView.findViewById(R.id.itemImage);
            productName = (TextView) itemView.findViewById(R.id.itemName);
            productPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            description  = (TextView) itemView.findViewById(R.id.description);
            tvRestName = (TextView) itemView.findViewById(R.id.restName);
            tvRestPrice = (TextView) itemView.findViewById(R.id.restPrice);
            cvAddCart = (CardView) itemView.findViewById(R.id.cv_rest_add_cart);
            etRestQuan = (EditText) itemView.findViewById(R.id.restQuantity);
            rvDec = (RelativeLayout) itemView.findViewById(R.id.restDecrement);
            rvInc = (RelativeLayout) itemView.findViewById(R.id.restIncrement);
            tvCart = (TextView) itemView.findViewById(R.id.tv_add_to_cart);
            ivFav = (ImageView) itemView.findViewById(R.id.restFav);

        }


        private void productFav(Integer productID, final String option) {
            //option is 0 or 1 -
            //1 for adding , 0 for removing

            String url = djangoBaseUrl +"users/favs/"+ option + "/";
            JSONObject obj = new JSONObject();
            try {
                obj.put("LastItem", productID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (option.equals("1")) {
                        Picasso.with(c).load(R.drawable.fav_filled_48).into(ivFav);
                        Toast.makeText(c, "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                    }

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(c, "Network error", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", c.getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
                    return params;
                }

            });
        }

    }


}