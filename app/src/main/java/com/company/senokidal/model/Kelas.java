package com.company.senokidal.model;

public class Kelas {
    public static final String TABLE_NAME = "kelas";
    public static final String TABLE_NAME_RESET = "TRUNCATE kelas";


    public static final String COLUMN_KELAS_ID = "kelas_id";
    public static final String COLUMN_KELAS_NAMA = "kelas_nama";
    public static final String COLUMN_KELAS_DESC = "kelas_desc";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_KELAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_KELAS_NAMA + " TEXT,"
                    + COLUMN_KELAS_DESC + " TEXT"
                    + ")";

    public static final String INSERT_TABLE =
            "INSERT INTO " + TABLE_NAME + "("
                    + COLUMN_KELAS_ID + ","
                    + COLUMN_KELAS_NAMA + ","
                    + COLUMN_KELAS_DESC + ") VALUES " +
                    "(1,'XTKJ1','')" +
                    "(2,'XTKJ2','')" +
                    "(3,'XTKJ3','')" +
                    "(4,'XITKJ1','')" +
                    "(5,'XITKJ2','')" +
                    "(6,'XIITKJ1','')" +
                    "(7,'XIITKJ2','');";



    private boolean selected;
    public int kelas_id;
    public String kelas_nama;
    public String kelas_desc;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Kelas(){}
    public Kelas(String kelas_nama, String kelas_desc){
        this.kelas_nama = kelas_nama;
        this.kelas_desc = kelas_desc;
    }
    public Kelas(int kelas_id, String kelas_nama, String kelas_desc){
        this.kelas_id = kelas_id;
        this.kelas_nama = kelas_nama;
        this.kelas_desc = kelas_desc;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
