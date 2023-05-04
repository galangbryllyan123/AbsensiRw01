package com.company.senokidal.model;

import java.util.ArrayList;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class SikapPrilaku implements Comparable<SikapPrilaku>{

    public String kelas_key;

    public String sikapprilaku_tanggal;
    public String sikapprilaku_ket;
    public String sikapprilaku_ta;
    public String sikapprilaku_semester;
    public ArrayList<Status> sikapprilaku_body;


    public String key;

    public void setKey(String key) {
        this.key = key;
    }


    public SikapPrilaku(){}

    public SikapPrilaku(String sikapprilaku_tanggal){
        this.sikapprilaku_tanggal = sikapprilaku_tanggal;
    }

    public SikapPrilaku(String sikapprilaku_tanggal,
                        String kelas_key,
                        String sikapprilaku_ket,
                        ArrayList<Status> sikapprilaku_body,
                        String sikapprilaku_ta,
                        String sikapprilaku_semester){

        this.kelas_key = kelas_key;

        this.sikapprilaku_tanggal = sikapprilaku_tanggal;
        this.sikapprilaku_ket = sikapprilaku_ket;
        this.sikapprilaku_body = sikapprilaku_body;
        this.sikapprilaku_ta = sikapprilaku_ta;
        this.sikapprilaku_semester = sikapprilaku_semester;
    }

    @Override
    public int compareTo(SikapPrilaku o) {
        return (this.sikapprilaku_tanggal != null && this.sikapprilaku_tanggal.equals(o.sikapprilaku_tanggal)) ? -1 : 1;
    }
}
