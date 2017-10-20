package com.halanx.userapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nishant on 12/10/17.
 */

public class AddedBy {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private user user;


    public Integer getId() {
        return id;
    }


    public user getUser() {
        return user;
    }


}
