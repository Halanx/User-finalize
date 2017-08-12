package com.halanx.userapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.halanx.userapp.R;

public class Tracking extends AppCompatActivity {

    ImageView tick1,tick2,tick3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        tick1 = (ImageView) findViewById(R.id.tick1);
        tick2 = (ImageView) findViewById(R.id.tick2);
        tick3 = (ImageView) findViewById(R.id.tick3);

        tick2.setVisibility(View.GONE);
        tick3.setVisibility(View.GONE);

    }
}
