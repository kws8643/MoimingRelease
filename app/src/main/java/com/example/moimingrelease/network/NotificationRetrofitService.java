package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.NotificationUserAndActivityDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.extras.SessionAndUserStatusDTO;
import com.example.moimingrelease.moiming_model.request_dto.NotificationRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.NotificationResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationRetrofitService {

    @POST("api/notification/create")
    Observable<TransferModel<List<String>>> notificationCreateRequest(@Body TransferModel<List<NotificationRequestDTO>> requestModel);

    @POST("api/notification/getAll")
    Observable<TransferModel<List<ReceivedNotificationDTO>>> getUserAllNotification(@Body TransferModel<String> requestModel);

//    @POST("api/notification/craete")
//    Observable<TransferModel<NotificationResponseDTO>> notificationCreateRequest(@Body TransferModel<List<NotificationRequestDTO>> requestModel);

    // 알림 요청은 메인 화면에서 요청하는 All 요청 (메인화면에서는 모두 요청한 다음에 쭉 파싱해줘야 함), 정산활동에서 요청하는 Session 요청 두 개 뿐
    // 공지사항에서는 메인화면에서 가져온거 그냥 파싱해서 보냄. Notification 종류 중 system 에서 온 것들
    // 근데 공지사항을 눌렀을 때는 공지사항으로 이동해야함. Main Activity 에 온 다음에 진행되어야 하기 때문
    // FCM Service --> 1) 서버 통신 가능? (공지사항 때문) -- 2) 서비스 왔을시 메인화면에서 Notification 새로 불러와야함!

    @POST("api/notification/get/{activity}/{msgType}")
    Observable<TransferModel<List<ReceivedNotificationDTO>>> getUserNotification(@Path("activity") String activity, @Path("msgType") String msgType
//            , @Body String userUuid, @Body String activityUuid);
            , @Body NotificationUserAndActivityDTO uuidInfo);


    @POST("api/notification/delete/{activity}/{msgType}")
    Observable<TransferModel> deleteNotification(@Path("activity") String activity, @Path("msgType") String msgType
            , @Body NotificationUserAndActivityDTO uuidInfo);

    // 보내줘야 하는 것.
    // 1. session 이라는 것, 가져오고 싶은 user_uuid, sessionUuid, 무슨 종류 요청인지?

    //받아오는 것 List<NotificationReponseDTO> + 보낸사람 이름 + Activity 이름

}
