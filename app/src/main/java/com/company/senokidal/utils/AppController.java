package com.company.senokidal.utils;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

public class AppController extends MultiDexApplication {
    private static AppController sInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        sInstance = this;
    }

    public static synchronized AppController getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}