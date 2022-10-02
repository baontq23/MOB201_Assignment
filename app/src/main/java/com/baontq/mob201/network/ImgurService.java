package com.baontq.mob201.network;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImgurService {

    @GET("image/{id}")
    @Headers("Authorization: Client-ID 481d675ae249af2")
    Call<JsonObject> getImage(@Path("id") String id);

    @Multipart
    @POST("upload")
    @Headers("Authorization: Client-ID 481d675ae249af2")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part image);
}
