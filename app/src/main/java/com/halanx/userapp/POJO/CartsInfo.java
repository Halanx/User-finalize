
package com.halanx.userapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartsInfo {

    @SerializedName("data")
    @Expose
    private byData data;
    @SerializedName("xcash")
    @Expose
    private Double xcash;

    public byData getData() {
        return data;
    }

    public Double getXcash() {
        return xcash;
    }

}


