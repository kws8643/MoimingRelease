package com.example.moimingrelease.network.fcm;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.moimingrelease.app_listener_interface.FCMCallBack;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FCMAppToken {

    // Messaging 앱 토큰 발급 용
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = {MESSAGING_SCOPE};

    public static String moimingAccessToken;


    // FCM용 Moiming Appd의 등록 App Token 을 새로 발급 받는 곳 (3600sec / refresh 무관)
    public static void getFcmAppAccessToken(Context context, FCMCallBack fcmCallBack) throws IOException { // 비동기 적용이 안되는 상태임.

        GetMoimingAccessToken getToken = new GetMoimingAccessToken(context, fcmCallBack);

        getToken.execute();
    }

    static class GetMoimingAccessToken extends AsyncTask<String, String, String> {

        private Context context;
        private FCMCallBack fcmCallBack;

        public GetMoimingAccessToken(Context context, FCMCallBack fcmCallBack) {

            this.context = context;
            this.fcmCallBack = fcmCallBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String accessToken) {
            super.onPostExecute(accessToken);

            if (accessToken != null) {
                moimingAccessToken = accessToken;
                fcmCallBack.onSuccess();

            } else {
                Toast.makeText(context.getApplicationContext(), "토큰을 받아오지 못헀습니다", Toast.LENGTH_SHORT).show();
                fcmCallBack.onFailure();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            AssetManager am = context.getResources().getAssets();
            InputStream inputStream = null;

            try {

                inputStream = am.open("service-account.json");

                GoogleCredentials googleCredentials = GoogleCredentials
                        .fromStream(inputStream)
                        .createScoped(Arrays.asList(SCOPES));

                googleCredentials.refresh();

                AccessToken at = googleCredentials.getAccessToken();
                String value = at.getTokenValue();

                return value;

            } catch (IOException e) {

                Log.w("ACC_TOKEN", e.getMessage());
                return null;
            }

        }
    }
}
