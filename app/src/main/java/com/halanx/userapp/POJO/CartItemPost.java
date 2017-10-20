package com.halanx.userapp.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by samarthgupta on 27/06/17.
 */

public class CartItemPost  {
    @SerializedName("Item")
    @Expose
    private Integer item;

    @SerializedName("Quantity")
    @Expose
    private Double quantity;

    @SerializedName("Notes")
    @Expose
    private String notes;

    @SerializedName("Cart")
    @Expose
    private Integer CartId;


    public CartItemPost(Integer cartId,Double quantity, Integer item, String notes) {
        this.item = item;
        this.quantity = quantity;
        this.notes = notes;
        CartId = cartId;
    }
}