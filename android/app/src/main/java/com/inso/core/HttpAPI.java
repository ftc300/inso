package com.inso.core;

import com.inso.entity.http.Information;
import com.inso.entity.http.Product;
import com.inso.entity.http.SignUpResponse;
import com.inso.entity.http.post.Sign;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/24
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public interface HttpAPI {
    @GET("data/information")
    Call<Information> getInformation();

    @GET("product/list")
    Call<Product> getProductList();
//
//    @GET("member/loginout")
//    Call<Product> getLoginOut();
//
//    @GET("member/info")
//    Call<Product> getUserInfo();

    @FormUrlEncoded
    @POST("member/signup")
    Call<SignUpResponse> postSignUp(@Body Sign sign);
}
