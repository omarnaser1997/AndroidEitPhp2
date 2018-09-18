package com.naser.omar.androideitserverphp.Test;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amigold.fundapter.fields.StringField;
import com.naser.omar.androideitserverphp.Adapter.ViewpagerAdapter;
import com.naser.omar.androideitserverphp.MainActivity;
import com.naser.omar.androideitserverphp.R;

public class ActivitySliding extends AppCompatActivity {

    ViewPager viewPager;
    ViewpagerAdapter adapter;
    private String[] images={
            "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/Category/1535748708.jpeg",
            "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/Category/1535748774.jpeg",
            "https://omarnaser.000webhostapp.com/AndroidEitServerPHP/Category/1535748887.jpeg"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapter=new ViewpagerAdapter(ActivitySliding.this,images);
        viewPager.setAdapter(adapter);
    }
}
