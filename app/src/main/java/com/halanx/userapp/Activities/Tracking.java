package com.halanx.userapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

public class Tracking extends AppCompatActivity {

  RecyclerView trackRecycler;
    Trackadapter adapter;
    String orderId;
    JSONArray orderResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        trackRecycler = (RecyclerView) findViewById(R.id.trackRecycler);

        orderId  = getIntent().getStringExtra("orderId");

        String url = djangoBaseUrl +"orders/"+orderId+"/track";
        Log.d("URL",url);
        JSONArray json = new JSONArray();

        Volley.newRequestQueue(getApplicationContext()).add(new JsonArrayRequest(Request.Method.GET, url, json, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.d("Response", String.valueOf(response));

                try {
                    if (response.length()>0) {
                        Log.d("Response", response.getString(0));
                        adapter = new Trackadapter(getApplicationContext(), response);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        trackRecycler.setLayoutManager(layoutManager);
                        trackRecycler.setAdapter(adapter);
                        trackRecycler.setHasFixedSize(true);
                    }
                    else{

                        startActivity(new Intent(Tracking.this,track.class).putExtra("batch_done",false).putExtra("delivered",false));
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Response", String.valueOf(error));

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

    private class Trackadapter extends RecyclerView.Adapter<Trackadapter.TrackViewHolder> {
        Context c;
        JSONArray orderResponse;

        public Trackadapter(Context applicationContext, JSONArray orderId) {
            c = applicationContext;
            this.orderResponse = orderId;
            Log.d("response", String.valueOf(orderResponse));
            Log.d("orderlength", String.valueOf(orderResponse.length()));
        }

        @Override
        public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_tracking_order,parent,false);
            TrackViewHolder holder = new TrackViewHolder(view,c);

            return holder;
        }

        @Override
        public void onBindViewHolder(Trackadapter.TrackViewHolder holder, final int position) {
            final List<String> items = new ArrayList<>();
            try {
                JSONArray itemArray = orderResponse.getJSONObject(position).getJSONArray("items");

                for(int i = 0;i<itemArray.length();i++){
                    items.add(itemArray.getJSONObject(i).getString("ProductName"));
                }
                TrackRecycler rvAdapter = new TrackRecycler(items);
                holder.rvProducts.setAdapter(rvAdapter);
                RecyclerView.LayoutManager manager = new LinearLayoutManager(c);
                holder.rvProducts.setLayoutManager(manager);
                holder.shopper_name.setText(orderResponse.getJSONObject(position).getJSONObject("ShopperId").getJSONObject("user").getString("first_name")+" "+orderResponse.getJSONObject(position).getJSONObject("ShopperId").getJSONObject("user").getString("last_name"));
                holder.ivCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mobile = null;
                        try {
                            mobile = String.valueOf(Long.parseLong(orderResponse.getJSONObject(position).getJSONObject("ShopperId").getString("PhoneNo")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobile, null));
                        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) !=
                                PackageManager.PERMISSION_GRANTED) {

                            c.startActivity(intent);
                        }

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.ivOrderStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if(orderResponse.getJSONObject(0).getBoolean("IsDelivered")) {
                            startActivity(new Intent(Tracking.this, track.class).putExtra("batch_done", true).putExtra("delivered",true));
                        }
                        else{
                            startActivity(new Intent(Tracking.this, track.class).putExtra("batch_done", true).putExtra("delivered",false));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();

                }
            });

        }

        @Override
        public int getItemCount() {
            return orderResponse.length();
        }

        class TrackViewHolder extends RecyclerView.ViewHolder {

            Context c ;

            TextView orderNo, shopper_name, orderDeliverDate, orderDeliveredBy;
            LinearLayout llIOnGoing, llCompleted;
            ImageView ivOrderStatus,ivCall;
            RecyclerView rvProducts;

            public TrackViewHolder(View itemView, Context c) {
                super(itemView);
                this.c = c;
                orderNo = (TextView) itemView.findViewById(R.id.textView_order_number);
                shopper_name = (TextView) itemView.findViewById(R.id.textView_shopper_name);
                orderDeliverDate = (TextView) itemView.findViewById(R.id.textView_order_date);
                orderDeliveredBy = (TextView) itemView.findViewById(R.id.textView_order_delivered_by);
                llIOnGoing = (LinearLayout) itemView.findViewById(R.id.ll_onGoingOrder);
                llCompleted = (LinearLayout) itemView.findViewById(R.id.ll_completed);
                rvProducts = (RecyclerView) itemView.findViewById(R.id.rv_products);
                ivOrderStatus = (ImageView) itemView.findViewById(R.id.iv_orderStatus);
                ivCall = (ImageView) itemView.findViewById(R.id.call);
            }


        }
    }

    private class TrackRecycler extends RecyclerView.Adapter<TrackRecycler.TrackHolder> {
        List<String> item;
        public TrackRecycler(List<String> items) {
            this.item = items;
        }

        @Override
        public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TrackHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.recycler_order_products,parent,false));

        }

        @Override
        public void onBindViewHolder(TrackHolder holder, int position) {

            holder.tvProduct.setText(item.get(position));
        }


        @Override
        public int getItemCount() {
            return item.size();
        }

        class TrackHolder extends RecyclerView.ViewHolder {
            TextView tvProduct;
            public TrackHolder(View inflate) {
                super(inflate);
                tvProduct = (TextView) itemView.findViewById(R.id.tv_product_orders);



            }
        }
    }
}
