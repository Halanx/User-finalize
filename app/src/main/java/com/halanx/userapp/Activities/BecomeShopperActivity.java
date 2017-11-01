package com.halanx.userapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.halanx.userapp.R;

public class BecomeShopperActivity extends AppCompatActivity {

    TextView download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_shopper);

        download = (TextView) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.shopperapphalanx"));
                startActivity(intent);
            }
        });

    }

}
