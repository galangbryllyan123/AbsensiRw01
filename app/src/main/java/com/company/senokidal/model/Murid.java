package com.company.senokidal.model;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class Murid implements Comparable<Murid>{
    public String kelas_key;
    public String siswa_nis;
    public String siswa_nama;
    public String siswa_jk;
    public String siswa_foto;
    public String siswa_ta;
    public String siswa_semester;

    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Murid(){}
    public Murid(String siswa_nis, String siswa_nama, String siswa_jk, String siswa_foto){
        this.siswa_nis = siswa_nis;
        this.siswa_nama = siswa_nama;
        this.siswa_jk = siswa_jk;
        this.siswa_foto = siswa_foto;
    }
    public Murid(String kelas_key, String siswa_nis, String siswa_nama, String siswa_jk, String siswa_foto){
        this.kelas_key = kelas_key;
        this.siswa_nis = siswa_nis;
        this.siswa_nama = siswa_nama;
        this.siswa_jk = siswa_jk;
        this.siswa_foto = siswa_foto;
    }

    @Override
    public int compareTo(Murid o) {
        return (this.siswa_nama != null && this.siswa_nama.equals(o.siswa_nama)) ? -1 : 1;
    }
}
