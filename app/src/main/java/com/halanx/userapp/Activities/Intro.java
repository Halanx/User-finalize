package com.halanx.userapp.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.halanx.userapp.R;
import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

public class Intro extends AppCompatActivity {
    ViewPager pager;
    Button but,but1,next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        but= (Button) findViewById(R.id.skip);
        but1= (Button) findViewById(R.id.CONTINUE);
        next= (Button) findViewById(R.id.next);

        pager = (ViewPager) findViewById(R.id.mypager);

        pager.setPageTransformer(false, new ParallaxPagerTransformer(R.id.mypager));
     //   MyPagerAdapter madapter = new MyPagerAdapter(getSupportFragmentManager());
       // pager.setAdapter(madapter);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem()+1);
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if(position==2){
                    but1.setVisibility(View.VISIBLE);
                    but.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                }
                else {
                    but1.setVisibility(View.GONE);
                    but.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(intro.this,mainscreen.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                finish();
            }
        });

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(intro.this,mainscreen.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                finish();
            }
        });

    }

//    public class MyPagerAdapter extends FragmentPagerAdapter {
//        public MyPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0: {
//                  //  Fragment fm2 = new fragment2();
//                  //  return fm2;
//                }
//                case 1:{
//                   // Fragment fm1 = new fragment3();
//                   // return fm1;
//                }
//
//                default: {
//               //     Fragment fm3 = new fragment1();
//                 //   return fm3;
//
//                }
//            }
//            return 0;
//        }
//        @Override
//        public int getCount() {
//            return 3;
//        }
    }
