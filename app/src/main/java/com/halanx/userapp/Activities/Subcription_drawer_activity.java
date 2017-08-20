package com.halanx.userapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.halanx.userapp.R;

public class Subcription_drawer_activity extends AppCompatActivity {

    RecyclerView sub_recycler;
    Switch onoff;
    TextView balance_amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription_drawer_activity);

        sub_recycler = (RecyclerView) findViewById(R.id.subscription_recycler);
        onoff = (Switch) findViewById(R.id.subscription_onoff);
        balance_amount = (TextView) findViewById(R.id.balance_amount);


        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    //switch is in On state
                }
                else{
                    //Switch is in off state
                }
            }
        });


    }
}
