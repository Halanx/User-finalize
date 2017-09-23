package com.halanx.userapp.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Adapters.OrdersAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.OrderInfo;
import com.halanx.userapp.R;

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
 * A simple {@link Fragment} subclass.
 */
public class CompletedOrderFrag extends Fragment {

    RecyclerView recyclerView;
    OrdersAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    LinearLayout llNoOrders;

    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;

    List<OrderInfo> allOrdersList, completedOrderList;


    public CompletedOrderFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_completed_order, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_completedOrders);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_completedOrders);
        llNoOrders = (LinearLayout) v.findViewById(R.id.ll_no_completed);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String mobileNumber = sharedPreferences.getString("MobileNumber", null);

        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);
        final String token = getActivity().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token","token1");
        Log.d("token",token);

        progressBar.setVisibility(View.VISIBLE);
        Call<List<OrderInfo>> orderCall = client.getUserOrders(token);
        orderCall.enqueue(new Callback<List<OrderInfo>>() {
            @Override
            public void onResponse(Call<List<OrderInfo>> call, Response<List<OrderInfo>> response) {
                allOrdersList = response.body();
                progressBar.setVisibility(View.GONE);
                Log.d("response", String.valueOf(response));

                if (!allOrdersList.isEmpty()) {
                    completedOrderList = new ArrayList<>();

                    for (int i = allOrdersList.size()-1; i >= 0  ; i--) {
                        Boolean flag = true;
                        for (int j = 0;j<allOrdersList.get(i).getOrderItems().size();j++) {
                            if (allOrdersList.get(i).getOrderItems().get(j).getIsdeliver()) {
                                //Completed order = isDelivered is true
                            }
                            else{
                                flag =false;
                            }
                        }

                        if(flag) {
                            completedOrderList.add(allOrdersList.get(i));
                        }
                    }

                    if (completedOrderList.size() > 0) {
                        adapter = new OrdersAdapter(completedOrderList, getActivity(),true);
                        layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);

                    } else {
                        llNoOrders.setVisibility(View.VISIBLE);
                    }


                }

                else {
                    llNoOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<OrderInfo>> call, Throwable t) {
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(getActivity()).add(new StringRequest(Request.Method.GET, "https://api.halanx.com/orders/user/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("OrderBro", response);


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
                params.put("Authorization", token);
                return params;
            }

        });


        return v;
    }


}

