package com.ocwvar.video_svg;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/dyjx/api.php")//接口路径
    Call<urlResponse> getVideo(@Query("url") String url);//一个参数 请求的url

}
