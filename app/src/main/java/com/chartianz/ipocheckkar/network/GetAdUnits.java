package com.chartianz.ipocheckkar.network;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.chartianz.ipocheckkar.AppConfig;
import com.chartianz.ipocheckkar.BuildConfig;
import com.chartianz.ipocheckkar.ads.Ads_Config;
import com.chartianz.ipocheckkar.ads.OpenAds.AppOpenManager;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetAdUnits extends AsyncTask<String, String, String> {

    Context context;
    Application application;
    String content;

    public GetAdUnits(Context context, Application application) {
        this.context = context;
        this.application = application;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Document document = Jsoup.connect(BuildConfig.AD_UNIT).get();
            Elements element = document.getElementsByClass("ad_manager");

            content = Jsoup.parse(String.valueOf(element)).text();
            if (content.length() != 0) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    if(jsonObject.getString("interstitial_ad") != null || jsonObject.getString("interstitial_ad").length() <= 0)
                        Ads_Config.ADMANAGER_INTERSTITIAL_AD = jsonObject.getString("interstitial_ad");
                    if(jsonObject.getString("open_ad") != null || jsonObject.getString("open_ad").length() <= 0)
                        Ads_Config.ADMANAGER_OPEN_AD = jsonObject.getString("open_ad");
                    if(jsonObject.getString("isProAppBannerVisible") != null || jsonObject.getString("isProAppBannerVisible").length() <= 0)
                        Ads_Config.isProAppBannerVisible = jsonObject.getBoolean("isProAppBannerVisible");
                } catch (JSONException e) {

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        MobileAds.initialize(context);
        if (AppConfig.isUserPaid) {
            new AppOpenManager(application, Ads_Config.ADMOB_OPEN_AD_PRO);
        } else {
            new AppOpenManager(application, Ads_Config.ADMANAGER_OPEN_AD);
        }
    }
}
