package com.mecodroid.firedb_author;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                Intent page = new Intent();
                page.setClass(Splash.this, MainActivity.class);
                startActivity(page);
                finish();
            }
        }).start();
    }
}
