package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupCreationRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupRetrofitService {


    /*@POST("/group/create")
    Call<TransferModel<GroupResponseDTO>> groupCreationRequest(@Body TransferModel<GroupCreationRequestDTO> requestModel);
*/

    @POST("group/create")
    Observable<TransferModel<MoimingGroupResponseDTO>> groupCreationRequest(@Body TransferModel<MoimingGroupCreationRequestDTO> requestModel);



}
