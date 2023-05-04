package com.company.senokidal.utils;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.interstitial.InterstitialAd;

public class Iklan {

    Handler handler;
    Context context;
    private AdView adView;
    private InterstitialAd mInterstitialAd;

    public Iklan(Context context) {
        this.context = context;


        /**
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);

        RequestConfiguration requestConfiguration
                = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);*/

    }


    public AdView getAd1View(AdSize adSize, int ad_unit_id) {


        float density = context.getResources().getDisplayMetrics().density;
        int height = Math.round(adSize.getHeight() * density);

        if (adView != null) {
            return adView;
        }
        adView = new AdView(context);
        adView.setAdUnitId(context.getString(ad_unit_id));
        adView.setAdSize(adSize);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        adView.setLayoutParams(layoutParams);
        adView.loadAd(new AdRequest
                .Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        );



        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                adView.loadAd(new AdRequest
                        .Builder()
                        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build()
                );

                // delay setiap 1 detik
                handler.postDelayed(this, 30000);
            }

            // Delay awal
        }, 1000);

        return adView;
    }

    public void preRewardedVideoAd() {
        /**
        //Menginisialisasi Rewarded Video Ads
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.inter_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest
                        .Builder()
                        //.addTestDevice("6355759993F32873748BBF0ABBD57C18")
                        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build()
                );
            }

        });*/
    }


}
