package com.example.moimingrelease;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.kakao.sdk.common.KakaoSdk;

public class KakaoLoginApplication extends Application {


    private static KakaoLoginApplication INSTANCE;

    // 어플의 Context(Not Activity)
    public static KakaoLoginApplication getGlobalApplicationContext() {

        if (INSTANCE == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.KakaoLoginApplication");
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(){

        super.onCreate();

        INSTANCE = this;

        FirebaseApp.initializeApp(getGlobalApplicationContext());

        KakaoSdk.init(getGlobalApplicationContext(), getString(R.string.kakao_app_key));

        // 파베 토큰을 만들기 위한 작업. (파베 내 우리 서버 호출)
    }

}
