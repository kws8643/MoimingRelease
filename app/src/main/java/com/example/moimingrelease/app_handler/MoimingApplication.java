package com.example.moimingrelease.app_handler;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.moimingrelease.R;
import com.google.firebase.FirebaseApp;
import com.kakao.sdk.common.KakaoSdk;

import org.jetbrains.annotations.NotNull;

public class MoimingApplication extends Application {


    private static MoimingApplication INSTANCE;

    // 어플의 Context(Not Activity)
    public static MoimingApplication getGlobalApplicationContext() {

        if (INSTANCE == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.KakaoLoginApplication");
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(){

        super.onCreate();

        INSTANCE = this;

        // 파베 토큰을 만들기 위한 작업. (파베 내 우리 서버 호출)
        FirebaseApp.initializeApp(getGlobalApplicationContext());

        KakaoSdk.init(getGlobalApplicationContext(), getString(R.string.kakao_app_key));

        Thread.setDefaultUncaughtExceptionHandler(new MoimingErrorHandler(this));

        // Crash Report
//        setCrashHandler();



    }

    private final void setCrashHandler() {

        if (Thread.getDefaultUncaughtExceptionHandler() == null) {

            Thread.setDefaultUncaughtExceptionHandler(
                    new MoimingErrorHandler(this));
        }
    }

}
