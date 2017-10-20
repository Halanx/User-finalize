package com.halanx.userapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class user{
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("last_name")
    @Expose
    private String last_name;

    public Integer getId() {
        return id;
    }


    public String getFirst_name() {
        return first_name;
    }


    public String getLast_name () {
        return last_name;
    }


}
