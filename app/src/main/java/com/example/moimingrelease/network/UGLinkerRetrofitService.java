package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.UserGroupUuidDTO;
import com.example.moimingrelease.moiming_model.request_dto.UGLinkerRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.UGLinkerResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UGLinkerRetrofitService {

    // 유저가 그룹을 생성할 때 그룹과 유저를 이어준다.
    @POST("api/userGroupLinker/link")
    Observable<TransferModel> ugLinkRequest(@Body TransferModel<UGLinkerRequestDTO> requestModel);

    // 홈 화면에서 UGLinker 를 통해 유저가 연결된 그룹들을 반영한다.
    @GET("api/userGroupLinker/{uuid}")
    Observable<TransferModel<List<UGLinkerResponseDTO>>> getUgLinkers(@Path("uuid") String userUuid);

    @POST("api/userGroupLinker/unlink")
    Observable<TransferModel> deleteGroupFromUser(@Body TransferModel<UserGroupUuidDTO> requestModel);

    @POST("api/userGroupLinker/link")
    Observable<TransferModel<List<String>>> addMembersToGroupRequest(@Body TransferModel<List<UserGroupUuidDTO>> requestModel);

}
