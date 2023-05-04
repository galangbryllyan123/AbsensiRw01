package com.company.senokidal.model;

import java.util.ArrayList;

import androidx.annotation.Keep;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class Kehadiran implements Comparable<Kehadiran>{

    public String kelas_key;
    public String pelajaran_key;

    public String kehadiran_tanggal;
    public long kehadiran_jam;
    public String kehadiran_ta;
    public String kehadiran_semester;
    public ArrayList<Status> kehadiran_body;

    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Kehadiran(){}

    public Kehadiran(String kehadiran_tanggal){
        this.kehadiran_tanggal = kehadiran_tanggal;
    }

    public Kehadiran(String kehadiran_tanggal,
                     long kehadiran_jam,
                     String kelas_key,
                     String pelajaran_key,
                     ArrayList<Status> kehadiran_body,
                     String kehadiran_ta,
                     String kehadiran_semester){

        this.kelas_key = kelas_key;
        this.pelajaran_key = pelajaran_key;

        this.kehadiran_tanggal = kehadiran_tanggal;
        this.kehadiran_jam = kehadiran_jam;
        this.kehadiran_body = kehadiran_body;
        this.kehadiran_ta = kehadiran_ta;
        this.kehadiran_semester = kehadiran_semester;
    }


    @Override
    public int compareTo(Kehadiran o) {
        return this.kehadiran_jam < o.kehadiran_jam? -1 : 1;
    }
}
