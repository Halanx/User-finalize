package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.POJO.SubscriptionInfoGet;
import com.halanx.userapp.POJO.UserInfo;
import com.halanx.userapp.R;

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

public class SubscriptionDrawerActivity extends AppCompatActivity {

//    RecyclerView sub_recycler;


//    ArrayList<String> id_list;

    RecyclerView rvSubs;
    String url, mobile;
    Switch onOffSwitch;
    List<SubscriptionInfoGet> allItems;
    List<SubscriptionInfoGet> activeItems;
    ProgressBar pbSubs;
    TextView tvBalanceAmount;
    Boolean subsStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription_drawer);

        rvSubs = (RecyclerView) findViewById(R.id.subscription_recycler);
        onOffSwitch = (Switch) findViewById(R.id.subscription_onoff);
        mobile = getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null);
        pbSubs = (ProgressBar) findViewById(R.id.pb_subs);
        pbSubs.setVisibility(View.VISIBLE);
        tvBalanceAmount = (TextView) findViewById(R.id.balance_amount);

        Call<UserInfo> userCall = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.
                create()).build().create(DataInterface.class).getUserInfo(getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));
        userCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                UserInfo info = response.body();
                tvBalanceAmount.setText(info.getAccountBalance().toString());
                onOffSwitch.setChecked(info.getSubscriptionStatus());
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.i("Err",t.toString());
            }
        });

        String token = getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null);
        Log.d("subscription_token",token);

        Call<List<SubscriptionInfoGet>> call = new Retrofit.Builder().baseUrl(djangoBaseUrl).addConverterFactory(GsonConverterFactory.
                create()).build().create(DataInterface.class).getUserSubscribedItems(token);
        call.enqueue(new Callback<List<SubscriptionInfoGet>>() {
            @Override
            public void onResponse(Call<List<SubscriptionInfoGet>> call, Response<List<SubscriptionInfoGet>> response) {

                allItems = response.body();
                activeItems = new ArrayList<SubscriptionInfoGet>();
                pbSubs.setVisibility(View.INVISIBLE);
                if (allItems != null && !allItems.isEmpty()) {

                    for (int i = 0; i < allItems.size(); i++) {
                        if (!allItems.get(i).getPermanentRemoved()) {
                            activeItems.add(allItems.get(i));
                        }
                    }


                    if (activeItems.size() > 0) {
                        rvSubs.setAdapter(new SubsAdapter(activeItems));
                        rvSubs.setLayoutManager(new LinearLayoutManager(SubscriptionDrawerActivity.this));
                        rvSubs.setHasFixedSize(true);
                    } else {
                        new AlertDialog.Builder(SubscriptionDrawerActivity.this).setTitle("No subscriptions").
                                setMessage("You have not subscribed to any items").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }).create().show();
                    }


                } else {
                    new AlertDialog.Builder(SubscriptionDrawerActivity.this).setTitle("No subscriptions").
                            setMessage("You have not subscribed to any items").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    }).setCancelable(false).create().show();
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionInfoGet>> call, Throwable t) {
                Log.i("Err", t.toString());
                pbSubs.setVisibility(View.INVISIBLE);
            }
        });


