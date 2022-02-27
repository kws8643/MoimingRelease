package com.example.moimingrelease.network.auth;

import com.example.moimingrelease.network.TransferModel;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AuthRetrofitService {

    @GET("auth/autoLogin")
    Observable<TransferModel<Map<String, Object>>> autoLoginByToken(@Header("JwtToken") String jwtToken);

    @GET("auth/login")
    Call<TransferModel<Map<String, Object>>> loginOrSignupUser(@Header("KakaoAccessToken") String accessToken);

}
