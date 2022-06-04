package com.example.moimingrelease.network.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.SplashActivity;
import com.example.moimingrelease.moiming_model.request_dto.NotificationRequestDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.NotificationRetrofitService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 안드 Comp. 중 Service 사용 --> Background State 에서도 액션을 받아준다

// 위에 팝업으로 아예 보이진 않음(그냥 와서 끌어 당겼을 때 보이기만 함) --> 해결 필요!

public class FCMReceiveService extends FirebaseMessagingService {

    private boolean isAppNotice;
    private boolean isSessionRequest;
    private boolean isSessionConfirm;
    private boolean isGroupInvite;
    private boolean isGroupPayment;

    public FCMReceiveService() {

        super();

    }

//    UUID toUserUuid, UUID sentUserUuid, String sentActivity
//            , UUID sentGroupUuid, UUID sentSessionUuid, Integer msgType, String msgText

    // TODO: 서버에 공지사항 알림을 저장한다.
    private void saveSystemNotification() {

//        NotificationRequestDTO notiRequest = new NotificationRequestDTO();


//        NotificationRetrofitService notiRetro = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);

//        notiRetro.createSystemNotification()


    }


    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {

            checkFcmSettingInSp();

            boolean isSending = false;

            String activity = remoteMessage.getData().get("activity");
            int type = Integer.parseInt(remoteMessage.getData().get("type"));

            MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true; // 어떤 메시지가 오든 Main Layout 은 다시 형성한다.

            if (activity.equals("system")) { // 공지사항 처리 필요한 경우

                String title = remoteMessage.getData().get("title");
                String text = remoteMessage.getData().get("text");
                String groupUuid = remoteMessage.getData().get("groupUuid");
                String sessionUuid = remoteMessage.getData().get("sessionUuid");

                if (isAppNotice) isSending = true;

                if (isSending) { //
                sendNotification(title, text, groupUuid, sessionUuid);
                }


            } else { // 공지사항 외는 흐르는대로 흘리면 된다.

                String title = remoteMessage.getData().get("title");
                String text = remoteMessage.getData().get("text");
                String groupUuid = remoteMessage.getData().get("groupUuid");
                String sessionUuid = remoteMessage.getData().get("sessionUuid");


                if (activity.equals("group")) {
                    if (type == 0 || type == 1) {
                        if (isGroupInvite) isSending = true;
                    } else if (type == 2) {
                        if (isGroupPayment) isSending = true;
                    }
                } else if (activity.equals("session")) {
                    if (type == 1) {
                        if (isSessionRequest) isSending = true;
                    } else if (type == 2) {
                        if (isSessionConfirm) isSending = true;
                    }
                }

                if (isSending) {
                    sendNotification(title, text, groupUuid, sessionUuid);
                }

            }


        }
    }

    private void checkFcmSettingInSp() {

        SharedPreferences spFcm = getSharedPreferences(SplashActivity.NOTI_SP_NAME, MODE_PRIVATE);

        isAppNotice = spFcm.getBoolean("fcm_app_notice", true);
        isSessionRequest = spFcm.getBoolean("fcm_session_request", true);
        isSessionConfirm = spFcm.getBoolean("fcm_session_confirm", true);
        isGroupInvite = spFcm.getBoolean("fcm_group_invite", true);
        isGroupPayment = spFcm.getBoolean("fcm_group_payment", true);

    }

    // 예시. 팝업 UI 처리하는 곳
    private void sendNotification(String title, String text, String groupUuid, String sessionUuid) {

        Log.w("Notification", title + ": " + text);

        //TODO: Pending Intent 처리 필요, notiBuilder에도 담(클릭시 이동하는 Activity)
        // 앱이 동작중이면 알림을 눌러도 아무 작동을 하지 않음. 꺼져 있는 상태임을 가정.

        // TODO: ERROR: 만약 앱이 아예 종료된 상태라면.. 이거 static 변수 살아 있는걸까?
        //       살아 있어서 Main 이 2번 돌아서 Error 생기는것으로 판단됨. 아니라면 다른 디버깅 필요. ( Splash > Main 으로 가는데 Error 발생)
        //       이걸로 작동 한다. Main 에서 static 변수 살아 있는거 확인했고 이거 제어하면 될듯. How??


        Intent intent = new Intent(getBaseContext(), SplashActivity.class);
        intent.putExtra("is_fcm_clicked", true);
        intent.putExtra(getResources().getString(R.string.main_move_to_group_key), groupUuid);
        intent.putExtra(getResources().getString(R.string.group_move_to_session_key), sessionUuid);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(getBaseContext()
                , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        Intent pendingIntent = new Intent(this, MainActivity.class);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntents = PendingIntent.getActivity(this, 0
                , pendingIntent, PendingIntent.FLAG_ONE_SHOT);*/

        CharSequence name = "moiming";
        String description = "moiming";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("moiming", name, importance);
        channel.setDescription(description);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, "moiming")
                .setSmallIcon(R.drawable.ic_app_back)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setFullScreenIntent(pIntent, true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
//                .setContentIntent(pIntent);

        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notiManager.createNotificationChannel(channel);
        notiManager.notify(0, notiBuilder.build());


        /*NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: Notification ID 지정 필요
        notiManager.notify(0, notiBuilder.build());
*/
    }


    private void requestUserState() {


    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }

}
