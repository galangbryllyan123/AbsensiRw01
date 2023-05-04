package com.company.senokidal.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.company.senokidal.LoginActivity;
import com.company.senokidal.R;
import com.company.senokidal.adapters.AdapterRuangBelajarDialog;
import com.company.senokidal.model.MataPelajaran;
import com.company.senokidal.model.Murid;
import com.company.senokidal.model.RuangBelajar;
import com.company.senokidal.utils.FileUtils;

public class PageImportData extends AppCompatActivity {
    String TAG = "MainActivity";

    private String getUserID;
    private FirebaseFirestore db;

    SweetAlertDialog sweetAlertDialog;
    Context context;

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    ArrayList<Murid> murids;

    CoordinatorLayout coordinatorLayout;
    int FILE_SELECT_CODE_KELAS = 111;
    int FILE_SELECT_CODE_SISWA = 112;
    int FILE_SELECT_CODE_PELAJARAN = 113;

    String jenis,kelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_importdata);

        context = PageImportData.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {

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

        MaterialButton action_template = findViewById(R.id.action_template);
        MaterialButton action_simpan = findViewById(R.id.action_simpan);

        AutoCompleteTextView tf_jenis = findViewById(R.id.tf_jenis);
        AutoCompleteTextView tf_kelas = findViewById(R.id.tf_kelas);

        TextInputLayout tf_kelas_lyt = findViewById(R.id.tf_kelas_lyt);

        action_simpan.setEnabled(true);
        tf_kelas_lyt.setVisibility(View.GONE);

        tf_jenis.setOnItemClickListener((adapterView, view, position, l) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            action_simpan.setEnabled(true);

            if(item.equalsIgnoreCase("Daftar Ketua")){

                tf_kelas_lyt.setVisibility(View.GONE);
            }else if(item.equalsIgnoreCase("Daftar Warga")){
                tf_kelas_lyt.setVisibility(View.VISIBLE);

            }else if(item.equalsIgnoreCase("Daftar Shift")){
                tf_kelas_lyt.setVisibility(View.GONE);

            }

        });


        db.collection("users/"+getUserID+"/Ketua")
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

                        ArrayAdapter arrayAdapter = new AdapterRuangBelajarDialog(PageImportData.this,R.layout.item_ruangbelajar_dialog, items1);
                        tf_kelas.setAdapter(arrayAdapter);
                        tf_kelas.setOnItemClickListener((parent, view, position, id) -> {
                            String selected = items1.get(position).kelas_desc;
                            tf_kelas.setText(selected,false);
                        });

                        sweetAlertDialog.dismiss();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task1.getException());
                    }
                });

        ArrayList<String> items = new ArrayList<>();
        items.add("Daftar Ketua");
        items.add("Daftar Warga");
        items.add("Daftar Shift");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        tf_jenis.setAdapter(itemsAdapter);



        action_simpan.setOnClickListener(v -> {
            jenis = tf_jenis.getText().toString();
            kelas = tf_kelas.getText().toString();

            if(TextUtils.isEmpty(jenis)){
                Toast.makeText(context, "Jenis import data blm dipilih!", Toast.LENGTH_SHORT).show();
            }else {
                if (jenis.equalsIgnoreCase("Daftar Ketua")) {
                    fileBrowse(FILE_SELECT_CODE_KELAS);

                } else if (jenis.equalsIgnoreCase("Daftar Warga")) {
                    fileBrowse(FILE_SELECT_CODE_SISWA);

                } else if (jenis.equalsIgnoreCase("Daftar Shift")) {
                    fileBrowse(FILE_SELECT_CODE_PELAJARAN);

                }
            }
        });



        findViewById(R.id.action_template).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.url_template_xls))));
        });
    }

    public void fileBrowse(int FILE_SELECT_CODE) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.ms-excel");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (Exception ex) {
            System.out.println("browseClick :"+ex);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            File file = new File( FileUtils.getPath(context, data.getData()) );

            if(!file.exists()) {
                Toast.makeText(context, "File tidak tersedia!", Toast.LENGTH_SHORT).show();
            }else {
                if (requestCode == 444) {
                } else if (requestCode == FILE_SELECT_CODE_KELAS) {


                    List<RuangBelajar> ruangBelajarList = new ArrayList<>();

                    try {
                        FileInputStream fis = new FileInputStream(file.getPath());

                        POIFSFileSystem myFileSystem = new POIFSFileSystem(fis);
                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                        Iterator rowIter = mySheet.rowIterator();

                        int i = 0;
                        while(rowIter.hasNext()){
                            HSSFRow row = (HSSFRow) rowIter.next();
                            if( i > 1 ) {
                                Iterator<Cell> cellIter = row.cellIterator();


                                HSSFCell a = row.getCell(0);
                                HSSFCell b = row.getCell(1);
                                HSSFCell c = row.getCell(2);

                                if(!a.toString().isEmpty() ){
                                    ruangBelajarList.add(new RuangBelajar(b.toString(), c.toString()));

                                    Log.e("a",b.toString() +"----"+ c.toString());
                                }


                            }
                            i++;

                        }

                        fis.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    CollectionReference nycRef = db.collection("users/" + getUserID + "/Ketua");
                    WriteBatch batch = db.batch();

                    if(ruangBelajarList.size() > 0){
                        for(RuangBelajar x:ruangBelajarList){
                            batch.set(nycRef.document(), x);
                        }

                        batch.commit().addOnCompleteListener(task -> {
                            Log.e("a","OK");
                        });
                    }

                } else if (requestCode == FILE_SELECT_CODE_SISWA) {


                    List<Murid> muridList = new ArrayList<>();

                    try {
                        FileInputStream fis = new FileInputStream(file.getPath());

                        POIFSFileSystem myFileSystem = new POIFSFileSystem(fis);
                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                        Iterator rowIter = mySheet.rowIterator();

                        int i = 0;
                        while(rowIter.hasNext()){
                            HSSFRow row = (HSSFRow) rowIter.next();
                            if( i > 1 ) {
                                Iterator<Cell> cellIter = row.cellIterator();


                                HSSFCell a = row.getCell(0);
                                HSSFCell b = row.getCell(1);
                                HSSFCell c = row.getCell(2);
                                HSSFCell d = row.getCell(3);

                                if(!a.toString().isEmpty() ){
                                    String jenis_kelamin = "";
                                    if(d.toString().equalsIgnoreCase("p")){
                                        jenis_kelamin = "Perempuan";
                                    }else if(d.toString().equalsIgnoreCase("l")){
                                        jenis_kelamin = "Laki-laki";

                                    }


                                    muridList.add(new Murid(kelas,b.toString(), c.toString(),jenis_kelamin,""));

                                    Log.e("a",b.toString() +"----"+ c.toString());
                                }


                            }
                            i++;

                        }

                        fis.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    CollectionReference nycRef = db.collection("users/" + getUserID + "/siswa");
                    WriteBatch batch = db.batch();

                    if(muridList.size() > 0){
                        for(Murid x:muridList){
                            batch.set(nycRef.document(), x);
                        }

                        batch.commit().addOnCompleteListener(task -> {
                            Log.e("a","OK");
                        });
                    }


                }else if (requestCode == FILE_SELECT_CODE_PELAJARAN) {


                    List<MataPelajaran> mataPelajaranList = new ArrayList<>();

                    try {
                        FileInputStream fis = new FileInputStream(file.getPath());

                        POIFSFileSystem myFileSystem = new POIFSFileSystem(fis);
                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                        Iterator rowIter = mySheet.rowIterator();

                        int i = 0;
                        while(rowIter.hasNext()){
                            HSSFRow row = (HSSFRow) rowIter.next();
                            if( i > 1 ) {
                                Iterator<Cell> cellIter = row.cellIterator();


                                HSSFCell a = row.getCell(0);
                                HSSFCell b = row.getCell(1);
                                HSSFCell c = row.getCell(2);

                                if(!a.toString().isEmpty() ){
                                    mataPelajaranList.add(new MataPelajaran(b.toString(), c.toString()));

                                    Log.e("a",b.toString() +"----"+ c.toString());
                                }


                            }
                            i++;

                        }

                        fis.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                    CollectionReference nycRef = db.collection("users/" + getUserID + "/matapelajaran");
                    WriteBatch batch = db.batch();

                    if(mataPelajaranList.size() > 0){
                        for(MataPelajaran x:mataPelajaranList){
                            batch.set(nycRef.document(), x);
                        }

                        batch.commit().addOnCompleteListener(task -> {
                            Log.e("a","OK");
                        });
                    }


                }
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