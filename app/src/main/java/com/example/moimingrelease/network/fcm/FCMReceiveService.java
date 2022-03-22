package com.example.moimingrelease.network.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// 안드 Comp. 중 Service 사용 --> Background State 에서도 액션을 받아준다

// 위에 팝업으로 아예 보이진 않음(그냥 와서 끌어 당겼을 때 보이기만 함) --> 해결 필요!

public class FCMReceiveService extends FirebaseMessagingService {

    public FCMReceiveService() {

        super();

    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {

            String title = remoteMessage.getData().get("title");
            String text = remoteMessage.getData().get("text");
            String groupUuid = remoteMessage.getData().get("groupUuid");
            String sessionUuid = remoteMessage.getData().get("sessionUuid");

            MainActivity.IS_MAIN_GROUP_INFO_REFRESH_NEEDED = true; // 어떤 메시지가 오든 Main Layout 은 다시 형성한다.

            sendNotification(title, text, groupUuid, sessionUuid);

        }
    }


    // 예시. 팝업 UI 처리하는 곳
    private void sendNotification(String title, String text, String groupUuid, String sessionUuid) {

        Log.w("Notification", title + ": " + text);

        //TODO: Pending Intent 처리 필요, notiBuilder에도 담(클릭시 이동하는 Activity)
        // 앱이 동작중이면 알림을 눌러도 아무 작동을 하지 않음. 꺼져 있는 상태임을 가정.
        /*
        Intent intent = new Intent(getBaseContext(), ActSplash.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), NotificationHelper.ID_FCM, intent, PendingIntent.FLAG_UPDATE_CURRENT);
*/

        // TODO: 앱이 아예 꺼진건지 ( 눌러도 다시 처음으로 돌아감 ) 아니면 누르면 실행 중이던 곳에서 실행되는지의 여부를 체크 필요.
        // 아예 꺼진거면 Login Check 다시 진행 필요, curUser 데이터 불러와야 함.

        // TODO: if (안꺼짐){ 다시 켜서 원래 있던 상태}
        //       else { 이하
        Intent pendingIntent = new Intent(this, MainActivity.class);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntents = PendingIntent.getActivity(this, 0
                , pendingIntent, PendingIntent.FLAG_ONE_SHOT);

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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntents);

        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notiManager.createNotificationChannel(channel);
        notiManager.notify(0, notiBuilder.build());


        /*NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: Notification ID 지정 필요
        notiManager.notify(0, notiBuilder.build());
*/
    }


    private void requestUserState(){


    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }


    private boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
