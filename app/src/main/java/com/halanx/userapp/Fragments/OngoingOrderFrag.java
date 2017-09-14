package com.halanx.userapp.Fragments;


import android.content.Context;
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

import com.halanx.userapp.Adapters.OrdersAdapter;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.OrderInfo;
import com.halanx.userapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.halanx.userapp.GlobalAccess.djangoBaseUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingOrderFrag extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    LinearLayout llNoOrders;
    OrdersAdapter adapter;
    Boolean flag;


    Retrofit.Builder builder;
    Retrofit retrofit;
    DataInterface client;

    List<OrderInfo> allOrdersList, onGoingOrderList;



    public OngoingOrderFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ongoing_order, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_ongoingOrders);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_ongoingOrders);
        llNoOrders = (LinearLayout) v.findViewById(R.id.ll_no_completed);

        builder = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        client = retrofit.create(DataInterface.class);

        progressBar.setVisibility(View.VISIBLE);
        String token = getActivity().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null);
        Log.d("token",token);
        Call<List<OrderInfo>> orderCall = client.getUserOrders(token);
        orderCall.enqueue(new Callback<List<OrderInfo>>() {
            @Override
            public void onResponse(Call<List<OrderInfo>> call, Response<List<OrderInfo>> response) {
                allOrdersList = response.body();
                progressBar.setVisibility(View.GONE);
                Log.d("responsebody", String.valueOf(response.body()));

                Log.i("OrderBro","retrofit "+response.body().size());

                allOrdersList = response.body();
                progressBar.setVisibility(View.GONE);

                if (!allOrdersList.isEmpty()) {
                    onGoingOrderList = new ArrayList<>();

                    for (int i = allOrdersList.size()-1; i >=0 ; i--) {
                        for(int j= 0;j<allOrdersList.get(i).getOrderItems().size();j++){
                        if (!allOrdersList.get(i).getOrderItems().get(j).getIsdeliver()) {
                            //Completed order = isDelivered is true
                            flag=true;
                        }
                        else{
                            flag =false;
                             }
                        }
                        if (flag){
                            onGoingOrderList.add(allOrdersList.get(i));
                        }
                    }

                    if (onGoingOrderList.size() > 0) {
                        // Log.i("TAG",completedOrderList.get(0).getDeliveryDate());
                        adapter = new OrdersAdapter(onGoingOrderList, getActivity(), false);
                        layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);

                    } else {
                        Log.i("OrderBro","Else 1 ");
                        llNoOrders.setVisibility(View.VISIBLE);
                    }


                }

                else {
                    Log.i("OrderBro","Else 2 ");
                    llNoOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<OrderInfo>> call, Throwable t) {
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

//        Volley.newRequestQueue(getActivity()).add(new StringRequest(Request.Method.GET, "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/orders/user/9582184794",
//                new com.android.volley.Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.i("OrderBro",response);
//
//
//                    }
//                }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }));


        return v;
    }

}