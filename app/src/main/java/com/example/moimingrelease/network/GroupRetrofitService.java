package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.GroupNoticeDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingGroupAndMembersDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.SessionAndUserStatusDTO;
import com.example.moimingrelease.moiming_model.request_dto.GroupAndSessionCreationDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupEditInfoDto;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GroupRetrofitService {


    /*@POST("/group/create")
    Call<TransferModel<GroupResponseDTO>> groupCreationRequest(@Body TransferModel<GroupCreationRequestDTO> requestModel);
*/

    @POST("api/group/create")
    Observable<TransferModel<MoimingGroupResponseDTO>> groupCreationRequest(@Body TransferModel<MoimingGroupRequestDTO> requestModel);

    @GET("api/group/getGroupMembers/{groupUuid}")
    Observable<TransferModel<List<MoimingMembersDTO>>> getGroupMembersList(@Path("groupUuid") String groupUuid);

    @POST("api/group/getGroupSessions/{groupUuid}/{notificationCheck}")
    Observable<TransferModel<List<SessionAndUserStatusDTO>>> requestGroupSessions(@Path("groupUuid") String groupUuid, @Body String userUuid
            , @Path("notificationCheck") Integer notificationCheck);

    @POST("api/group/createWithSession")
    Observable<TransferModel<MoimingGroupAndMembersDTO>> groupCreationWithSessionRequest(@Body TransferModel<GroupAndSessionCreationDTO> requestModel);

    @POST("api/group/getGroupAndMembers") // GroupActivity 내 Refresh 용
    Observable<TransferModel<MoimingGroupAndMembersDTO>> getGroupAndMembers(@Body String groupUuid);

    @POST("api/group/setNotice") // 그룹 공지 Refresh
    Observable<TransferModel<MoimingGroupResponseDTO>> groupNoticeRequest(@Body TransferModel<GroupNoticeDTO> requestModel);

    @POST("api/group/update")
    Observable<TransferModel<MoimingGroupResponseDTO>> editGroupInfo(@Body TransferModel<MoimingGroupEditInfoDto> requestModel);

}
