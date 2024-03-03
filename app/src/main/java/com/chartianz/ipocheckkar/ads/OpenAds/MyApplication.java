package com.chartianz.ipocheckkar.ads.OpenAds;

import android.app.Application;
import android.content.Context;

import com.chartianz.ipocheckkar.network.GetAdUnits;
import com.chartianz.ipocheckkar.utils.CheckInternetConnection;

public class MyApplication extends Application {
    Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (CheckInternetConnection.isNetworkConnected(context)) {
                    new GetAdUnits(context, MyApplication.this).execute();
                }
            }
        }).start();

    }
}
