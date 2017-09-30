package com.halanx.userapp.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.halanx.userapp.Interfaces.DataInterface;
import com.halanx.userapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Wallet extends AppCompatActivity {

    TextView wallet_balance;
    TextView bonus_balance;
    Button add_money;

    String current_balance;
    DataInterface client;
    Button promotionCodeAdd;
    EditText promotionCode;

    TextView haveReferal;

    RelativeLayout enterCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);


        haveReferal = (TextView) findViewById(R.id.haveReferal);
        wallet_balance = (TextView) findViewById(R.id.wallet_balance);
        bonus_balance = (TextView) findViewById(R.id.bonus_balance);
        add_money = (Button) findViewById(R.id.add_money);
        promotionCodeAdd = (Button) findViewById(R.id.promotionCodeAdd);
        promotionCode = (EditText) findViewById(R.id.promotionCode);
        enterCode = (RelativeLayout) findViewById(R.id.enterCode);

        haveReferal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                haveReferal.setVisibility(View.GONE);
                enterCode.setVisibility(View.VISIBLE);
            }
        });



        final String uri = "https://api.halanx.com/users/detail/";
        Log.d("patchingdone",uri);

        Log.d("token",getApplicationContext().getSharedPreferences("Tokenkey",Context.MODE_PRIVATE).getString("token",null));



        promotionCodeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json = new JSONObject();
                try {
                    json.put("ReferralCode",String.valueOf(promotionCode.getText()));
                    Log.d("patchingdone",String.valueOf(promotionCode.getText()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.PATCH, uri, json, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                            Log.d("patchingdone","done");



                    }
                },new com.android.volley.Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ammount added", String.valueOf(error));
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token",null));
                        return params;
                    }

                });

            }
        });




        JSONObject jsonObject = new JSONObject();
        Log.d("ammount added",uri);

        Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.GET, uri, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    wallet_balance.setText(String.valueOf(response.get("AccountBalance")));
                    bonus_balance.setText(String.valueOf(response.get("PromotionalBalance")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },new com.android.volley.Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ammount added", String.valueOf(error));
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", getApplicationContext().getSharedPreferences("Tokenkey", Context.MODE_PRIVATE).getString("token",null));
                return params;
            }

        });


        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Dialog dialAddMoney = new Dialog(Wallet.this);
                dialAddMoney.setContentView(R.layout.add_ammount_dialog_box);

                Button pay = (Button) dialAddMoney.findViewById(R.id.btProceed_dialogue);
                Button exit = (Button) dialAddMoney.findViewById(R.id.btCancel_dialogue);
                final EditText amount = (EditText) dialAddMoney.findViewById(R.id.et1_dialogue);

                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (amount.getText().toString().isEmpty()) {
                            Toast.makeText(Wallet.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            return;
                        }

                       else
                        {
                            String ammount = String.valueOf(Double.parseDouble(String.valueOf(amount.getText())));
                            Log.d("ammount",ammount);Log.d("ammount", String.valueOf(Double.parseDouble(String.valueOf(wallet_balance.getText()))));Log.d("ammount", String.valueOf(Double.parseDouble(String.valueOf(amount.getText()))));
                            startActivity(new Intent(Wallet.this,PaymentActivity.class).putExtra("total_cost",ammount).putExtra("current_ammount",wallet_balance.getText()).putExtra("isOrder",false));
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            dialAddMoney.dismiss();
                            finish();
                        }



                    }
                });

                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialAddMoney.dismiss();
                    }
                });


                dialAddMoney.show();

            }
        });

    }
}
