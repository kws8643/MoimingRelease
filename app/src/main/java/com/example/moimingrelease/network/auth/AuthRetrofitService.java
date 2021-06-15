package com.example.moimingrelease.network.auth;

import com.example.moimingrelease.network.TransferModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AuthRetrofitService {

    @GET("auth/token")
    Call<TransferModel<Map<String, Object>>> confirmKakaoToken(@Header("KakaoAccessToken")String accessToken);

}
