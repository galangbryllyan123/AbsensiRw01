package com.company.senokidal.model;

public class Akumulasi  implements Comparable<Akumulasi>{
    public String tanggal;
    public long tanggal_miliseconds;
    public String status;
    public String catatan;

    public void Akumulasi(){}
    public Akumulasi(String tanggal, long tanggal_miliseconds, String status, String catatan){
        this.tanggal = tanggal;
        this.tanggal_miliseconds = tanggal_miliseconds;
        this.status = status;
        this.catatan = catatan;
    }

    @Override
    public int compareTo(Akumulasi o) {
        return this.tanggal_miliseconds > o.tanggal_miliseconds? -1 : 1;
    }
}
