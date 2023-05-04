package com.company.senokidal.model;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class MataPelajaran implements Comparable<MataPelajaran>{
    public String pelajaran_nama;
    public String pelajaran_desc;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public MataPelajaran(){}

    public MataPelajaran(String pelajaran_nama, String pelajaran_desc){
        this.pelajaran_nama = pelajaran_nama;
        this.pelajaran_desc = pelajaran_desc;
    }

    @Override
    public int compareTo(MataPelajaran o) {
        return (this.pelajaran_desc != null && this.pelajaran_desc.equals(o.pelajaran_desc)) ? -1 : 1;
    }
}
