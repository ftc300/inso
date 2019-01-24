package com.inso.core;

import com.inso.entity.http.Information;
import com.inso.entity.http.Product;

import retrofit2.Call;
import retrofit2.http.GET;

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
}
