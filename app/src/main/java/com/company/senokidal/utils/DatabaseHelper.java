package com.company.senokidal.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.company.senokidal.Splash;
import com.company.senokidal.model.Absensi;
import com.company.senokidal.model.Kelas;
import com.company.senokidal.model.Nilai;
import com.company.senokidal.model.Pelajaran;
import com.company.senokidal.model.Recent;
import com.company.senokidal.model.Siswa;

public class DatabaseHelper extends SQLiteOpenHelper {

    static SharedPreferences sharedpreferences;
    // Database Version
    private static final int DATABASE_VERSION = 12;

    // Database Name
    public static final String DATABASE_NAME = "disiplin";



    String ta, semester;
    static Context context;

    public String getTahunAjaran(){
        Date date_sekarang = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");

        String ta = sharedpreferences.getString("ta", simpleDateformat.format(date_sekarang));
        int ta1 = Integer.valueOf(ta);
        return ta1+"-"+(ta1+1);
    }

    public void setTahunAjaran(String tahunajaran){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("ta",String.valueOf(tahunajaran));
        editor.apply();

    }

    public String getSemester(){
        return sharedpreferences.getString("semester", "ganjil");
    }


    public String getAsalSekolah(){
        return sharedpreferences.getString("asalsekolah", "Untitled");
    }

