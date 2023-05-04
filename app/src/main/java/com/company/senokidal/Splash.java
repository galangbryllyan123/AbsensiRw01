package com.company.senokidal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    static SharedPreferences sharedpreferences;

    private final Handler waitHandler = new Handler();
    private final Runnable waitCallback = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_start);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;//Version Name
        int verCode = pInfo.versionCode;//Version Code
        String versionName = version;

        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText(versionName);

        if (android.os.Build.VERSION.SDK_INT > 9){
                StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
        }

                //Fake wait 2s to simulate some initialization on cold start (never do this in production!)
        waitHandler.postDelayed(waitCallback, 100);
    }

    @Override
    protected void onDestroy() {
        waitHandler.removeCallbacks(waitCallback);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}