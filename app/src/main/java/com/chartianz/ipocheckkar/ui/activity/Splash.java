package com.chartianz.ipocheckkar.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chartianz.ipocheckkar.Config;
import com.chartianz.ipocheckkar.Constants;
import com.chartianz.ipocheckkar.databinding.ActivitySplashBinding;
import com.chartianz.ipocheckkar.ui.bottomsheets.AdBlockerBottomsheet;
import com.chartianz.ipocheckkar.utils.TinyDB;

import java.net.HttpURLConnection;
import java.net.URL;

public class Splash extends AppCompatActivity {
    Context context;
    public static ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        //checkAdBlocker();
    }

    private void init(){
        context = getApplicationContext();
        TinyDB tinyDB = new TinyDB(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    if(tinyDB.getBoolean("isFirstTime")){
                        startActivity(new Intent(Splash.this, ControllerActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(Splash.this, Onboarding.class));
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkAdBlocker() {
        if (!Config.isUserPaid) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(Constants.AD_BLOCKER_LINK);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.connect();

                        startActivity(new Intent(Splash.this, ControllerActivity.class));
                        finish();

                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AdBlockerBottomsheet().Show(Splash.this);
                            }
                        });
                    }
                }
            }).start();
        }else{
            startActivity(new Intent(Splash.this, ControllerActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}