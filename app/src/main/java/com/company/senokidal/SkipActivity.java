package com.company.senokidal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdSize;

import com.company.senokidal.utils.Iklan;

public class SkipActivity extends AppCompatActivity {

    static SharedPreferences sharedpreferences;

    Context context;
    Iklan iklan;

    int w = 6000;
    TextView waktu;
    ImageView action_skip;
    private final Handler waitHandler = new Handler();
    private final Runnable waitCallback = new Runnable() {
        @Override
        public void run() {
            action_skip.setVisibility(View.VISIBLE);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        context = SkipActivity.this;

        iklan = new Iklan(context);
        iklan.preRewardedVideoAd();

        LinearLayout linearLayout = findViewById(R.id.addView);
        linearLayout.addView(iklan.getAd1View(AdSize.MEDIUM_RECTANGLE, R.string.banner_ad_unit_id));


        waitHandler.postDelayed(waitCallback, w);

        action_skip = findViewById(R.id.action_skip);
        action_skip.setVisibility(View.GONE);
        action_skip.setOnClickListener(view -> {
            int restore = sharedpreferences.getInt("restore", 0);
            Log.e("r", String.valueOf(restore));
            if(restore == 1){

                Intent intent = new Intent(context, RestoreActivity.class);
                startActivity(intent);
                finish();

            }else{

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();

            }

        });

        waktu = findViewById(R.id.waktu);
        waktu.setText(w+" detik");
        new CountDownTimer(w, 1000) {

            public void onTick(long millisUntilFinished) {
                waktu.setText(millisUntilFinished / 1000 + " detik");
            }

            public void onFinish() {
                waktu.setVisibility(View.GONE);
                action_skip.setVisibility(View.VISIBLE);
            }

        }.start();

    }
}
