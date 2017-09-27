package com.halanx.userapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.halanx.userapp.R;

public class HelpActivity extends AppCompatActivity {

    ImageView call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

//        call = (ImageView) findViewById(R.id.call);
//        call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+911127890252", null));
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) !=
//                        PackageManager.PERMISSION_GRANTED) {
//
//                    getApplicationContext().startActivity(intent);
//                }
//            }
//        });

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar3);
//        setSupportActionBar(myToolbar);
//        ActionBar ab = getSupportActionBar();
//        // Enable the Up button
//        ab.setDisplayHomeAsUpEnabled(true);
    }
}
