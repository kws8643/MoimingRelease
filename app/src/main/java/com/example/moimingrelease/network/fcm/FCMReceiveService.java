package com.example.moimingrelease.network.fcm;

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

            String title = remoteMessage.getData().get("title").toString();
            String text = remoteMessage.getData().get("text").toString();

            MainActivity.IS_NOTIFICATION_REFRESH_NEEDED = true;

            sendNotification(title, text);
        }
    }

    // 예시. 팝업 UI 처리하는 곳
    private void sendNotification(String title, String text) {

        Log.w("Notification", title + ": " + text);
        //TODO: Pending Intent 처리 필요, notiBuilder에도 담(클릭시 이동하는 Activity)
        Intent pendingIntent = new Intent(this, MainActivity.class);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntents = PendingIntent.getActivity(this, 0
                , pendingIntent, PendingIntent.FLAG_ONE_SHOT);

        CharSequence name = "moiming";
        String description = "moiming";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("moiming", name, importance);
        channel.setDescription(description);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this,"moiming")
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

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }
}
