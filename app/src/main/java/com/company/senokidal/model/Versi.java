package com.company.senokidal.model;

public class Versi {

    public int versi_code;
    public String versi_name;
    public String penambahan;
    public String perbaikan;

    public void Versi(){}
    public void Versi(int versi_code, String versi_name, String penambahan, String perbaikan){
        this.versi_code = versi_code;
        this.versi_name = versi_name;
        this.penambahan = penambahan;
        this.perbaikan = perbaikan;
    }
}
