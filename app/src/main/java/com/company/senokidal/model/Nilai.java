package com.company.senokidal.model;

public class Nilai {
    public static final String TABLE_NAME = "nilai";
    public static final String TABLE_NAME_RESET = "TRUNCATE nilai";


    public static final String COLUMN_NILAI_ID = "nilai_id";
    public static final String COLUMN_KELAS_ID = "kelas_id";
    public static final String COLUMN_SISWA_ID = "siswa_id";
    public static final String COLUMN_NILAI_DESC = "nilai_desc";
    public static final String COLUMN_NILAI = "nilai";
    public static final String COLUMN_NILAI_TANGGAL = "nilai_tanggal";
    public static final String COLUMN_NILAI_TA = "nilai_ta";
    public static final String COLUMN_NILAI_SEMESTER = "nilai_semester";


    private boolean isheader;
    public int nilai_id;
    public int kelas_id;
    public int siswa_id;
    public String nilai_desc;
    public int nilai;
    public String nilai_tanggal;
    public String nilai_ta;
    public String nilai_semester;


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_NILAI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_KELAS_ID + " INTEGER,"
                    + COLUMN_SISWA_ID + " INTEGER,"
                    + COLUMN_NILAI_DESC + " TEXT,"
                    + COLUMN_NILAI + " INTEGER,"
                    + COLUMN_NILAI_TANGGAL + " TEXT,"
                    + COLUMN_NILAI_TA + " TEXT,"
                    + COLUMN_NILAI_SEMESTER + " TEXT"
                    + ")";

    public static final String INSERT_TABLE =
            "INSERT INTO " + TABLE_NAME + "("
                    + COLUMN_NILAI_ID + ","
                    + COLUMN_KELAS_ID + ","
                    + COLUMN_SISWA_ID + ","
                    + COLUMN_NILAI_DESC + ","
                    + COLUMN_NILAI + ","
                    + COLUMN_NILAI_TANGGAL + ","
                    + COLUMN_NILAI_TA + ","
                    + COLUMN_NILAI_SEMESTER + ") VALUES " +
                    "(1,1,1,'',80,'2018-12-01','2019','ganjil')," +
                    "(2,1,2,'',77,'2018-12-01','2019','ganjil')," +
                    "(3,1,3,'',67,'2018-12-01','2019','ganjil')," +
                    "(4,2,4,'',80,'2018-12-01','2019','ganjil')," +
                    "(5,2,5,'',90,'2018-12-02','2019','ganjil')," +
                    "(6,2,6,'',58,'2018-12-02','2019','ganjil')";


    public int get_nilai_id() {
        return nilai_id;
    }
    public int get_kelas_id() {
        return kelas_id;
    }
    public int get_siswa_id() {
        return siswa_id;
    }
    public String get_nilai_desc() {
        return nilai_desc;
    }
    public int get_nilai() {
        return nilai;
    }
    public String get_nilai_tanggal() {
        return nilai_tanggal;
    }

    public void set_nilai_id(int nilai_id) {
        this.nilai_id = nilai_id;
    }
    public void set_kelas_id(int kelas_id) {
        this.kelas_id = kelas_id;
    }
    public void set_siswa_id(int siswa_id) {
        this.siswa_id = siswa_id;
    }
    public void set_nilai_desc(String nilai_desc) {
        this.nilai_desc = nilai_desc;
    }
    public void set_nilai(int nilai) {
        this.nilai = nilai;
    }
    public void set_nilai_tanggal(String nilai_tanggal) {
        this.nilai_tanggal = nilai_tanggal;
    }


    public boolean isHeader() {
        return isheader;
    }

    public Nilai(String nilai_tanggal){
        this.nilai_tanggal = nilai_tanggal;
        this.isheader = true;
    }
    public Nilai(String nilai_tanggal, int nilai_id,int kelas_id,int siswa_id){
        this.nilai_id = nilai_id;
        this.kelas_id = kelas_id;
        this.siswa_id = siswa_id;
        this.nilai_tanggal = nilai_tanggal;
        this.isheader = false;
    }
    public Nilai(String nilai_tanggal, int nilai_id,int kelas_id,int siswa_id, String nilai_desc){
        this.nilai_id = nilai_id;
        this.kelas_id = kelas_id;
        this.siswa_id = siswa_id;
        this.nilai_desc = nilai_desc;
        this.nilai_tanggal = nilai_tanggal;
        this.isheader = false;
    }
    public Nilai(String nilai_tanggal, int nilai_id, int kelas_id, int siswa_id, String nilai_desc, int nilai){
        this.nilai_id = nilai_id;
        this.kelas_id = kelas_id;
        this.siswa_id = siswa_id;
        this.nilai_desc = nilai_desc;
        this.nilai = nilai;
        this.nilai_tanggal = nilai_tanggal;
        this.isheader = false;
    }
}