    /**
     * DatabaseHelper
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        this.context = context;


        ta = getTahunAjaran();
        semester = getSemester();
    }

    /**
     * onCreate
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e("DB","Create");

        db.execSQL(Absensi.CREATE_TABLE);
        db.execSQL(Kelas.CREATE_TABLE);
        db.execSQL(Nilai.CREATE_TABLE);
        db.execSQL(Siswa.CREATE_TABLE);
        db.execSQL(Pelajaran.CREATE_TABLE);
        db.execSQL(Recent.CREATE_TABLE);


        //db.execSQL(Kelas.INSERT_TABLE);
        //db.execSQL(Siswa.INSERT_TABLE);
        //db.execSQL(Absensi.INSERT_TABLE);
        //db.execSQL(Nilai.INSERT_TABLE);
    }

    /**
     * onUpgrade
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("DB","Upgrade");

        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + Kelas.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + Siswa.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + Absensi.TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + Nilai.TABLE_NAME);

        try{
            db.execSQL("ALTER TABLE " + Absensi.TABLE_NAME + " ADD absensi_terlambat INTEGER");
            db.execSQL("ALTER TABLE " + Absensi.TABLE_NAME + " ADD absensi_ta TEXT DEFAULT '" + ta + "'");
            db.execSQL("ALTER TABLE " + Absensi.TABLE_NAME + " ADD absensi_semester TEXT DEFAULT '" + semester + "'");

            db.execSQL("ALTER TABLE " + Nilai.TABLE_NAME + " ADD nilai_ta TEXT DEFAULT '" + ta + "'");
            db.execSQL("ALTER TABLE " + Nilai.TABLE_NAME + " ADD nilai_semester TEXT DEFAULT '" + semester + "'");

            db.execSQL("ALTER TABLE " + Siswa.TABLE_NAME + " ADD siswa_ta TEXT DEFAULT '" + ta + "'");
            db.execSQL("ALTER TABLE " + Siswa.TABLE_NAME + " ADD siswa_semester TEXT DEFAULT '" + semester + "'");
        }catch (SQLiteException e){
            Log.e("SQLiteException","Column in table Absensi, Nilai, Siswa is  exists");
        }

        try {
            db.execSQL(Pelajaran.CREATE_TABLE);
        }catch (SQLiteException e){
                Log.e("SQLiteException","Column table Pelajaran exists");
        }

        try{
            db.execSQL("ALTER TABLE " + Absensi.TABLE_NAME + " ADD absensi_pelajaran TEXT");
        }catch (SQLiteException e){
            Log.e("SQLiteException","Column in table Absensi exists");
        }

        try{
            db.execSQL(Recent.CREATE_TABLE);
        }catch (SQLiteException e){
            Log.e("SQLiteException","Column in table Absensi exists");
        }


        // Create tables again

        /**
         * ugrade aja ambil data drop -> insert ke table_tmp ulang
         */
        //onCreate(db);


    }


    public List<Kelas> getKelasAll() {
        List<Kelas> list = new ArrayList<>();

        String addQuery = "";

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Kelas.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        list.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Kelas(
                        cursor.getString(cursor.getColumnIndex(Kelas.COLUMN_KELAS_NAMA)),
                        cursor.getString(cursor.getColumnIndex(Kelas.COLUMN_KELAS_DESC))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return list;
    }

    public List<Siswa> getSiswaAll() {

        String addQuery = "";

        List<Siswa> siswa = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Siswa.TABLE_NAME + addQuery;

        //Log.e("sql",selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        siswa.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String jk = cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_JK));
                siswa.add(new Siswa(
                        cursor.getInt(cursor.getColumnIndex(Siswa.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Siswa.COLUMN_KELAS_ID)),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_KELAS_NAMA)),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_NIS)),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_NAMA)),
                        jk.toLowerCase(),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_FOTO))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return siswa;

    }

    public List<Pelajaran> getPelajaranAll() {
        List<Pelajaran> list = new ArrayList<>();

        String addQuery = "";

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Pelajaran.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        list.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Pelajaran(
                        cursor.getString(cursor.getColumnIndex(Pelajaran.COLUMN_PELAJARAN_NAMA)),
                        cursor.getString(cursor.getColumnIndex(Pelajaran.COLUMN_PELAJARAN_DESC))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return list;
    }


    public Kelas getKelas(long kelas_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Kelas.TABLE_NAME,
                new String[]{ Kelas.COLUMN_KELAS_ID,Kelas.COLUMN_KELAS_NAMA,Kelas.COLUMN_KELAS_DESC},
                Kelas.COLUMN_KELAS_ID + "=?",
                new String[]{String.valueOf(kelas_id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Kelas kelas = new Kelas(
                cursor.getInt(cursor.getColumnIndex(Kelas.COLUMN_KELAS_ID)),
                cursor.getString(cursor.getColumnIndex(Kelas.COLUMN_KELAS_NAMA)),
                cursor.getString(cursor.getColumnIndex(Kelas.COLUMN_KELAS_DESC))
        );

        // close db connection
        db.close();

        // return jadwals list
        return kelas;

    }



    public List<Absensi> getAbsensiGroup() {

        String addQuery = "";
        addQuery+=" GROUP BY "+Absensi.COLUMN_ABSENSI_TANGGAL+","+Absensi.COLUMN_ABSENSI_JAM+","+Absensi.COLUMN_KELAS_ID+","+Absensi.COLUMN_ABSENSI_PELAJARAN;
        addQuery+= " ORDER BY " + Absensi.COLUMN_ABSENSI_TANGGAL + " DESC";

        List<Absensi> absensi = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Absensi.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        absensi.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                absensi.add(new Absensi(
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_JAM)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_PELAJARAN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_HADIR)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ALFA)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_IZIN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_SAKIT)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TERLAMBAT))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return absensi;

    }



    public List<Absensi> getAbsensiBy(String absensi_tanggal, String absensi_jam, int kelas, String pelajaran) {

        String addQuery = "";
        addQuery+= " WHERE " + Absensi.COLUMN_ABSENSI_TANGGAL + "='"+absensi_tanggal+"' AND " + Absensi.COLUMN_ABSENSI_JAM + "='"+absensi_jam+"' AND " + Absensi.COLUMN_KELAS_ID + "="+kelas+"' AND " + Absensi.COLUMN_ABSENSI_PELAJARAN + "='"+pelajaran+"'";
        addQuery+= " ORDER BY " + Absensi.COLUMN_ABSENSI_TANGGAL + " DESC";
        List<Absensi> absensi = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Absensi.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        absensi.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                absensi.add(new Absensi(
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_JAM)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_PELAJARAN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_HADIR)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ALFA)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_IZIN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_SAKIT)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TERLAMBAT))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return absensi;

    }


    public List<Absensi> getAbsensi() {

        String addQuery = " ORDER BY " + Absensi.COLUMN_ABSENSI_TANGGAL + " DESC";
        List<Absensi> absensi = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Absensi.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        absensi.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                absensi.add(new Absensi(
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_JAM)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_PELAJARAN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_HADIR)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ALFA)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_IZIN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_SAKIT)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TERLAMBAT))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return absensi;

    }

    /**
     * DATA LAMA
     */



    public List<Kelas> getKelasAll2() {
        List<Kelas> list = new ArrayList<>();

        String addQuery = "";

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Kelas.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        list.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(new Kelas(
                        cursor.getInt(cursor.getColumnIndex(Kelas.COLUMN_KELAS_ID)),
                        cursor.getString(cursor.getColumnIndex(Kelas.COLUMN_KELAS_NAMA)),
                        cursor.getString(cursor.getColumnIndex(Kelas.COLUMN_KELAS_DESC))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        return list;
    }

    public List<Absensi> getAbsensiAllByTanggal(int kelas_id) {
        List<Absensi> absensi = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Absensi.TABLE_NAME + " WHERE "+Absensi.COLUMN_KELAS_ID+"="+ kelas_id +"  GROUP BY "+Absensi.COLUMN_ABSENSI_TANGGAL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        absensi.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                absensi.add(new Absensi(
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_JAM)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_PELAJARAN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_HADIR)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ALFA)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_IZIN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_SAKIT)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TERLAMBAT))
                ));


            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return absensi;

    }

    public List<Siswa> SiswaGetAll(int kelas_id) {

        String addQuery = "";
        if (kelas_id > 0){

            /**addQuery = " WHERE " + Siswa.COLUMN_KELAS_ID + "='" + kelas_id + "' AND "+Siswa.COLUMN_SISWA_TA+"='"+ta+"' AND "+Siswa.COLUMN_SISWA_SEMESTER + "='"+semester+"' ORDER BY " + Siswa.COLUMN_SISWA_NAMA + " ASC";*/
            addQuery = " WHERE " + Siswa.COLUMN_KELAS_ID + "='" + kelas_id + "' ORDER BY " + Siswa.COLUMN_SISWA_NAMA + " ASC";
        }
        else if(kelas_id == 0)
            addQuery = " ORDER BY " + Siswa.COLUMN_KELAS_ID + " ASC," + Siswa.COLUMN_SISWA_NAMA + " ASC";



        List<Siswa> siswa = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Siswa.TABLE_NAME + addQuery;

        //Log.e("sql",selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        siswa.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String jk = cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_JK));
                siswa.add(new Siswa(
                        cursor.getInt(cursor.getColumnIndex(Siswa.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Siswa.COLUMN_KELAS_ID)),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_KELAS_NAMA)),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_NIS)),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_NAMA)),
                        jk.toLowerCase(),
                        cursor.getString(cursor.getColumnIndex(Siswa.COLUMN_SISWA_FOTO))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return siswa;

    }

    public List<Absensi> AbsensiGetAll(int id, String absensi_tanggal, String absensi_jam, boolean kelas) {


        String addQuery = " ORDER BY " + Absensi.COLUMN_ABSENSI_TANGGAL + " DESC";
        if (id > 0 && kelas == false)
            addQuery = " WHERE " + Absensi.COLUMN_SISWA_ID + "=" + id +" ORDER BY " + Absensi.COLUMN_ABSENSI_TANGGAL + " DESC, " + Absensi.COLUMN_ABSENSI_JAM + " DESC";
        else if (id == 0 && kelas == true && absensi_tanggal != null && absensi_jam == null)
            addQuery = " WHERE " + Absensi.COLUMN_ABSENSI_TANGGAL + "='" + absensi_tanggal+"' GROUP BY "+Absensi.COLUMN_ABSENSI_JAM +","+Absensi.COLUMN_KELAS_ID+" ORDER BY "+Absensi.COLUMN_KELAS_ID +" DESC,"+Absensi.COLUMN_ABSENSI_JAM +" DESC";
        else if (id > 0 && kelas == true && absensi_tanggal != null && absensi_jam != null)
            addQuery = " WHERE " + Absensi.COLUMN_ABSENSI_TANGGAL + "='" + absensi_tanggal+"' AND " + Absensi.COLUMN_ABSENSI_JAM + "='" + absensi_jam+"' AND " + Absensi.COLUMN_KELAS_ID+ "=" + id;
        else if (id > 0 && kelas == true && absensi_tanggal != null && absensi_jam == null)
            addQuery = " WHERE " + Absensi.COLUMN_ABSENSI_TANGGAL + "='" + absensi_tanggal+"' AND " + Absensi.COLUMN_KELAS_ID+ "=" + id + addQuery;
        else if(id == 0 && absensi_tanggal == null && kelas == false)
            addQuery = " GROUP BY "+ Absensi.COLUMN_ABSENSI_TANGGAL + addQuery;
        else if(id == 0 && absensi_tanggal != null && kelas == false)
            addQuery = " WHERE " + Absensi.COLUMN_ABSENSI_TANGGAL + "='" + absensi_tanggal;


        List<Absensi> absensi = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Absensi.TABLE_NAME + addQuery;


        //Log.e("sql",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        absensi.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                absensi.add(new Absensi(
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TANGGAL)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_JAM)),
                        cursor.getString(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_PELAJARAN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_SISWA_ID)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_HADIR)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_ALFA)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_IZIN)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_SAKIT)),
                        cursor.getInt(cursor.getColumnIndex(Absensi.COLUMN_ABSENSI_TERLAMBAT))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return absensi;

    }




    public List<Nilai> getNilaiAllByTanggal(int kelas_id) {
        List<Nilai> nilai = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Nilai.TABLE_NAME + " WHERE "+Nilai.COLUMN_KELAS_ID+"="+ kelas_id +" GROUP BY "+Nilai.COLUMN_NILAI_TANGGAL + "," +Nilai.COLUMN_NILAI_DESC;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        nilai.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                nilai.add(new Nilai(
                        cursor.getString(cursor.getColumnIndex(Nilai.COLUMN_NILAI_TANGGAL)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_SISWA_ID)),
                        cursor.getString(cursor.getColumnIndex(Nilai.COLUMN_NILAI_DESC)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_NILAI))
                ));


            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return nilai;

    }


    public List<Nilai> NilaiGetAll(int id, String nilai_tanggal,boolean kelas,boolean nilai_desc) {

        String addQuery = " ORDER BY " + Nilai.COLUMN_NILAI_TANGGAL + " DESC";
        if (id > 0 && kelas == false && nilai_desc == false)
            addQuery = " WHERE " + Nilai.COLUMN_SISWA_ID + "=" + id +" ORDER BY " + Nilai.COLUMN_NILAI_TANGGAL + " DESC";
        else if (id == 0 && kelas == true && nilai_tanggal != null && nilai_desc == true)
            addQuery = " WHERE " + Nilai.COLUMN_NILAI_TANGGAL + "='" + nilai_tanggal+"' GROUP BY " + Nilai.COLUMN_KELAS_ID + "," + Nilai.COLUMN_NILAI_DESC;
        else if (id == 0 && kelas == true && nilai_tanggal != null && nilai_desc == false)
            addQuery = " WHERE " + Nilai.COLUMN_NILAI_TANGGAL + "='" + nilai_tanggal+"' GROUP BY " + Nilai.COLUMN_KELAS_ID;
        else if (id > 0 && kelas == true && nilai_tanggal != null && nilai_desc == false)
            addQuery = " WHERE " + Nilai.COLUMN_NILAI_TANGGAL + "='" + nilai_tanggal+"' AND " + Nilai.COLUMN_KELAS_ID+ "=" + id;
        else if(id == 0 && nilai_tanggal == null && kelas == false && nilai_desc == false)
            addQuery = " GROUP BY "+ Nilai.COLUMN_NILAI_TANGGAL + addQuery;
        else if(id == 0 && nilai_tanggal != null && kelas == false && nilai_desc == false)
            addQuery = " WHERE " + Nilai.COLUMN_NILAI_TANGGAL + "='" + nilai_tanggal;


        List<Nilai> nilai = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Nilai.TABLE_NAME + addQuery;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        nilai.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                nilai.add(new Nilai(
                        cursor.getString(cursor.getColumnIndex(Nilai.COLUMN_NILAI_TANGGAL)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_KELAS_ID)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_SISWA_ID)),
                        cursor.getString(cursor.getColumnIndex(Nilai.COLUMN_NILAI_DESC)),
                        cursor.getInt(cursor.getColumnIndex(Nilai.COLUMN_NILAI))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return nilai;

    }


    /**
     * DATA RECENT REKAP
     */

    public List<Recent> RekapRecentAll() {

        String addQuery = " ORDER BY recent_tanggal DESC";

        List<Recent> recent = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM recent" + addQuery;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        recent.clear();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                recent.add(new Recent(
                        cursor.getInt(cursor.getColumnIndex("recent_id")),
                        cursor.getString(cursor.getColumnIndex("recent_tanggal")),
                        cursor.getString(cursor.getColumnIndex("recent_tipe")),
                        cursor.getString(cursor.getColumnIndex("recent_files")),
                        cursor.getString(cursor.getColumnIndex("recent_opsi1")),
                        cursor.getString(cursor.getColumnIndex("recent_opsi2"))
                ));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return jadwals list
        return recent;
    }

    public long RecentInsert(ContentValues values){

        SQLiteDatabase db = this.getWritableDatabase();
        // insert row
        long id = db.insert("recent", null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public void RecentDel(int recent_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("recent", "recent_id = ?",
                new String[]{String.valueOf(recent_id)});
        db.close();
    }
}