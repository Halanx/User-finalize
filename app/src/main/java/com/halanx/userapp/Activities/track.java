package com.halanx.userapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.halanx.userapp.R;

public class track extends AppCompatActivity {

    ImageView tick1,tick2,tick3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Boolean order_accept = getIntent().getBooleanExtra("batch_done",false);
        Boolean delivered = getIntent().getBooleanExtra("delivered",false);

        Log.d("batch", String.valueOf(order_accept));
        Log.d("batch", String.valueOf(delivered));
        tick1 = (ImageView) findViewById(R.id.tick1);
        tick2 = (ImageView) findViewById(R.id.tick2);
        tick3 = (ImageView) findViewById(R.id.tick3);

        if(order_accept){
         tick2.setVisibility(View.VISIBLE);
            Log.d("batch", String.valueOf(delivered));

            if(delivered){
                tick3.setVisibility(View.VISIBLE);
            }
        }
        else {
            tick2.setVisibility(View.GONE);
            tick3.setVisibility(View.GONE);
        }
    }
}
