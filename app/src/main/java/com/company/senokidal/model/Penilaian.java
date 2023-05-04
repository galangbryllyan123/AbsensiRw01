package com.company.senokidal.model;

import java.util.ArrayList;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class Penilaian implements Comparable<Penilaian>{

    public String kelas_key;
    public String pelajaran_key;

    public String penilaian_tanggal;
    public String penilaian_ket;
    public String penilaian_ta;
    public String penilaian_semester;
    public ArrayList<Status> penilaian_body;


    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Penilaian(){}

    public Penilaian(String penilaian_tanggal){
        this.penilaian_tanggal = penilaian_tanggal;
    }

    public Penilaian(String penilaian_tanggal,
                     String kelas_key,
                     String pelajaran_key,
                     String penilaian_ket,
                     ArrayList<Status> penilaian_body,
                     String penilaian_ta,
                     String penilaian_semester){

        this.kelas_key = kelas_key;
        this.pelajaran_key = pelajaran_key;

        this.penilaian_tanggal = penilaian_tanggal;
        this.penilaian_ket = penilaian_ket;
        this.penilaian_body = penilaian_body;
        this.penilaian_ta = penilaian_ta;
        this.penilaian_semester = penilaian_semester;
    }

    @Override
    public int compareTo(Penilaian o) {
        return (this.penilaian_tanggal != null && this.penilaian_tanggal.equals(o.penilaian_tanggal)) ? -1 : 1;
    }
}
