package com.chartianz.ipocheckkar.ads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.chartianz.ipocheckkar.AppConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Map;

public class InterstitialAds {
    Context context;
    private String AD_UNIT_ID = Ads_Config.ADMOB_INTERSTITIAL_AD;
    AdManagerInterstitialAd mAdManagerInterstitialAd;
    InterstitialAd mInterstitialAd;
    AdManagerAdRequest adManagerAdRequest;

    public InterstitialAds(Context context) {
        this.context = context;
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                MobileAds.initialize(context, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                        }
                    }
                });
            }
        });

    }

    public void loadAd() {
        if (!AppConfig.isUserPaid) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, AD_UNIT_ID, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                    adManagerAdRequest = new AdManagerAdRequest.Builder().build();
                    AdManagerInterstitialAd.load(context, Ads_Config.ADMANAGER_INTERSTITIAL_AD, adManagerAdRequest, new AdManagerInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                            mAdManagerInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError AdManagerloadAdError) {
                            mAdManagerInterstitialAd = null;
                        }
                    });
                }
            });

        }
    }

    public boolean isAdLoaded(){
        if (mInterstitialAd != null) {
            return true;
        }else if (mAdManagerInterstitialAd != null){
            return true;
        }
        return false;
    }

    public void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) context);
        }

        //
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd.show((Activity) context);
        }
    }

}
