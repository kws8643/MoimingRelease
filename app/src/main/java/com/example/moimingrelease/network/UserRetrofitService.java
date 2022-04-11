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

public interface UserRetrofitService {

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
