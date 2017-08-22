package com.halanx.userapp.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Subcription_drawer_activity extends AppCompatActivity {

    RecyclerView sub_recycler;
    Switch onoff;
    TextView balance_amount;
    ArrayList<String> id_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription_drawer_activity);


        sub_recycler = (RecyclerView) findViewById(R.id.subscription_recycler);
        onoff = (Switch) findViewById(R.id.subscription_onoff);
        balance_amount = (TextView) findViewById(R.id.balance_amount);
        id_list = new ArrayList<>();


        String url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/subscriptions/" +
                getSharedPreferences("Login", Context.MODE_PRIVATE).getString("MobileNumber", null)+"/"+ "items";
        JSONArray obj = new JSONArray();


        Volley.newRequestQueue(getApplicationContext()).add(new JsonArrayRequest(Request.Method.GET, url,obj, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("Address", String.valueOf(response));
                Log.d("id_list", String.valueOf(response.length()));

                for (int i=0;i<response.length();i++){
                    try {
                        id_list.add(response.getJSONObject(i).getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("id_list", String.valueOf(id_list));


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }));



        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    //switch is in On state


                }
                else{
                    Log.d("id_list", String.valueOf(id_list));
                    try{
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("TemporaryRemoved",true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for(int i=0;i<id_list.size();i++) {
                            String url = "http://ec2-34-208-181-152.us-west-2.compute.amazonaws.com/subscriptions/" + id_list.get(i);


                            Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.PATCH, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.i("Status", "Done");


                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }));
                        }

                    }
                    catch(Exception e){

                    }



                    //Switch is in off state
                }
            }
        });


    }
}
