package com.halanx.userapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
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

import retrofit2.Retrofit;


public class RatingActivity extends AppCompatActivity {

    RatingBar rb;
    TextView tvRating;
    Boolean isRated;
    float final_rating;
    Retrofit.Builder builder;
    Retrofit retrofit;
    String mobileNumber;
    DataInterface client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        rb = (RatingBar) findViewById(R.id.rb_rating);
        tvRating = (TextView) findViewById(R.id.tv_num_stars);


        final String id = getSharedPreferences("BatchData", Context.MODE_PRIVATE).getString("ShopperID", null);
        Log.d("stringid",id);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

//                Toast.makeText(RatingActivity.this, "Rating is "+ v, Toast.LENGTH_SHORT).show();
//                tvRating.setText(""+v+" "+b);

                if (v == 1.0) {
                    tvRating.setText("1 star");
                    isRated = true;

                } else if (v == 2.0) {
                    isRated = true;
                    tvRating.setText("2 stars");
                } else if (v == 3.0) {
                    isRated = true;
                    tvRating.setText("3 stars");
                } else if (v == 4.0) {
                    isRated = true;
                    tvRating.setText("4 stars");
                } else if (v == 5.0) {
                    isRated = true;
                    tvRating.setText("5 stars");
                }
                    final_rating = v;


            }
        });


        (findViewById(R.id.btn_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRated){



                    String url = "https://api.halanx.com/users/rateshopper/";

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("Rating", final_rating);
                        obj.put("sid", id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Volley.newRequestQueue(getApplicationContext()).add(new JsonObjectRequest(Request.Method.POST, url, obj, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Rating_data", "Done");
                            startActivity(new Intent(RatingActivity.this,HomeActivity.class));
                            Toast.makeText(RatingActivity.this, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("errror", String.valueOf(error));

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
                else {
                    Toast.makeText(RatingActivity.this, "Please rate your shopper", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
