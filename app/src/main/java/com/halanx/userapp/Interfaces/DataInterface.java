package com.halanx.userapp.Interfaces;


import com.halanx.userapp.POJO.CartItem;
import com.halanx.userapp.POJO.CartItemPost;
import com.halanx.userapp.POJO.CartsInfo;
import com.halanx.userapp.POJO.GroupItemPost;
import com.halanx.userapp.POJO.OrderInfo;
import com.halanx.userapp.POJO.ProductInfo;
import com.halanx.userapp.POJO.StoreInfo;
import com.halanx.userapp.POJO.SubscriptionInfo;
import com.halanx.userapp.POJO.SubscriptionInfoGet;
import com.halanx.userapp.POJO.UserInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by samarthgupta on 05/04/17.
 */

public interface DataInterface {

    //Register User On Django
    @POST("/users/")
    Call<UserInfo> putUserDataOnServer(@Body UserInfo userData);

    //Get User Cart Items where isRemoved is false
    @GET("/carts/active/")
    Call<List<CartItem>> getUserCartItems(@Header("Authorization") String token);

    @GET("/carts/groupitems/")
    Call<List<CartItem>> getGroupCartItems(@Header("Authorization") String token);


    //Showing products on home fragment
    @GET("/products/")
    Call<List<ProductInfo>> getAllProductsFromServer();

    //Get products according to the store ID
    @GET("/stores/{storeID}/products")
    Call<List<ProductInfo>> getProductsFromStore(@Path("storeID") String storeID);

    //Post cart item when add to cart clicked - while posting item is sent as an integer
    @POST("/carts/items/")
    Call<CartItemPost> putCartItemOnServer(@Body CartItemPost cartItem, @Header("Authorization") String token);

    @POST("/carts/addtogroup/")
    Call<GroupItemPost> putGroupItemOnServer(@Body GroupItemPost cartItem, @Header("Authorization") String token);



    //Get cart details for order total etc
    @GET("/carts/detail/")
    Call<CartsInfo> getCartDetails(@Header("Authorization") String token);

    //Posting an order
    @POST("/orders/")
    Call<OrderInfo> postUserOrder(@Header("Authorization") String token, @Body OrderInfo order);

    //Getting user orders
    @GET("/orders/user/")
    Call<List<OrderInfo>> getUserOrders(@Header("Authorization") String token);

    //Getting store list
    @GET("/stores/verified")
    Call<List<StoreInfo>> getStoreInfo();

    @GET("/stores/{storeID}/")
    Call<List<StoreInfo>> getspecificstore(@Path("storeID") String storeID);


    @GET("/users/detail/")
    Call<UserInfo> getUserInfo(@Header("Authorization") String token);

    @POST("/subscriptions/")
    Call<SubscriptionInfo> postSubscription(@Body SubscriptionInfo info, @Header("Authorization") String token);

    @GET("/subscriptions/items/")
    Call<List<SubscriptionInfoGet>> getUserSubscribedItems(@Header("Authorization") String token);

}