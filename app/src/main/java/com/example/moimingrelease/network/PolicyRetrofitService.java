package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.UserAndPolicyAgreeDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PolicyRetrofitService {

    @POST("api/policy/linkWithUser")
    Call<TransferModel<String>> createAgreedPolicy(@Body TransferModel<UserAndPolicyAgreeDTO> requestModel);

}
