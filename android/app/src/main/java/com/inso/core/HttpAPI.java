package com.inso.core;

import com.inso.entity.http.Information;
import com.inso.entity.http.Product;
import com.inso.entity.http.SignUpResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

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

    @GET("member/loginout")
    Call<Product> getLoginOut();

    @GET("member/info")
    Call<Product> getUserInfo();

    @POST("member/signup")
    Call<SignUpResponse> postSignUp(@QueryMap Map<String , String> params);
}
