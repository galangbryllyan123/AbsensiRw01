package com.company.senokidal.pages;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.R;
import com.company.senokidal.model.Versi;

public class PageLog extends AppCompatActivity {
    String TAG = "MainActivity";

    SweetAlertDialog sweetAlertDialog;
    private FirebaseFirestore db;


    RecyclerView recycle_view;
    LinearLayoutManager linearLayoutManager;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_log);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

        recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setNestedScrollingEnabled(false);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recycle_view.setLayoutManager(linearLayoutManager);

        db = FirebaseFirestore.getInstance();
        getList();
    }



    private void getList(){


        Query query = db.collection("log").orderBy("versi_code", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Versi> response = new FirestoreRecyclerOptions.Builder<Versi>()
                .setQuery(query, Versi.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Versi, AdapterViewHolder>(response) {

            @Override
            public void onBindViewHolder(AdapterViewHolder holder, int position, Versi model) {
                String docId = getSnapshots().getSnapshot(position).getId();

                holder.ev_title.setText("#" + model.versi_name);


                holder.perbaikan.setText( model.perbaikan.replace("\\n", "\n") );
                holder.penambahan.setText( model.penambahan.replace("\\n", "\n"));

                sweetAlertDialog.dismiss();
            }

            @Override
            public AdapterViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.item_versi, group, false);

                return new AdapterViewHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());

                sweetAlertDialog.dismiss();
            }
        };

        adapter.notifyDataSetChanged();
        recycle_view.setAdapter(adapter);
    }


    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView ev_title, perbaikan, penambahan;
        public AdapterViewHolder(View itemView) {
            super(itemView);

            ev_title = (TextView)itemView.findViewById(R.id.ev_title);
            perbaikan = (TextView)itemView.findViewById(R.id.perbaikan);
            penambahan = (TextView)itemView.findViewById(R.id.penambahan);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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