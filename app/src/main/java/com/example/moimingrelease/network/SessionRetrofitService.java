package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.SessionStatusChangeDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingSessionRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SessionRetrofitService {

    @POST("api/session/createSession")
    Observable<TransferModel<MoimingSessionResponseDTO>> sessionCreationRequest(@Body TransferModel<MoimingSessionRequestDTO> requestDTO);

    @GET("api/session/getGroupFundings/{groupUuid}")
    Observable<TransferModel<List<MoimingSessionResponseDTO>>> getGroupFundings(@Path("groupUuid") String groupUuid);

    @PUT("api/session/updateSessionStatus")
    Observable<TransferModel<MoimingSessionResponseDTO>> updateSessionStatus(@Body TransferModel<SessionStatusChangeDTO> requestModel);

    @POST("api/session/deleteSession")
    Observable<TransferModel<String>> deleteSession(@Body TransferModel<String> requestModel);


}
