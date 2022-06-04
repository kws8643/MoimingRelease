package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.response_dto.AppNoticeResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AppNoticeRetrofitService {

    @GET("api/appNotice/getAppNotice/{userUuid}")
    Observable<TransferModel<List<AppNoticeResponseDTO>>> getAppNotice(@Path("userUuid") String userUuid);

}
