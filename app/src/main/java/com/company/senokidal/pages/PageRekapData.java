package com.company.senokidal.pages;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.LoginActivity;
import com.company.senokidal.R;
import com.company.senokidal.adapters.AdapterRuangBelajarDialog;
import com.company.senokidal.model.Kehadiran;
import com.company.senokidal.model.MataPelajaran;
import com.company.senokidal.model.Murid;
import com.company.senokidal.model.Penilaian;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.model.SikapPrilaku;
import com.company.senokidal.model.Status;
import com.company.senokidal.utils.DatabaseHelper;
import com.company.senokidal.utils.Fungsi;

public class PageRekapData extends AppCompatActivity {
    String TAG = "MainActivity";

    private String getUserID;
    private FirebaseFirestore db;

    SweetAlertDialog sweetAlertDialog;
    Context context;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    Workbook workbook;
    HSSFSheet sheet;
    Row row;

    ArrayList<Kehadiran> kehadirans;
    ArrayList<Penilaian> penilaians;
    ArrayList<SikapPrilaku> sikapPrilakus;
    ArrayList<Murid> murids;

    String jenis = "";
    String kelas = "";
    String pelajaran = "";

    CoordinatorLayout coordinatorLayout;
    public static DatabaseHelper dbase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_rekapdata);

        context = PageRekapData.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        init();

    }

    private void init() {

        dbase = new DatabaseHelper(context);
        db = FirebaseFirestore.getInstance();
        getUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        if (getUserID == null) {

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        /**
         * Loading
         */

        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Tunggu sebentar");
        sweetAlertDialog.setContentText("Sedang memperbaharui data");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        MaterialButton action_simpan = findViewById(R.id.action_simpan);

        AutoCompleteTextView tf_jenis = findViewById(R.id.tf_jenis);
        TextInputEditText tf_tanggal_dari = findViewById(R.id.tf_tanggal_dari);
        TextInputEditText tf_tanggal_ke = findViewById(R.id.tf_tanggal_ke);
        AutoCompleteTextView tf_kelas = findViewById(R.id.tf_kelas);
        AutoCompleteTextView tf_pelajaran = findViewById(R.id.tf_pelajaran);

        TextInputLayout tf_kelas_lyt = findViewById(R.id.tf_kelas_lyt);
        TextInputLayout tf_pelajaran_lyt = findViewById(R.id.tf_pelajaran_lyt);
        LinearLayout tf_tanggal_darike_lyt = findViewById(R.id.tf_tanggal_darike_lyt);

        action_simpan.setEnabled(true);
        tf_kelas_lyt.setVisibility(View.GONE);
        tf_pelajaran_lyt.setVisibility(View.GONE);
        tf_tanggal_darike_lyt.setVisibility(View.GONE);

        tf_kelas.setOnClickListener(v->{
            action_simpan.setEnabled(true);
        });
        tf_pelajaran.setOnClickListener(v->{
            action_simpan.setEnabled(true);
        });


        tf_jenis.setOnItemClickListener((adapterView, view, position, l) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            action_simpan.setEnabled(true);

            if(item.equalsIgnoreCase("Daftar Ruang Belajar")){

                tf_kelas_lyt.setVisibility(View.GONE);
                tf_pelajaran_lyt.setVisibility(View.GONE);
                tf_tanggal_darike_lyt.setVisibility(View.GONE);

            }else if(item.equalsIgnoreCase("Daftar Siswa")){
                tf_kelas_lyt.setVisibility(View.VISIBLE);
                tf_pelajaran_lyt.setVisibility(View.GONE);
                tf_tanggal_darike_lyt.setVisibility(View.GONE);

            }else if(item.equalsIgnoreCase("Daftar Mata Pelajaran")){
                tf_kelas_lyt.setVisibility(View.GONE);
                tf_pelajaran_lyt.setVisibility(View.GONE);
                tf_tanggal_darike_lyt.setVisibility(View.GONE);

            }else if(item.equalsIgnoreCase("Daftar Kehadiran")){
                tf_kelas_lyt.setVisibility(View.VISIBLE);
                tf_pelajaran_lyt.setVisibility(View.VISIBLE);
                tf_tanggal_darike_lyt.setVisibility(View.VISIBLE);

            }else if(item.equalsIgnoreCase("Daftar Penilaian")){
                tf_kelas_lyt.setVisibility(View.VISIBLE);
                tf_pelajaran_lyt.setVisibility(View.VISIBLE);
                tf_tanggal_darike_lyt.setVisibility(View.VISIBLE);

            }else if(item.equalsIgnoreCase("Daftar Sikap dan Prilaku")){
                tf_kelas_lyt.setVisibility(View.VISIBLE);
                tf_pelajaran_lyt.setVisibility(View.GONE);
                tf_tanggal_darike_lyt.setVisibility(View.VISIBLE);

            }

        });


        db.collection("users/"+getUserID+"/ruangbelajar")
                .get()
                .addOnCompleteListener(task1 -> {
                    ArrayList<RuangBelajar> items1 = new ArrayList<>();
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task1.getResult()){
                            RuangBelajar item = document.toObject(RuangBelajar.class);
                            items1.add(item);

                        }


                        Comparator<RuangBelajar> compareById = (o1, o2) -> o1.kelas_desc.compareTo(o2.kelas_desc);
                        Collections.sort(items1, compareById);

                        ArrayAdapter arrayAdapter = new AdapterRuangBelajarDialog(PageRekapData.this,R.layout.item_ruangbelajar_dialog, items1);
                        tf_kelas.setAdapter(arrayAdapter);
                        tf_kelas.setOnItemClickListener((parent, view, position, id) -> {
                            String selected = items1.get(position).kelas_desc;
                            tf_kelas.setText(selected,false);
                            action_simpan.setEnabled(true);
                        });


                        db.collection("users/"+getUserID+"/matapelajaran")
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    ArrayList<String> items2 = new ArrayList<>();

                                    if (task2.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task2.getResult()){
                                            MataPelajaran item = document.toObject(MataPelajaran.class);
                                            items2.add(item.pelajaran_desc);
                                        }

                                        ArrayAdapter<String> itemsAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items2);
                                        tf_pelajaran.setAdapter(itemsAdapter2);
                                        sweetAlertDialog.dismiss();
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task2.getException());
                                    }
                                });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task1.getException());
                    }
                });

        ArrayList<String> items = new ArrayList<>();
        items.add("Daftar Ketua");
        items.add("Daftar Warga");
        items.add("Daftar Shift");
        items.add("Daftar Kehadiran");
        items.add("Daftar Penilaian");
        items.add("Daftar Sikap dan Prilaku");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        tf_jenis.setAdapter(itemsAdapter);

        tf_tanggal_dari.setText(sdf.format(new Date()));
        tf_tanggal_dari.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_tanggal_dari.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    PageRekapData.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        tf_tanggal_ke.setText(sdf.format(new Date()));
        tf_tanggal_ke.setOnClickListener(v->{

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tf_tanggal_ke.setText(sdf.format(myCalendar.getTime()));
            };

            new DatePickerDialog(
                    PageRekapData.this,
                    date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });


        action_simpan.setOnClickListener(v -> {
            v.setEnabled(false);

            jenis = tf_jenis.getText().toString();
            kelas = tf_kelas.getText().toString();
            pelajaran = tf_pelajaran.getText().toString();
            String tanggal_dari = tf_tanggal_dari.getText().toString();
            String tanggal_ke = tf_tanggal_ke.getText().toString();

            if(TextUtils.isEmpty(jenis)){
                Toast.makeText(PageRekapData.this, "Jenis rekap data blm dipilih!", Toast.LENGTH_SHORT).show();
            }else{

                sweetAlertDialog.show();
                workbook = new HSSFWorkbook();

                if(jenis.equalsIgnoreCase("Daftar Ketua")){
                    backupDataRuangBelajar();

                }else if(jenis.equalsIgnoreCase("Daftar Warga")){
                    backupDataSiswa();

                }else if(jenis.equalsIgnoreCase("Daftar Shift")){
                    backupDataMataPelajaran();

                }else if(jenis.equalsIgnoreCase("Daftar Kehadiran")){
                    backupDataKehadiran(pelajaran,tanggal_dari,tanggal_ke);

                }else if(jenis.equalsIgnoreCase("Daftar Penilaian")){
                    backupDataPenilaian(pelajaran,tanggal_dari,tanggal_ke);

                }else if(jenis.equalsIgnoreCase("Daftar Sikap dan Prilaku")){
                    backupDataSikapPrilaku(tanggal_dari,tanggal_ke);

                }


            }


        });
    }


    private void backupDataRuangBelajar(){
        db.collection("users/"+getUserID+"/ruangbelajar")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    ArrayList<RuangBelajar> items = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : value) {
                        RuangBelajar p = doc.toObject(RuangBelajar.class);
                        p.setKey( doc.getId() );

                        items.add( p );
                    }

                    Collections.sort(items);

                    String fileName = "RuangBelajar";
                    sheet = ((HSSFWorkbook) workbook).createSheet(fileName );


                    Cell cell;

                    row = sheet.createRow(0);
                    cell = row.createCell(0);
                    cell.setCellValue("Rekap Data Ruang Belajar");
                    //row.createCell(0).setCellValue("Mobile Phone Sensors Readings");

                    row = sheet.createRow(1);
                    row.createCell(0).setCellValue("No");
                    row.createCell(1).setCellValue("Kode Ruangan");
                    row.createCell(2).setCellValue("Nama Ruangan");

                    int nomor = 1;
                    int nomorRow = 2;
                    for(int i=0; i<=items.size()-1;i++){
                        RuangBelajar item = items.get(i);

                        row = sheet.createRow(nomorRow);
                        row.createCell(0).setCellValue(nomor);
                        row.createCell(1).setCellValue(item.kelas_nama);
                        row.createCell(2).setCellValue(item.kelas_desc);
                        nomor++;
                        nomorRow++;
                    }

                    saveFileXLS(workbook,fileName,"Kelas");

                    sweetAlertDialog.dismiss();
                });
    }


    private void backupDataMataPelajaran(){
        db.collection("users/"+getUserID+"/matapelajaran")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    ArrayList<MataPelajaran> items = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : value) {
                        MataPelajaran p = doc.toObject(MataPelajaran.class);
                        p.setKey( doc.getId() );

                        items.add( p );
                    }

                    Collections.sort(items);

                    String fileName = "Pelajaran";
                    sheet = ((HSSFWorkbook) workbook).createSheet(fileName );


                    Cell cell;

                    row = sheet.createRow(0);
                    cell = row.createCell(0);
                    cell.setCellValue("Rekap Data Ruang Belajar");
                    //row.createCell(0).setCellValue("Mobile Phone Sensors Readings");

                    row = sheet.createRow(1);
                    row.createCell(0).setCellValue("No");
                    row.createCell(1).setCellValue("Kode Pelajaran");
                    row.createCell(2).setCellValue("Nama Pelajaran");

                    int nomor = 1;
                    int nomorRow = 2;
                    for(int i=0; i<=items.size()-1;i++){
                        MataPelajaran item = items.get(i);

                        row = sheet.createRow(nomorRow);
                        row.createCell(0).setCellValue(nomor);
                        row.createCell(1).setCellValue(item.pelajaran_nama);
                        row.createCell(2).setCellValue(item.pelajaran_desc);
                        nomor++;
                        nomorRow++;
                    }

                    saveFileXLS(workbook,fileName,"MataPelajaran");

                    sweetAlertDialog.dismiss();
                });
    }


    private void backupDataSiswa(){
        db.collection("users/"+getUserID+"/siswa")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    ArrayList<Murid> items = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : value) {
                        Murid p = doc.toObject(Murid.class);
                        p.setKey( doc.getId() );

                        items.add( p );
                    }

                    Collections.sort(items);

                    String fileName = "Murid";
                    sheet = ((HSSFWorkbook) workbook).createSheet(fileName );


                    Cell cell;

                    row = sheet.createRow(0);
                    cell = row.createCell(0);
                    cell.setCellValue("Rekap Data Murid");
                    //row.createCell(0).setCellValue("Mobile Phone Sensors Readings");

                    row = sheet.createRow(1);
                    row.createCell(0).setCellValue("No");
                    row.createCell(1).setCellValue("Nama");
                    row.createCell(2).setCellValue("NIS");
                    row.createCell(3).setCellValue("Jenis Kelamin");

                    int nomor = 1;
                    int nomorRow = 2;
                    for(int i=0; i<=items.size()-1;i++){
                        Murid item = items.get(i);

                        if(item.kelas_key.equalsIgnoreCase(kelas)){

                            row = sheet.createRow(nomorRow);
                            row.createCell(0).setCellValue(nomor);
                            row.createCell(1).setCellValue(item.siswa_nama);
                            row.createCell(2).setCellValue(item.siswa_nis);
                            row.createCell(3).setCellValue(item.siswa_jk);
                            nomor++;
                            nomorRow++;

                        }
                    }

                    saveFileXLS(workbook,fileName,"Siswa");

                    sweetAlertDialog.dismiss();
                });
    }


    private void backupDataKehadiran(String pelajaran_key, String tanggal_dari, String tanggal_ke){
        kehadirans = new ArrayList<>();
        murids = new ArrayList<>();
        db.collection("users/"+getUserID+"/kehadiran")
                .whereEqualTo("kelas_key",kelas)
                .whereEqualTo("pelajaran_key",pelajaran_key)
                .addSnapshotListener((value1, e1) -> {
                    if (e1 != null) {
                        Log.w(TAG, "Listen failed.", e1);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : value1) {
                        Kehadiran p = doc.toObject(Kehadiran.class);
                        p.setKey( doc.getId() );

                        kehadirans.add( p );
                    }

                    Collections.sort(kehadirans);


                    db.collection("users/"+getUserID+"/siswa")
                            .whereEqualTo("kelas_key",kelas)
                            .addSnapshotListener((value2, e2) -> {
                                if (e2 != null) {
                                    Log.w(TAG, "Listen failed.", e2);
                                    return;
                                }

                                for (QueryDocumentSnapshot doc : value2) {
                                    Murid p = doc.toObject(Murid.class);
                                    p.setKey( doc.getId() );

                                    murids.add( p );
                                }

                                Collections.sort(murids);



                                String fileName = "Kehadiran";
                                sheet = ((HSSFWorkbook) workbook).createSheet(fileName);


                                Cell cell;

                                row = sheet.createRow(0);
                                cell = row.createCell(0);
                                cell.setCellValue("Rekap Data Kehadiran");
                                row = sheet.createRow(1);
                                cell = row.createCell(0);
                                cell.setCellValue("Kelas : "+kelas);
                                row = sheet.createRow(2);
                                cell = row.createCell(0);
                                cell.setCellValue("Pelajaran : "+pelajaran_key);

                                //lewati 1 baris

                                Row row1 = sheet.createRow(4);
                                row1.createCell(0).setCellValue("No");
                                row1.createCell(1).setCellValue("Nama");
                                row1.createCell(2).setCellValue("Tanggal");

                                //cetak tanggal dulu untuk kolom
                                row = sheet.createRow(5);

                                int nomorCol1 = 2;
                                for(int i=0; i<=kehadirans.size()-1;i++) {
                                    Kehadiran item1 = kehadirans.get(i);

                                    row.createCell(nomorCol1).setCellValue(item1.kehadiran_tanggal.toString());

                                    nomorCol1++;
                                }

                                row1.createCell(nomorCol1+0).setCellValue("Jumlah");
                                row.createCell(nomorCol1+0).setCellValue("Hadir");
                                row.createCell(nomorCol1+1).setCellValue("Alfa");
                                row.createCell(nomorCol1+2).setCellValue("Izin");
                                row.createCell(nomorCol1+3).setCellValue("Sakit");
                                row.createCell(nomorCol1+4).setCellValue("Terlambat");




                                //cetak nama siswa

                                int nomor = 1;
                                int nomorRow1 = 6;
                                for(int i=0; i<=murids.size()-1;i++) {
                                    Murid item = murids.get(i);

                                    row = sheet.createRow(nomorRow1);
                                    row.createCell(0).setCellValue(nomor);
                                    row.createCell(1).setCellValue(item.siswa_nama);


                                    int hadir = 0;
                                    int alfa = 0;
                                    int izin = 0;
                                    int sakit = 0;
                                    int terlambat = 0;

                                    int nomorCol2 = 2;
                                    for(int i2=0; i2<=kehadirans.size()-1;i2++) {
                                        Kehadiran item2 = kehadirans.get(i2);
                                        ArrayList<Status> status = item2.kehadiran_body;


                                        row.createCell(nomorCol2).setCellValue("");
                                        for(Status s:status){
                                            if(s.k.equalsIgnoreCase(item.key)){
                                                row.createCell(nomorCol2).setCellValue(s.v);

                                                if(s.v.equalsIgnoreCase("Hadir")){
                                                    hadir++;
                                                }else if(s.v.equalsIgnoreCase("Alfa")){
                                                    alfa++;
                                                }else if(s.v.equalsIgnoreCase("Izin")){
                                                    izin++;
                                                }else if(s.v.equalsIgnoreCase("Sakit")){
                                                    sakit++;
                                                }else if(s.v.equalsIgnoreCase("Terlambat")){
                                                    terlambat++;
                                                }


                                            }
                                        }

                                        nomorCol2++;
                                    }

                                    row.createCell(nomorCol2+0).setCellValue(hadir);
                                    row.createCell(nomorCol2+1).setCellValue(alfa);
                                    row.createCell(nomorCol2+2).setCellValue(izin);
                                    row.createCell(nomorCol2+3).setCellValue(sakit);
                                    row.createCell(nomorCol2+4).setCellValue(terlambat);


                                    nomor++;
                                    nomorRow1++;
                                }

                                saveFileXLS(workbook,fileName,"Kehadiran");

                                sweetAlertDialog.dismiss();
                            });
                });
    }


    private void backupDataPenilaian(String pelajaran_key, String tanggal_dari, String tanggal_ke){
        penilaians = new ArrayList<>();
        murids = new ArrayList<>();
        db.collection("users/"+getUserID+"/penilaian")
                .whereEqualTo("kelas_key",kelas)
                .whereEqualTo("pelajaran_key",pelajaran_key)
                .addSnapshotListener((value1, e1) -> {
                    if (e1 != null) {
                        Log.w(TAG, "Listen failed.", e1);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : value1) {
                        Penilaian p = doc.toObject(Penilaian.class);
                        p.setKey( doc.getId() );

                        penilaians.add( p );
                    }

                    Collections.sort(penilaians);


                    db.collection("users/"+getUserID+"/siswa")
                            .whereEqualTo("kelas_key",kelas)
                            .addSnapshotListener((value2, e2) -> {
                                if (e2 != null) {
                                    Log.w(TAG, "Listen failed.", e2);
                                    return;
                                }

                                for (QueryDocumentSnapshot doc : value2) {
                                    Murid p = doc.toObject(Murid.class);
                                    p.setKey( doc.getId() );

                                    murids.add( p );
                                }

                                Collections.sort(murids);



                                String fileName = "Penilaian";
                                sheet = ((HSSFWorkbook) workbook).createSheet(fileName);


                                Cell cell;

                                row = sheet.createRow(0);
                                cell = row.createCell(0);
                                cell.setCellValue("Rekap Data Penilaian");
                                row = sheet.createRow(1);
                                cell = row.createCell(0);
                                cell.setCellValue("Kelas : "+kelas);
                                row = sheet.createRow(2);
                                cell = row.createCell(0);
                                cell.setCellValue("Pelajaran : "+pelajaran_key);

                                //lewati 1 baris

                                Row row1 = sheet.createRow(4);
                                row1.createCell(0).setCellValue("No");
                                row1.createCell(1).setCellValue("Nama");
                                row1.createCell(2).setCellValue("Tanggal");

                                //cetak tanggal dulu untuk kolom
                                row = sheet.createRow(5);

                                int nomorCol1 = 2;
                                for(int i=0; i<=penilaians.size()-1;i++) {
                                    Penilaian item1 = penilaians.get(i);

                                    row.createCell(nomorCol1).setCellValue(item1.penilaian_tanggal);

                                    nomorCol1++;
                                }

                                row1.createCell(nomorCol1+0).setCellValue("Nilai");
                                row.createCell(nomorCol1+0).setCellValue("Total");
                                row.createCell(nomorCol1+1).setCellValue("Rata2x");




                                //cetak nama siswa

                                int nomor = 1;
                                int nomorRow1 = 6;
                                for(int i=0; i<=murids.size()-1;i++) {
                                    Murid item = murids.get(i);

                                    row = sheet.createRow(nomorRow1);
                                    row.createCell(0).setCellValue(nomor);
                                    row.createCell(1).setCellValue(item.siswa_nama);


                                    int total = 0;
                                    float rata2x = 0;
                                    int jumlah = 0;

                                    int nomorCol2 = 2;
                                    for(int i2=0; i2<=penilaians.size()-1;i2++) {
                                        Penilaian item2 = penilaians.get(i2);
                                        ArrayList<Status> status = item2.penilaian_body;


                                        row.createCell(nomorCol2).setCellValue("");
                                        for(Status s:status){
                                            if(s.k.equalsIgnoreCase(item.key)){
                                                row.createCell(nomorCol2).setCellValue(s.v);

                                                int nilai = Integer.parseInt(s.v);
                                                total = total + nilai;


                                            }
                                        }

                                        nomorCol2++;
                                        jumlah++;
                                    }

                                    if(total > 0)
                                        rata2x = total / jumlah;

                                    row.createCell(nomorCol2+0).setCellValue(total);
                                    row.createCell(nomorCol2+1).setCellValue(rata2x);


                                    nomor++;
                                    nomorRow1++;
                                }

                                saveFileXLS(workbook,fileName,"Penilaian");

                                sweetAlertDialog.dismiss();
                            });
                });
    }


    private void backupDataSikapPrilaku(String tanggal_dari, String tanggal_ke){
        sikapPrilakus = new ArrayList<>();
        murids = new ArrayList<>();
        db.collection("users/"+getUserID+"/sikapprilaku")
                .whereEqualTo("kelas_key",kelas)
                .addSnapshotListener((value1, e1) -> {
                    if (e1 != null) {
                        Log.w(TAG, "Listen failed.", e1);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : value1) {
                        SikapPrilaku p = doc.toObject(SikapPrilaku.class);
                        p.setKey( doc.getId() );

                        sikapPrilakus.add( p );
                    }

                    Collections.sort(sikapPrilakus);


                    db.collection("users/"+getUserID+"/siswa")
                            .whereEqualTo("kelas_key",kelas)
                            .addSnapshotListener((value2, e2) -> {
                                if (e2 != null) {
                                    Log.w(TAG, "Listen failed.", e2);
                                    return;
                                }

                                for (QueryDocumentSnapshot doc : value2) {
                                    Murid p = doc.toObject(Murid.class);
                                    p.setKey( doc.getId() );

                                    murids.add( p );
                                }

                                Collections.sort(murids);



                                String fileName = "SikapPrilaku";
                                sheet = ((HSSFWorkbook) workbook).createSheet(fileName);


                                Cell cell;

                                row = sheet.createRow(0);
                                cell = row.createCell(0);
                                cell.setCellValue("Rekap Data Sikap dan Prilaku");
                                row = sheet.createRow(1);
                                cell = row.createCell(0);
                                cell.setCellValue("Kelas : "+kelas);
                                row = sheet.createRow(2);
                                cell = row.createCell(0);
                                cell.setCellValue("");

                                //lewati 1 baris

                                Row row1 = sheet.createRow(4);
                                row1.createCell(0).setCellValue("No");
                                row1.createCell(1).setCellValue("Nama");
                                row1.createCell(2).setCellValue("Tanggal");

                                //cetak tanggal dulu untuk kolom
                                row = sheet.createRow(5);

                                int nomorCol1 = 2;
                                for(int i=0; i<=sikapPrilakus.size()-1;i++) {
                                    SikapPrilaku item1 = sikapPrilakus.get(i);

                                    row.createCell(nomorCol1).setCellValue(item1.sikapprilaku_tanggal);

                                    nomorCol1++;
                                }

                                row1.createCell(nomorCol1+0).setCellValue("Keterangan");
                                row.createCell(nomorCol1+0).setCellValue("A");
                                row.createCell(nomorCol1+1).setCellValue("B");
                                row.createCell(nomorCol1+2).setCellValue("C");
                                row.createCell(nomorCol1+3).setCellValue("D");
                                row.createCell(nomorCol1+4).setCellValue("E");




                                //cetak nama siswa

                                int nomor = 1;
                                int nomorRow1 = 6;
                                for(int i=0; i<=murids.size()-1;i++) {
                                    Murid item = murids.get(i);

                                    row = sheet.createRow(nomorRow1);
                                    row.createCell(0).setCellValue(nomor);
                                    row.createCell(1).setCellValue(item.siswa_nama);

                                    int a = 0;
                                    int b = 0;
                                    int c = 0;
                                    int d = 0;
                                    int e = 0;

                                    int nomorCol2 = 2;
                                    for(int i2=0; i2<=sikapPrilakus.size()-1;i2++) {
                                        SikapPrilaku item2 = sikapPrilakus.get(i2);
                                        ArrayList<Status> status = item2.sikapprilaku_body;


                                        row.createCell(nomorCol2).setCellValue("");
                                        for(Status s:status){
                                            if(s.k.equalsIgnoreCase(item.key)){
                                                row.createCell(nomorCol2).setCellValue(s.v);


                                                if(s.v.equalsIgnoreCase("A")){
                                                    a++;
                                                }else if(s.v.equalsIgnoreCase("B")){
                                                    b++;
                                                }else if(s.v.equalsIgnoreCase("C")){
                                                    c++;
                                                }else if(s.v.equalsIgnoreCase("D")){
                                                    d++;
                                                }else if(s.v.equalsIgnoreCase("E")){
                                                    e++;
                                                }


                                            }
                                        }

                                        nomorCol2++;
                                    }


                                    row.createCell(nomorCol2+0).setCellValue(a);
                                    row.createCell(nomorCol2+1).setCellValue(b);
                                    row.createCell(nomorCol2+2).setCellValue(c);
                                    row.createCell(nomorCol2+3).setCellValue(d);
                                    row.createCell(nomorCol2+4).setCellValue(e);


                                    nomor++;
                                    nomorRow1++;
                                }

                                saveFileXLS(workbook,fileName,"SikapPrilaku");

                                sweetAlertDialog.dismiss();


                            });
                });
    }


    private void saveFileXLS(Workbook wb, String fileName, String folderName){
        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name) + File.separator + folderName;


        File folder = new File(path);
        if(!folder.exists()) folder.mkdir();

        sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
        String tanggal_dibuat = sdf.format(new Date());
        // Create a path where we will place our List of objects on external storage
        File file = new File(path, fileName+'_'+tanggal_dibuat+".xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);


            String recent_tipe = "";
            String recent_opsi1 = "";
            String recent_opsi2 = "";

            if(jenis.equalsIgnoreCase("Daftar Ketua")){
                kelas = "";
                pelajaran = "";
            }else if(jenis.equalsIgnoreCase("Daftar Warga")){
                pelajaran = "";
            }else if(jenis.equalsIgnoreCase("Daftar Shift")){
                kelas = "";
            }else if(jenis.equalsIgnoreCase("Daftar Kehadiran")){
            }else if(jenis.equalsIgnoreCase("Daftar Penilaian")){
            }else if(jenis.equalsIgnoreCase("Daftar Sikap dan Prilaku")){
            }


            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());


            ContentValues value = new ContentValues();
            value.put("recent_tanggal",calendar.getTimeInMillis());
            value.put("recent_tipe", folderName);
            value.put("recent_files", file.getPath());
            value.put("recent_opsi1", kelas);
            value.put("recent_opsi2", pelajaran);
            dbase.RecentInsert(value);

            Snackbar.make(coordinatorLayout, "Export Data "+file.toString()+" success!", Snackbar.LENGTH_LONG)
                    .setAction("Buka File", v -> new Fungsi().openFile(context,file))
                    .show();

        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup2, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if(item.getItemId() == R.id.rekaplama){
            startActivity( new Intent(context, PageRekapDataLama.class) );
        }else if(item.getItemId() == R.id.action_history){
            startActivity( new Intent(context, PageRekapDataHistory.class) );
        }

        return super.onOptionsItemSelected(item);
    }
}