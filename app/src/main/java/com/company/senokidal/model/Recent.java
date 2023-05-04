package com.company.senokidal.model;

public class Recent {
    public static final String CREATE_TABLE =
            "CREATE TABLE recent("
                    +" recent_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "recent_tanggal TEXT,"
                    + "recent_tipe TEXT,"
                    + "recent_files TEXT,"
                    + "recent_opsi1 TEXT,"
                    + "recent_opsi2 TEXT"
                    + ")";

    public int recent_id;
    public String recent_tanggal;
    public String recent_tipe;
    public String recent_files;
    public String recent_opsi1;
    public String recent_opsi2;

    public Recent(int recent_id, String recent_tanggal, String recent_tipe, String recent_files,String recent_opsi1, String recent_opsi2) {
        this.recent_id = recent_id;
        this.recent_tanggal = recent_tanggal;
        this.recent_tipe = recent_tipe;
        this.recent_files = recent_files;
        this.recent_opsi1 = recent_opsi1;
        this.recent_opsi2 = recent_opsi2;
    }
}
