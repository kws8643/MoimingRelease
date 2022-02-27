package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.SessionMembersDTO;
import com.example.moimingrelease.moiming_model.request_dto.USLinkerRequestDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface USLinkerRetrofitService {

    @POST("api/userSessionLinker/receiveAll")
    Observable<TransferModel> usLinkRequest(@Body TransferModel<List<USLinkerRequestDTO>> linkRequestList);

    @GET("api/userSessionLinker/{uuid}")
    Observable<TransferModel<SessionMembersDTO>> requestSessionLinker(@Path("uuid") String sessionUuid);
}
