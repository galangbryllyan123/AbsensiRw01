package com.company.senokidal.model;


public class Profile {
    public String foto;
    public String asal_sekolah;
    public String semester;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Profile(){}

    public Profile(String foto){
        this.foto = foto;
    }

    public Profile(String foto,String asal_sekolah){
        this.foto = foto;
        this.asal_sekolah = asal_sekolah;
    }

    public Profile(String foto, String asal_sekolah, String semester){
        this.foto = foto;
        this.asal_sekolah = asal_sekolah;
        this.semester = semester;
    }
}