//        balance_amount = (TextView) findViewById(R.id.balance_amount);
//        id_list = new ArrayList<>();
//
//
//        String url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/subscriptions/" +
//                getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null)+"/"+ "items";
//        JSONArray obj = new JSONArray();
//
//
//        Volley.newRequestQueue(getApplicationContext()).add(new JsonArrayRequest(Request.Method.GET, url,obj, new com.android.volley.Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.i("Address", String.valueOf(response));
//                Log.d("id_list", String.valueOf(response.length()));
//
//                for (int i=0;i<response.length();i++){
//                    try {
//                        id_list.add(response.getJSONObject(i).getString("id"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.d("id_list", String.valueOf(id_list));
//
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//
//            }
//        }));
//
//
//

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String url = "https://api.halanx.com/users/detail/";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("SubscriptionStatus",b);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Volley.newRequestQueue(SubscriptionDrawerActivity.this).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

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


                if (activeItems != null && !activeItems.isEmpty()) {

                    //switch is in ON state
                    if (b) {
                        for (int i = 0; i < activeItems.size(); i++) {

                            //Remove temp is false
                            changeRemoveTemporarily(activeItems.get(i).getId(), false);
                        }
                    } else {
                        //Switch if off
                        for (int i = 0; i < activeItems.size(); i++) {

                            //Remove temp is true
                            changeRemoveTemporarily(activeItems.get(i).getId(), true);
                        }

                    }


                }


            }
        });


    }

    void changeRemoveTemporarily(Integer id, Boolean b) {

        url = "http://api.halanx.com/subscriptions/" + id;
        JSONObject obj = new JSONObject();
        try {
            obj.put("TemporaryRemoved", b);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Volley.newRequestQueue(SubscriptionDrawerActivity.this).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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

    class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.SubsHolder> {

        List<SubscriptionInfoGet> subscriptionInfoList;

        public SubsAdapter(List<SubscriptionInfoGet> subscriptionInfoList) {
            this.subscriptionInfoList = subscriptionInfoList;
        }

        @Override
        public SubsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SubsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subscribed_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SubsHolder holder, int position) {
            holder.tvStartDate.setText(subscriptionInfoList.get(position).getStartDate());
            holder.tvEvery.setText(getEveryString(subscriptionInfoList.get(position)));
            holder.tvDelLeft.setText(subscriptionInfoList.get(position).getDeliveriesLeft().toString());
            holder.tvQuan.setText(subscriptionInfoList.get(position).getQuantityPerDay().toString());
            holder.tvPrice.setText(subscriptionInfoList.get(position).getCostPerDay().toString());
            holder.tvProName.setText(subscriptionInfoList.get(position).getItem().getProductName());
        }

        private String getEveryString(SubscriptionInfoGet subscriptionInfo) {
            String ev = "";
            if (subscriptionInfo.getOnMonday()) {
                ev += "M";
            }

            if (subscriptionInfo.getOnTuesday()) {
                ev += "T";
            }

            if (subscriptionInfo.getOnWednesday()) {
                ev += "W";
            }

            if (subscriptionInfo.getOnThursday()) {
                ev += "Th";
            }

            if (subscriptionInfo.getOnFriday()) {
                ev += "F";
            }

            if (subscriptionInfo.getOnSaturday()) {
                ev += "S";
            }

            if (subscriptionInfo.getOnSunday()) {
                ev += "Su";
            }

            Log.i("Err", ev);
            return ev;
        }

        @Override
        public int getItemCount() {
            return subscriptionInfoList.size();
        }

        public class SubsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tvStartDate, tvEvery, tvDelLeft, tvProName, tvQuan, tvPrice, tvUnsubs;

            public SubsHolder(View itemView) {
                super(itemView);
                tvStartDate = (TextView) itemView.findViewById(R.id.tv_start_date);
                tvEvery = (TextView) itemView.findViewById(R.id.tv_every);
                tvDelLeft = (TextView) itemView.findViewById(R.id.tv_del_left);
                tvProName = (TextView) itemView.findViewById(R.id.tv_subs_pro_name);
                tvQuan = (TextView) itemView.findViewById(R.id.tv_subs_quantity);
                tvPrice = (TextView) itemView.findViewById(R.id.tv_subs_price);
                tvUnsubs = (TextView) itemView.findViewById(R.id.tv_unsubs);
                tvUnsubs.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                final int pos = getAdapterPosition();


                if (view == tvUnsubs) {

                    int id = subscriptionInfoList.get(pos).getId();
                    url = "http://api.halanx.com/subscriptions/" + id;
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("PermanentRemoved", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Volley.newRequestQueue(SubscriptionDrawerActivity.this).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Toast.makeText(SubscriptionDrawerActivity.this, "Item Unsubscribed", Toast.LENGTH_SHORT).show();
                            subscriptionInfoList.remove(pos);
                            notifyDataSetChanged();
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(SubscriptionDrawerActivity.this, "Network error", Toast.LENGTH_SHORT).show();
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
            }
        }
    }

}
