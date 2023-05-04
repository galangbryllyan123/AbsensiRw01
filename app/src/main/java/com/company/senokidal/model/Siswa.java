package com.company.senokidal.model;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class Siswa {
    public static final String TABLE_NAME = "siswa";
    public static final String TABLE_NAME_RESET = "TRUNCATE siswa";


    public static final String COLUMN_SISWA_ID = "siswa_id";
    public static final String COLUMN_KELAS_ID = "kelas_id";
    public static final String COLUMN_KELAS_NAMA = "kelas_nama";
    public static final String COLUMN_SISWA_NIS = "siswa_nis";
    public static final String COLUMN_SISWA_NAMA = "siswa_nama";
    public static final String COLUMN_SISWA_JK = "siswa_jk";
    public static final String COLUMN_SISWA_FOTO = "siswa_foto";
    public static final String COLUMN_SISWA_TA = "siswa_ta";
    public static final String COLUMN_SISWA_SEMESTER = "siswa_semester";


    private int siswa_id;
    public int kelas_id;
    public String kelas_nama;
    public String siswa_nis;
    public String siswa_nama;
    public String siswa_jk;
    public String siswa_foto;
    public String siswa_ta;
    public String siswa_semester;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_SISWA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_KELAS_ID + " INTEGER,"
                    + COLUMN_KELAS_NAMA + " TEXT,"
                    + COLUMN_SISWA_NIS + " TEXT,"
                    + COLUMN_SISWA_NAMA + " TEXT,"
                    + COLUMN_SISWA_JK + " TEXT,"
                    + COLUMN_SISWA_FOTO + " TEXT,"
                    + COLUMN_SISWA_TA + " TEXT,"
                    + COLUMN_SISWA_SEMESTER + " TEXT"
                    + ")";


    public static final String INSERT_TABLE =
            "INSERT INTO " + TABLE_NAME + "("
                    + COLUMN_SISWA_ID + ","
                    + COLUMN_KELAS_ID + ","
                    + COLUMN_KELAS_NAMA + ","
                    + COLUMN_SISWA_NIS + ","
                    + COLUMN_SISWA_NAMA + ","
                    + COLUMN_SISWA_JK + ","
                    + COLUMN_SISWA_TA + ","
                    + COLUMN_SISWA_SEMESTER + ") VALUES " +
                    "(1,1,'12345001','Ahmad A','l','2019','ganjil'),"+
                    "(2,1,'12345001','Ahmad B','l','2019','ganjil'),"+
                    "(3,1,'12345001','Ahmad C','p','2019','ganjil'),"+
                    "(4,2,'12345001','Ahmad D','l','2019','ganjil'),"+
                    "(5,2,'12345001','Ahmad E','l','2019','ganjil'),"+
                    "(6,2,'12345002','Ahmad F','p','2019','ganjil');";


    public int get_siswa_id() {
        return siswa_id;
    }
    public int get_kelas_id() {
        return kelas_id;
    }
    public String get_kelas_nama() {
        return kelas_nama;
    }
    public String get_siswa_nis() {
        return siswa_nis;
    }
    public String get_siswa_nama() {
        return siswa_nama;
    }
    public String get_siswa_jk() {
        return siswa_jk;
    }
    public String get_siswa_foto() {
        return siswa_foto;
    }

    public void set_siswa_id(int siswa_id) {
        this.siswa_id = siswa_id;
    }
    public void set_kelas_id(int kelas_id) {
        this.kelas_id = kelas_id;
    }
    public void set_kelas_nama(String kelas_nama) {
        this.kelas_nama = kelas_nama;
    }
    public void set_siswa_nis(String siswa_nis) {
        this.siswa_nis = siswa_nis;
    }
    public void set_siswa_nama(String siswa_nama) {
        this.siswa_nama = siswa_nama;
    }
    public void set_siswa_jk(String siswa_jk) {
        this.siswa_jk = siswa_jk;
    }
    public void set_siswa_foto(String siswa_foto) {
        this.siswa_foto = siswa_foto;
    }

    public Siswa(){}
    public Siswa(String kelas_nama,String siswa_nis, String siswa_nama, String siswa_jk, String siswa_foto){
        this.kelas_nama = kelas_nama;
        this.siswa_nis = siswa_nis;
        this.siswa_nama = siswa_nama;
        this.siswa_jk = siswa_jk;
        this.siswa_foto = siswa_foto;
    }
    public Siswa(int siswa_id, int kelas_id, String kelas_nama,String siswa_nis, String siswa_nama, String siswa_jk, String siswa_foto){
        this.siswa_id = siswa_id;
        this.kelas_id = kelas_id;
        this.kelas_nama = kelas_nama;
        this.siswa_nis = siswa_nis;
        this.siswa_nama = siswa_nama;
        this.siswa_jk = siswa_jk;
        this.siswa_foto = siswa_foto;
    }
}
