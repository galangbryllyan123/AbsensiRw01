package com.company.senokidal.model;

public class Pelajaran {
    public static final String TABLE_NAME = "pelajaran";

    public static final String COLUMN_PELAJARAN_ID = "pelajaran_id";
    public static final String COLUMN_PELAJARAN_NAMA = "pelajaran_nama";
    public static final String COLUMN_PELAJARAN_DESC = "pelajaran_desc";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_PELAJARAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PELAJARAN_NAMA + " TEXT,"
                    + COLUMN_PELAJARAN_DESC + " TEXT"
                    + ")";

    private boolean selected;
    public String pelajaran_nama;
    public String pelajaran_desc;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }


    public String pelajaran_nama() {
        return pelajaran_nama;
    }
    public String pelajaran_desc() {
        return pelajaran_desc;
    }

    public Pelajaran(){}

    public Pelajaran(String pelajaran_nama, String pelajaran_desc){
        this.pelajaran_nama = pelajaran_nama;
        this.pelajaran_desc = pelajaran_desc;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
