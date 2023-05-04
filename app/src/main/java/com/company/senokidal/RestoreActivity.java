package com.company.senokidal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.model.Kelas;
import com.company.senokidal.model.MataPelajaran;
import com.company.senokidal.model.Murid;
import com.company.senokidal.model.Pelajaran;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.model.Siswa;
import com.company.senokidal.utils.DatabaseHelper;

public class RestoreActivity extends AppCompatActivity {
    String TAG = "MainActivity";

    public static DatabaseHelper dbase;
    static SharedPreferences sharedpreferences;
    int restore = 0;

    private String getUserID;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    Context context;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        restore = sharedpreferences.getInt("restore", 0);


        context = RestoreActivity.this;

        dbase = new DatabaseHelper(RestoreActivity.this);

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        getUserID = user.getUid();


        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);



        TextView ev_title = findViewById(R.id.ev_title);
        ev_title.setText("Migrasikan data!");

        MaterialButton actionBack = findViewById(R.id.actionBack);
        MaterialButton actionRestoreNow = findViewById(R.id.actionRestoreNow);
        actionRestoreNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog.show();



                WriteBatch batch = db.batch();
                for(Kelas k:dbase.getKelasAll()){
                    DocumentReference nycRef = db.collection("users/"+getUserID+"/ruangbelajar").document();
                    batch.set(nycRef, new RuangBelajar(k.kelas_nama,k.kelas_desc));
                }


                for(Siswa s:dbase.getSiswaAll()){
                    DocumentReference nycRef = db.collection("users/"+getUserID+"/siswa").document();

                    String kelas = s.kelas_nama;
                    if(s.kelas_nama == null){
                        Kelas k = dbase.getKelas(s.kelas_id);
                        kelas = k.kelas_desc;
                        if(k.kelas_desc == null ) kelas = k.kelas_nama;
                    }

                    batch.set(nycRef, new Murid(kelas, s.siswa_nis, s.siswa_nama, s.siswa_jk, ""));
                }



                for(Pelajaran s:dbase.getPelajaranAll()){
                    DocumentReference nycRef = db.collection("users/"+getUserID+"/matapelajaran").document();
                    batch.set(nycRef, new MataPelajaran(s.pelajaran_nama, s.pelajaran_desc));
                }



                 /**
                for(Absensi s:dbase.getAbsensiGroup()){
                    //DocumentReference nycRef = db.collection("users/"+getUserID+"/kehadiran").document();

                    List<Status> kehadiranStatus = new ArrayList<>();
                    Log.e("x",s.absensi_tanggal+":"+s.absensi_jam+":"+s.kelas_id+":"+s.absensi_pelajaran);
                    for(Absensi r:dbase.getAbsensiBy(s.absensi_tanggal,s.absensi_jam,s.kelas_id,s.absensi_pelajaran)){
                        Log.e("r",r.absensi_tanggal+":"+r.absensi_jam+":"+r.kelas_id+":"+r.absensi_pelajaran);

                    }

                    //batch.set(nycRef, new Kehadiran(s.absensi_tanggal,s.absensi_jam,kelas_id,s.absensi_pelajaran,kehadiran_body,"",""));
                }*/



                batch.commit()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "onSuccess add");

                            sweetAlertDialog.dismiss();


                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("restore",2);
                            editor.apply();


                            Intent intent = new Intent(context, SkipActivity.class);
                            startActivity(intent);
                            finish();

                        })
                        .addOnFailureListener(e -> Log.e(TAG, "onFailure: "+e));



            }
        });

        //RESTORE DATA LAMA
        if(restore == 1){
            actionRestoreNow.setEnabled(true);
            ev_title.setText("Data akan dimigrasikan!");
        }else if(restore == 2){
            actionRestoreNow.setEnabled(false);
            ev_title.setText("Data sudah dimigrasikan!");
        }else{
            actionRestoreNow.setEnabled(true);
        }

        actionBack.setOnClickListener(v->{

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("restore",2);
            editor.apply();

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
