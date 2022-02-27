package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingUserRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingUserResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Retrofit Interface 에서 메소드 하나하나 = 요청문;

public interface UserRetrofitService {

    // Response Callback으로 전달받는 객체는 원래 <DTO>가 맞는 것
    // 일단 객체 내용이 비슷한 것 같으니 VO로 한다...?
    @POST("api/user/signup")
    Call<TransferModel<MoimingUserResponseDTO>> userSignupRequest(@Body TransferModel<MoimingUserRequestDTO> requestModel);

    @GET("api/user/{userUuid}")
    Call<TransferModel<MoimingUserResponseDTO>> getCurUser(@Path("userUuid") String userUuid);

    // 카카오 친구들 중 MoimingUser 파싱하는 함수
    @POST("api/user/parseFromKakao")
    Observable<TransferModel<List<MoimingMembersDTO>>> parseMoimingUserFromKakao(@Body TransferModel<List<String>> requestModel);

    @POST("api/user/updateUser")
    Observable<TransferModel<MoimingUserResponseDTO>> userUpdate(@Body TransferModel<MoimingUserRequestDTO> requestModel);
}
