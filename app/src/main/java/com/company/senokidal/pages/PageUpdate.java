package com.company.senokidal.pages;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.R;
import com.company.senokidal.model.Versi;

public class PageUpdate extends AppCompatActivity {
    String TAG = "MainActivity";

    SweetAlertDialog sweetAlertDialog;
    private FirebaseFirestore db;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_update);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = PageUpdate.this;

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;//Version Name
        int verCode = pInfo.versionCode;//Version Code
        String versionName = version;


        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        db = FirebaseFirestore.getInstance();


        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon2 = findViewById(R.id.icon2);
        LinearLayout halamanPesan = findViewById(R.id.halamanPesan);
        LinearLayout halamanUpdate = findViewById(R.id.halamanUpdate);
        MaterialButton actUnduh = findViewById(R.id.actUnduh);
        TextView ev_title1 = findViewById(R.id.ev_title1);
        TextView perbaikan = findViewById(R.id.perbaikan);
        TextView penambahan = findViewById(R.id.penambahan);

        ev_title1.setText("#"+versionName);

        db.collection("log")
                .orderBy("versi_code", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sweetAlertDialog.dismiss();

                        boolean up = false;
                        String ver = "0";
                        String add = "-";
                        String fix = "-";
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Versi versi = document.toObject(Versi.class);

                            if(versi.versi_code > verCode ){
                                up = true;
                                ver = versi.versi_name;
                                add = versi.penambahan;
                                fix = versi.perbaikan;
                            }

                        }

                        if( up ){
                            icon1.setVisibility(View.VISIBLE);
                            icon2.setVisibility(View.GONE);

                            halamanUpdate.setVisibility(View.VISIBLE);
                            halamanPesan.setVisibility(View.GONE);

                            ev_title1.setText("#"+ver);
                            perbaikan.setText(fix);
                            penambahan.setText(add);

                            Log.e("on","Update");
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


        actUnduh.setOnClickListener(v->{


            String str;

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            str = getApplicationContext().getPackageName();

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+str)));

            } catch (android.content.ActivityNotFoundException n) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+str)));
            }

        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}