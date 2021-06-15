package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.request_dto.MoimingUserRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// Retrofit Interface 에서 메소드 하나하나 = 요청문;

public interface UserRetrofitService {

    // Response Callback으로 전달받는 객체는 원래 <DTO>가 맞는 것
    // 일단 객체 내용이 비슷한 것 같으니 VO로 한다...?
    @POST("user/signup")
    Call<TransferModel<MoimingUserResponseDTO>> userSignupRequest(@Body TransferModel<MoimingUserRequestDTO> requestModel);

}
