package com.company.senokidal.model;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class RuangBelajar implements Comparable<RuangBelajar>{
    public String kelas_nama;
    public String kelas_desc;

    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public RuangBelajar(){}
    public RuangBelajar(String kelas_nama, String kelas_desc){
        this.kelas_nama = kelas_nama;
        this.kelas_desc = kelas_desc;
    }

    @Override
    public int compareTo(RuangBelajar o) {
        return (this.kelas_desc != null && this.kelas_desc.equals(o.kelas_desc)) ? -1 : 1;
    }
}
