package com.example.moimingrelease.app_handler;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.moimingrelease.MoimingErrorActivity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;

public class MoimingErrorHandler implements Thread.UncaughtExceptionHandler {

    private Activity lastActivity;
    private int activityCount;
//    private final UncaughtExceptionHandler exceptionHandler;

    //  Kotlin 때문인지 UncaughtExceptionHandler 역할 없음으로 제거시도.
    //    public MoimingErrorHandler(@NotNull Application application, @NotNull UncaughtExceptionHandler exceptionHandler){
    public MoimingErrorHandler(@NotNull Application application) {

//        this.exceptionHandler = exceptionHandler;
        application.registerActivityLifecycleCallbacks((Application.ActivityLifecycleCallbacks) (new ApplicationLifecyclerCallbacks() {

            public void onActivityCreated(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {

                Intrinsics.checkNotNullParameter(activity, "activity");
                if (!MoimingErrorHandler.this.isSkipActivity(activity)) {
                    MoimingErrorHandler.this.lastActivity = activity;
                }
            }

            public void onActivityStarted(@NotNull Activity activity) {
                Intrinsics.checkNotNullParameter(activity, "activity");
                if (!MoimingErrorHandler.this.isSkipActivity(activity)) {
                    MoimingErrorHandler thisHandler = MoimingErrorHandler.this;
                    thisHandler.activityCount = thisHandler.activityCount + 1;
                    MoimingErrorHandler.this.lastActivity = activity;
                }
            }

            public void onActivityStopped(@NotNull Activity activity) {
                Intrinsics.checkNotNullParameter(activity, "activity");
                if (!MoimingErrorHandler.this.isSkipActivity(activity)) {
                    MoimingErrorHandler thisHandler = MoimingErrorHandler.this;
                    thisHandler.activityCount = thisHandler.activityCount + -1;
                    if (MoimingErrorHandler.this.activityCount < 0) {
                        MoimingErrorHandler.this.lastActivity = (Activity) null;
                    }

                }
            }
        }));
    }


    @Override
    public void uncaughtException(@NonNull @NotNull Thread thread, @NonNull @NotNull Throwable error) {

        Activity curActivity = this.lastActivity;
        if (curActivity != null) {

            // 발생한 에러를 기록하여 전달한다
            StringWriter stringWriter = new StringWriter();
            error.printStackTrace(new PrintWriter((Writer) stringWriter));

            String strError = stringWriter.toString();

            this.startErrorActivity(curActivity, strError);
        }

        System.exit(-1);
    }


    private final void startErrorActivity(Activity activity, String errorText) {

        Intent errorIntent = new Intent((Context) activity, MoimingErrorActivity.class);

        errorIntent.putExtra("EXTRA_INTENT", (Parcelable) activity.getIntent());
        errorIntent.putExtra("EXTRA_ERROR_TEXT", errorText);
        errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(errorIntent);
        activity.finish();

    }

    private final boolean isSkipActivity(Activity activity) {
        return activity instanceof MoimingErrorActivity;
    }

}
