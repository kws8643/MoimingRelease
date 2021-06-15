package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.request_dto.UGLinkerCreationRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.UGLinkerResponeDTO;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UGLinkerRetrofitService {

    @POST("userGroupLinker/link")
    Observable<TransferModel> ugLinkRequest(@Body TransferModel<UGLinkerCreationRequestDTO> requestModel);

}
