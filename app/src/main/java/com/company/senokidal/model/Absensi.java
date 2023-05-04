package com.company.senokidal.model;

public class Absensi {
    public static final String TABLE_NAME = "absensi";
    public static final String TABLE_NAME_RESET = "TRUNCATE absensi";


    public static final String COLUMN_ABSENSI_ID = "absensi_id";
    public static final String COLUMN_KELAS_ID = "kelas_id";
    public static final String COLUMN_SISWA_ID = "siswa_id";
    public static final String COLUMN_ABSENSI_HADIR = "absensi_hadir";
    public static final String COLUMN_ABSENSI_ALFA = "absensi_alfa";
    public static final String COLUMN_ABSENSI_IZIN = "absensi_izin";
    public static final String COLUMN_ABSENSI_SAKIT = "absensi_sakit";
    public static final String COLUMN_ABSENSI_TERLAMBAT = "absensi_terlambat";
    public static final String COLUMN_ABSENSI_TANGGAL = "absensi_tanggal";
    public static final String COLUMN_ABSENSI_JAM = "absensi_jam";
    public static final String COLUMN_ABSENSI_PELAJARAN = "absensi_pelajaran";
    public static final String COLUMN_ABSENSI_TA = "absensi_ta";
    public static final String COLUMN_ABSENSI_SEMESTER = "absensi_semester";




    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ABSENSI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_KELAS_ID + " INTEGER,"
                    + COLUMN_SISWA_ID + " INTEGER,"
                    + COLUMN_ABSENSI_HADIR + " INTEGER,"
                    + COLUMN_ABSENSI_ALFA + " INTEGER,"
                    + COLUMN_ABSENSI_IZIN + " INTEGER,"
                    + COLUMN_ABSENSI_SAKIT + " INTEGER,"
                    + COLUMN_ABSENSI_TERLAMBAT + " INTEGER,"
                    + COLUMN_ABSENSI_TANGGAL + " TEXT,"
                    + COLUMN_ABSENSI_JAM + " TEXT,"
                    + COLUMN_ABSENSI_PELAJARAN + " TEXT,"
                    + COLUMN_ABSENSI_TA + " TEXT,"
                    + COLUMN_ABSENSI_SEMESTER + " TEXT"
                    + ")";



    public static final String INSERT_TABLE =
            "INSERT INTO " + TABLE_NAME + "("
                    + COLUMN_ABSENSI_ID + ","
                    + COLUMN_KELAS_ID + ","
                    + COLUMN_SISWA_ID + ","
                    + COLUMN_ABSENSI_HADIR + ","
                    + COLUMN_ABSENSI_ALFA + ","
                    + COLUMN_ABSENSI_IZIN + ","
                    + COLUMN_ABSENSI_SAKIT + ","
                    + COLUMN_ABSENSI_TERLAMBAT + ","
                    + COLUMN_ABSENSI_TANGGAL + ","
                    + COLUMN_ABSENSI_JAM + ","
                    + COLUMN_ABSENSI_PELAJARAN + ","
                    + COLUMN_ABSENSI_TA + ","
                    + COLUMN_ABSENSI_SEMESTER + ") VALUES " +
                    "(1,1,1,1,0,0,0,0,'2018-12-01','00:00','2019','ganjil')," +
                    "(2,1,2,1,0,0,0,0,'2018-12-01','00:00','2019','ganjil')," +
                    "(3,1,3,0,0,0,0,1,'2018-12-01','00:00','2019','ganjil')," +
                    "(4,2,4,1,0,0,0,0,'2018-12-01','00:00','2019','ganjil')," +
                    "(5,2,5,1,0,0,0,0,'2018-12-02','00:00','2019','ganjil')," +
                    "(6,2,6,0,1,0,0,0,'2018-12-02','00:00','2019','ganjil')";

    private boolean isheader;

    public int absensi_id;
    public String kelas_key;
    public String siswa_key;
    public String pelajaran_key;
    public String absensi_tanggal;
    public String absensi_status;
    public String absensi_ta;
    public String absensi_semester;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }


    public boolean isHeader() {
        return isheader;
    }

    public Absensi(){}

    public Absensi(String absensi_tanggal){
        this.absensi_tanggal = absensi_tanggal;
        this.isheader = true;
    }

    public Absensi(String absensi_tanggal,
                   String kelas_key,
                   String siswa_key,
                   String pelajaran_key,
                   String absensi_status,
                   String absensi_ta,
                   String absensi_semester){

        this.kelas_key = kelas_key;
        this.siswa_key = siswa_key;
        this.pelajaran_key = pelajaran_key;
        this.absensi_status = absensi_status;
        this.absensi_tanggal = absensi_tanggal;
        this.absensi_ta = absensi_ta;
        this.absensi_semester = absensi_semester;
        this.isheader = false;
    }


    public int kelas_id;
    public int siswa_id;
    public String absensi_jam;
    public String absensi_pelajaran;
    public int absensi_hadir;
    public int absensi_alfa;
    public int absensi_izin;
    public int absensi_sakit;
    public int absensi_terlambat;

    public Absensi(String absensi_tanggal, String absensi_jam, String absensi_pelajaran, int kelas_id,int siswa_id, int absensi_hadir, int absensi_alfa, int absensi_izin, int absensi_sakit, int absensi_terlambat){
        this.kelas_id = kelas_id;
        this.siswa_id = siswa_id;
        this.absensi_hadir = absensi_hadir;
        this.absensi_alfa = absensi_alfa;
        this.absensi_izin = absensi_izin;
        this.absensi_sakit = absensi_sakit;
        this.absensi_terlambat = absensi_terlambat;
        this.absensi_tanggal = absensi_tanggal;
        this.absensi_jam = absensi_jam;
        this.absensi_pelajaran = absensi_pelajaran;
        this.isheader = false;
    }

    public Absensi(String absensi_tanggal, String absensi_jam, String absensi_pelajaran, int absensi_id,int kelas_id,int siswa_id, int absensi_hadir, int absensi_alfa, int absensi_izin, int absensi_sakit, int absensi_terlambat){
        this.absensi_id = absensi_id;
        this.kelas_id = kelas_id;
        this.siswa_id = siswa_id;
        this.absensi_hadir = absensi_hadir;
        this.absensi_alfa = absensi_alfa;
        this.absensi_izin = absensi_izin;
        this.absensi_sakit = absensi_sakit;
        this.absensi_terlambat = absensi_terlambat;
        this.absensi_tanggal = absensi_tanggal;
        this.absensi_jam = absensi_jam;
        this.absensi_pelajaran = absensi_pelajaran;
        this.isheader = false;
    }
}
