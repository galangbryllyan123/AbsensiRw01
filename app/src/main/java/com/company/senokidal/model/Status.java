package com.company.senokidal.model;


import java.io.Serializable;

import androidx.annotation.Keep;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class Status implements Serializable {
    public String k;
    public String v;

    public Status(){}

    public Status(String k, String v){
        this.k = k;
        this.v = v;
    }

}
