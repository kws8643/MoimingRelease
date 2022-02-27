package com.example.moimingrelease.network.fcm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.moimingrelease.app_listener_interface.FCMCallBack;
import com.example.moimingrelease.network.GlobalRetrofit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//TODO: 일단 매번 불러오는 방식 채택. 나중에 Singleton 도입 필요하면 적용하도록


public class FCMRequest {

    private static FCMRequest INSTANCE = new FCMRequest();

    public FCMRequest(){

    }

    public static FCMRequest getInstance() {

        if (INSTANCE == null) { // 필요 없긴 하지만 혹시 모르니까.
            INSTANCE = new FCMRequest();
        }
        return INSTANCE;
    }

    public JSONObject buildJsonBody(String title, String text, String fcmToken) throws JSONException {

        JSONObject jsonData = new JSONObject();

        jsonData.put("title", title);
        jsonData.put("text", text);

        JSONObject jsonMessage = new JSONObject();

        jsonMessage.put("token", fcmToken);
        jsonMessage.put("data", jsonData);

        JSONObject jsonSend = new JSONObject();
        jsonSend.put("message", jsonMessage);


        return jsonSend;

    }


    public void sendFcmRequest(Context context, RequestBody requestBody) {

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + FCMAppToken.moimingAccessToken)
                .url("https://fcm.googleapis.com/v1/projects/moimingofficial-rel/messages:send/")
                .post(requestBody)
                .build();


        OkHttpClient fcmClient = new OkHttpClient();

        fcmClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 메시지 전송이 실패했을 경우.
                Log.e("Session FCM", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.code() == 401) { // app access token expired

                    FCMAppToken.getFcmAppAccessToken(context, new FCMCallBack() {
                        @Override
                        public void onSuccess() {

                            Request request = new Request.Builder()
                                    .header("Content-Type", "application/json")
                                    .addHeader("Authorization", "Bearer " + FCMAppToken.moimingAccessToken)
                                    .url("https://fcm.googleapis.com/v1/projects/moimingofficial-rel/messages:send/")
                                    .post(requestBody)
                                    .build();

                            fcmClient.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(okhttp3.Call call, IOException e) {

                                    Log.e("Session FCM", "재호출시 error:: " + e.getMessage());

                                }

                                @Override
                                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                                    if (response.isSuccessful()) {
                                        // 전송이 완료됨.
                                        Log.w("Session FCM", "Successfully Sent Message");

                                    } else {
                                        Toast.makeText(context, "통신을 실패하였습니다..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure() {

                            Log.e("Session FCM", "Token Refresh Error");

                        }
                    });

                }

                if (response.isSuccessful()) {

                    Log.w("Session FCM", "Successfully Sent Message (No Refresh)");

                }
            }

        });

    }

}
