package com.naser.omar.androideitserverphp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NoWifiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_wifi);
        Toast.makeText(this, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
    }
}
