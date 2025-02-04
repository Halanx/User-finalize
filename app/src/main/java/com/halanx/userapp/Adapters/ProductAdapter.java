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
import android.widget.ProgressBar;
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
import com.halanx.userapp.POJO.ProductInfo;
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

/**
 * Created by samarthgupta on 23/05/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductInfo> products = new ArrayList<ProductInfo>();
    private static int restQuantity[];
    private Context c;
    private String storeCategory;
    public String mobileNumber;
    List<CartItem> items;
    DataInterface client;
    TextView itemCount;
    ProductViewHolder holder;
    String catetgory_text;

    int cartId;
    public ProductAdapter(List<ProductInfo> products, Context c, String storeCat, String mobileNumber, TextView itemCount, String text) {
        this.products = products;
        Log.d("textvie", String.valueOf(products));

        this.c = c;
        storeCategory = storeCat;
        catetgory_text = text;
        this.mobileNumber = mobileNumber;
        this.itemCount = itemCount;
        Log.d("textvie", (String) itemCount.getText());
        restQuantity = new int[products.size()];
        for (int i = 0; i < products.size(); i++) {
            restQuantity[i] = 1;
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_recycler, parent, false);
        holder = new ProductViewHolder(view, products, c,itemCount);
        return holder;

    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {

        Log.d("selected_position", String.valueOf(position));
        if(catetgory_text!=null){
            if(products.get(position).getCategory().equals(catetgory_text)) {
                if (storeCategory.equals("Grocery")) {
                    holder.cvProducts.setVisibility(View.VISIBLE);
                    holder.cvRest.setVisibility(View.GONE);
                    Picasso.with(c).load(products.get(position).getProductImage()).into(holder.productImage);
                    holder.productName.setText(products.get(position).getProductName());
                    if (products.get(position).getFeatures() != null) {
                        holder.description.setVisibility(View.VISIBLE);
                        holder.description.setText(products.get(position).getFeatures());
                    }
                    holder.productPrice.setText("₹ " + String.valueOf(products.get(position).getPrice()));
                } else if (storeCategory.equals("Food")) {
                    //Picasso.with(c).load(R.drawable.fav_48).into(holder.productImage);
                    holder.cvProducts.setVisibility(View.GONE);
                    holder.cvRest.setVisibility(View.VISIBLE);
                    if (products.get(position).getFeatures() != null) {
                        holder.description.setVisibility(View.VISIBLE);
                        holder.description.setText(products.get(position).getFeatures());
                    }
                    holder.tvRestName.setText(products.get(position).getProductName());
                    holder.tvRestPrice.setText("₹ " + String.valueOf(products.get(position).getPrice()));
                    holder.etRestQuan.setText(String.valueOf(restQuantity[position]));
                }
            }
        }
        else {
            if (storeCategory.equals("Grocery")) {
                holder.cvProducts.setVisibility(View.VISIBLE);
                holder.cvRest.setVisibility(View.GONE);
                Picasso.with(c).load(products.get(position).getProductImage()).into(holder.productImage);
                holder.productName.setText(products.get(position).getProductName());
                if (products.get(position).getFeatures() != null) {
                    holder.description.setVisibility(View.VISIBLE);
                    holder.description.setText(products.get(position).getFeatures());
                }
                holder.productPrice.setText("₹ " + String.valueOf(products.get(position).getPrice()));
            } else if (storeCategory.equals("Food")) {
                //Picasso.with(c).load(R.drawable.fav_48).into(holder.productImage);
                holder.cvProducts.setVisibility(View.GONE);
                holder.cvRest.setVisibility(View.VISIBLE);
                if (products.get(position).getFeatures() != null) {
                    holder.description.setVisibility(View.VISIBLE);
                    holder.description.setText(products.get(position).getFeatures());
                }
                holder.tvRestName.setText(products.get(position).getProductName());
                holder.tvRestPrice.setText("₹ " + String.valueOf(products.get(position).getPrice()));
                holder.etRestQuan.setText(String.valueOf(restQuantity[position]));
            }

        }
        holder.cvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.pb_addtocart.setVisibility(View.VISIBLE);
                holder.tvCart.setVisibility(View.GONE);
                //Add to cart
                Log.d("position_selected", String.valueOf(position));
                final Double val = Double.parseDouble(holder.etRestQuan.getText().toString());
                final int proId = products.get(position).getId();
                Long mob = Long.parseLong(mobileNumber);
  //              addCartItem(val, proId);
                Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.GET, "https://api.halanx.com/carts/detail/", null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            cartId = response.getInt("id");

                            final String token = c.getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");
                            Log.d("token",token);
                            CartItemPost item = new CartItemPost(cartId,val, proId, null);

                            Call<CartItemPost> call = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(djangoBaseUrl).build().create(DataInterface.class).putCartItemOnServer(item,token);
                            call.enqueue(new Callback<CartItemPost>() {
                                @Override
                                public void onResponse(Call<CartItemPost> call, Response<CartItemPost> response) {

                                    Log.d("done","donepb");

                                    holder.pb_addtocart.setVisibility(View.GONE);
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

                                            }
                                            else
                                            {
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

                String url = "https://api.halanx.com/users/favs/" + "1" + "/";
                JSONObject obj = new JSONObject();

                try {
                    obj.put("LastItem", products.get(position).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Volley.newRequestQueue(c).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if ("1".equals("1")) {
                            Picasso.with(c).load(R.drawable.fav_filled_48).into(holder.ivFav);
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
        });

        holder.cvProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ItemDisplayActivity.class);
                intent.putExtra("Name", products.get(position).getProductName());
                intent.putExtra("Price", products.get(position).getPrice());
                intent.putExtra("Features", products.get(position).getFeatures());
                intent.putExtra("Image", products.get(position).getProductImage());
                intent.putExtra("ID", products.get(position).getId());
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return products.size();
    }



    public class ProductViewHolder extends RecyclerView.ViewHolder {

        CardView cvProducts, cvRest;
        ImageView productImage;
        TextView productName, productPrice;
        List<ProductInfo> products;
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
        ProgressBar pb_addtocart;

        public ProductViewHolder(View itemView, List<ProductInfo> products, Context c, TextView itemCount) {
            super(itemView);
            this.products = products;
            this.c = c;
            this.itemCount = itemCount;

            cvProducts = (CardView) itemView.findViewById(R.id.cvProducts);
            cvRest = (CardView) itemView.findViewById(R.id.cvRest);
            productImage = (ImageView) itemView.findViewById(R.id.itemImage);
            productName = (TextView) itemView.findViewById(R.id.itemName);
            productPrice = (TextView) itemView.findViewById(R.id.itemPrice);
            description = (TextView) itemView.findViewById(R.id.description);
            tvRestName = (TextView) itemView.findViewById(R.id.restName);
            tvRestPrice = (TextView) itemView.findViewById(R.id.restPrice);
            cvAddCart = (CardView) itemView.findViewById(R.id.cv_rest_add_cart);
            etRestQuan = (EditText) itemView.findViewById(R.id.restQuantity);
            pb_addtocart = (ProgressBar) itemView.findViewById(R.id.pb_addtocart);

            rvDec = (RelativeLayout) itemView.findViewById(R.id.restDecrement);
            rvInc = (RelativeLayout) itemView.findViewById(R.id.restIncrement);
            tvCart = (TextView) itemView.findViewById(R.id.tv_add_to_cart);
            ivFav = (ImageView) itemView.findViewById(R.id.restFav);


        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

}