package com.company.senokidal.pages;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.company.senokidal.R;
import com.company.senokidal.model.Absensi;
import com.company.senokidal.model.Kelas;
import com.company.senokidal.model.Nilai;
import com.company.senokidal.model.Siswa;
import com.company.senokidal.utils.DatabaseHelper;
import com.company.senokidal.utils.Fungsi;

public class PageRekapDataLama extends AppCompatActivity {
    String TAG = "MainActivity";

    private String getUserID;
    private FirebaseFirestore db;

    Context context;

    CoordinatorLayout coordinatorLayout;
    public static DatabaseHelper dbase;
    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    Workbook workbook;
    HSSFSheet sheet;
    Row row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_rekapdata_lama);

        context = PageRekapDataLama.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dbase = new DatabaseHelper(context);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        MaterialButton action_simpan = findViewById(R.id.action_simpan);

        AutoCompleteTextView tf_jenis = findViewById(R.id.tf_jenis);


        action_simpan.setEnabled(true);


        ArrayList<String> items = new ArrayList<>();
        items.add("Daftar Kehadiran");
        items.add("Daftar Penilaian");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        tf_jenis.setAdapter(itemsAdapter);
        tf_jenis.setOnClickListener(v->{
            action_simpan.setEnabled(true);
        });


        action_simpan.setOnClickListener(v -> {
            v.setEnabled(false);

            String jenis = tf_jenis.getText().toString();

            if(TextUtils.isEmpty(jenis)){
                Toast.makeText(PageRekapDataLama.this, "Jenis rekap data blm dipilih!", Toast.LENGTH_SHORT).show();
            }else{

                final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name) + File.separator + "AppLama";

                File folder = new File(path);
                if(!folder.exists()) folder.mkdir();

                if(jenis.equalsIgnoreCase("Daftar Kehadiran")){
                    FileExportAbsensi();

                }else if(jenis.equalsIgnoreCase("Daftar Penilaian")){
                    FileExportNilai();

                }

            }


        });
    }

    public void FileExportAbsensi(){

        Workbook workbook =  new HSSFWorkbook();


        List<Kelas> k = dbase.getKelasAll2();

        if(k == null){
            Toast.makeText(PageRekapDataLama.this, "Data masih kosong!", Toast.LENGTH_SHORT).show();
        }else{

            Iterator<Kelas> list_k = k.iterator();

            while(list_k.hasNext()) {
                Kelas ik = list_k.next();
                Sheet sheet = workbook.createSheet("ABSENSI "+ ik.kelas_nama);


                Row rowxa = sheet.createRow(0);
                rowxa.createCell(0).setCellValue("");
                rowxa.createCell(1).setCellValue("");
                rowxa.createCell(3).setCellValue( "Tanggal" );

                Row rowx0 = sheet.createRow(1);
                rowx0.createCell(0).setCellValue("NIS");
                //Membuat Kolom NAMA SISWA
                rowx0.createCell(1).setCellValue("Nama Murid");
                rowx0.createCell(2).setCellValue("Jenis Kelamin");

                List<Absensi> a = dbase.getAbsensiAllByTanggal( ik.kelas_id );
                Iterator<Absensi> list_a = a.iterator();
                int columb2 = 3;
                while(list_a.hasNext()) {
                    Absensi ia = list_a.next();
                    //Membuat Kolom Dengan Tanggal
                    rowx0.createCell(columb2++).setCellValue( String.valueOf(ia.absensi_tanggal) );
                }

                rowxa.createCell(columb2+0).setCellValue( "Kehadiran" );
                //Membuat kolom jumlah Kehadiran
                rowx0.createCell(columb2+0).setCellValue( "Hadir" );
                rowx0.createCell(columb2+1).setCellValue( "Alfa" );
                rowx0.createCell(columb2+2).setCellValue( "Izin" );
                rowx0.createCell(columb2+3).setCellValue( "Sakit" );
                rowx0.createCell(columb2+4).setCellValue( "Terlambat" );


                List<Siswa> s = dbase.SiswaGetAll(ik.kelas_id);
                Iterator<Siswa> list_s = s.iterator();

                int rowIndex2 = 2;
                while(list_s.hasNext()) {
                    Siswa is = list_s.next();

                    Row rowy1 = sheet.createRow(rowIndex2++);
                    //Membuat Cell Row NIS
                    rowy1.createCell(0).setCellValue( String.valueOf(is.get_siswa_nis()) );
                    //Membuat Cell Row Nama Siswa
                    rowy1.createCell(1).setCellValue( String.valueOf(is.get_siswa_nama()) );
                    //Membuat Cell Row Jenis Kelamin
                    rowy1.createCell(2).setCellValue( String.valueOf(is.get_siswa_jk()) );

                    List<Absensi> ax = dbase.getAbsensiAllByTanggal( ik.kelas_id );
                    Iterator<Absensi> list_ax = ax.iterator();


                    int masuk = 0;
                    int alfa = 0;
                    int sakit = 0;
                    int izin = 0;
                    int terlambat = 0;

                    int rowIndex1x = 3;
                    while(list_ax.hasNext()) {
                        Absensi iax = list_ax.next();

                        //absensi_tanggal,siswa_id,kelas_id


                        String kehadiran = "";

                        for(Absensi az: dbase.AbsensiGetAll(ik.kelas_id,iax.absensi_tanggal,null,true)){

                            if( az.siswa_id == is.get_siswa_id() ) {

                                if( az.absensi_hadir == 1 ){
                                    kehadiran = "H";
                                    masuk++;
                                }else if( az.absensi_alfa == 1 ){
                                    kehadiran = "A";
                                    alfa++;
                                }else if( az.absensi_izin == 1 ){
                                    kehadiran = "I";
                                    sakit++;
                                }else if( az.absensi_sakit == 1 ){
                                    kehadiran = "S";
                                    izin++;
                                }else if( az.absensi_terlambat == 1 ){
                                    kehadiran = "T";
                                    terlambat++;
                                }
                            }
                        }
                        //Membuat Cell Row HAIS Kehadiran
                        rowy1.createCell(rowIndex1x++).setCellValue(  String.valueOf(kehadiran) );
                    }


                    //Membuat kolom jumlah Kehadiran
                    rowy1.createCell(rowIndex1x+0).setCellValue(  String.valueOf(masuk) );
                    rowy1.createCell(rowIndex1x+1).setCellValue(  String.valueOf(alfa) );
                    rowy1.createCell(rowIndex1x+2).setCellValue(  String.valueOf(izin) );
                    rowy1.createCell(rowIndex1x+3).setCellValue(  String.valueOf(sakit) );
                    rowy1.createCell(rowIndex1x+4).setCellValue(  String.valueOf(terlambat) );
                }
            }


            saveFileXLS(workbook,"Absensi");
        }

    }

    public void FileExportNilai(){

        Workbook workbook =  new HSSFWorkbook();


        List<Kelas> k = dbase.getKelasAll2();


        if(k == null){
            Toast.makeText(PageRekapDataLama.this, "Data masih kosong!", Toast.LENGTH_SHORT).show();
        }else{
            Iterator<Kelas> list_k = k.iterator();

            while(list_k.hasNext()) {
                Kelas ik = list_k.next();
                Sheet sheet = workbook.createSheet("NILAI "+ ik.kelas_nama);


                Row rowxa = sheet.createRow(0);
                rowxa.createCell(0).setCellValue("");
                rowxa.createCell(1).setCellValue("");
                rowxa.createCell(3).setCellValue( "Penilaian" );

                Row rowx0 = sheet.createRow(1);
                rowx0.createCell(0).setCellValue("NIS");
                //Membuat Kolom NAMA SISWA
                rowx0.createCell(1).setCellValue("Nama Murid");
                rowx0.createCell(2).setCellValue("Jenis Kelamin");

                List<Nilai> a = dbase.getNilaiAllByTanggal( ik.kelas_id );
                Iterator<Nilai> list_a = a.iterator();
                int columb2 = 3;
                while(list_a.hasNext()) {
                    Nilai ia = list_a.next();
                    //Membuat Kolom Dengan Tanggal
                    rowx0.createCell(columb2++).setCellValue( ia.get_nilai_desc() + " " + ia.get_nilai_tanggal() );
                }

                rowxa.createCell(columb2+0).setCellValue( "Nilai" );
                //Membuat kolom jumlah Kehadiran
                rowx0.createCell(columb2+0).setCellValue( "Total" );
                rowx0.createCell(columb2+1).setCellValue( "Rata2x" );


                List<Siswa> s = dbase.SiswaGetAll(ik.kelas_id);
                Iterator<Siswa> list_s = s.iterator();

                int rowIndex2 = 2;
                while(list_s.hasNext()) {
                    Siswa is = list_s.next();

                    Row rowy1 = sheet.createRow(rowIndex2++);
                    //Membuat Cell Row NIS
                    rowy1.createCell(0).setCellValue( String.valueOf(is.get_siswa_nis()) );
                    //Membuat Cell Row Nama Siswa
                    rowy1.createCell(1).setCellValue( String.valueOf(is.get_siswa_nama()) );
                    //Membuat Cell Row Jenis Kelamin
                    rowy1.createCell(2).setCellValue( String.valueOf(is.get_siswa_jk()) );

                    List<Nilai> ax = dbase.getNilaiAllByTanggal( ik.kelas_id );
                    Iterator<Nilai> list_ax = ax.iterator();


                    int nilai_total = 0;
                    int jumlah_penilaian = 0;

                    int rowIndex1x = 3;
                    while(list_ax.hasNext()) {
                        Nilai iax = list_ax.next();

                        //absensi_tanggal,siswa_id,kelas_id


                        int nilai = 0;
                        String nilai_desc = "";
                        for(Nilai n: dbase.NilaiGetAll(ik.kelas_id,iax.nilai_tanggal,true,false)){


                            if( n.get_siswa_id() == is.get_siswa_id() ) {

                                nilai = n.get_nilai();

                                if( n.get_nilai_desc() != null )
                                    nilai_desc = n.get_nilai_desc();

                                jumlah_penilaian++;
                                nilai_total = nilai_total+nilai;
                            }
                        }
                        //Membuat Cell Row HAIS Kehadiran
                        rowy1.createCell(rowIndex1x++).setCellValue(  String.valueOf(nilai) );
                    }


                    int nilai_rata_rata = 0;

                    if( nilai_total > 0 ) {
                        nilai_rata_rata = nilai_total / jumlah_penilaian;
                    }
                    //Membuat kolom jumlah Kehadiran
                    rowy1.createCell(rowIndex1x+0).setCellValue(  String.valueOf(nilai_total) );
                    rowy1.createCell(rowIndex1x+1).setCellValue(  String.valueOf(nilai_rata_rata) );
                }
            }

            saveFileXLS(workbook,"Nilai");

        }

    }



    private void saveFileXLS(Workbook wb, String folderName){
        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name) + File.separator + "AppLama";


        File folder = new File(path);
        if(!folder.exists()) folder.mkdir();

        sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
        String tanggal_dibuat = sdf.format(new Date());
        // Create a path where we will place our List of objects on external storage
        File file = new File(path, folderName+'_'+tanggal_dibuat+".xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);

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