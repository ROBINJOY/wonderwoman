package com.roninaks.tech.shesos;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//      Adding transition to next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(MainActivity.this, SetUID.class);
                MainActivity.this.startActivity(mainIntent);
                //Intent intent=new Intent(MainActivity.this, Home.class);
                // startActivity(intent);
                MainActivity.this.finish();
            }
        }, 2000);
    }
}
