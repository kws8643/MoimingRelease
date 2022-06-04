package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.moimingrelease.app_adapter.MyPageAppNoticeAdapter;
import com.example.moimingrelease.app_adapter.NotificationRecyclerAdapter;
import com.example.moimingrelease.moiming_model.dialog.AppProcessDialog;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.AppNoticeVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.AppNoticeResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;
import com.example.moimingrelease.network.AppNoticeRetrofitService;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


//APP NOTICE 를 불러와서, 선택시 해당 URL 을 웹 뷰 해줄 수 있도록 한다.
public class MyPageAppNoticeActivity extends AppCompatActivity {

    private MoimingUserVO curUser;

    private ImageView btnBack;
    private AppProcessDialog processDialog;

    private RecyclerView appNoticeRecycler;
    private MyPageAppNoticeAdapter noticeAdapter;

    private NestedScrollView appNoticeScroll;

    private List<AppNoticeVO> noticeDataRawList;

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_app_notice);

        receiveIntent();

        initView();

        initParams();

        initAppNotice();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

    }

    private void initView() {

        btnBack = findViewById(R.id.btn_back_app_notice);

        appNoticeRecycler = findViewById(R.id.app_notice_recycler);

        appNoticeScroll = findViewById(R.id.app_notice_scroll);
        appNoticeScroll.setNestedScrollingEnabled(true);

    }

    private void initParams() {

        processDialog = new AppProcessDialog(MyPageAppNoticeActivity.this);

        noticeDataRawList = new ArrayList<>();

    }


    private void initAppNotice() {

        processDialog.show();

        AppNoticeRetrofitService noticeRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(AppNoticeRetrofitService.class);

        noticeRetrofit.getAppNotice(curUser.getUuid().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<AppNoticeResponseDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<AppNoticeResponseDTO>> responseModel) {

                        List<AppNoticeResponseDTO> responseDataList = responseModel.getData();

                        for (AppNoticeResponseDTO data : responseDataList) {

                            noticeDataRawList.add(data.convertToVO());

                        }

                        // TODO: 최근 순으로 정렬되도록 변경한다.
                        Collections.sort(noticeDataRawList, byDate);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        processDialog.finish();

                        finish();

                        Log.e("APP_NOTICE_ERROR:: ", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                        processDialog.finish();

                        initRecyclerView();
                    }
                });

    }


    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        appNoticeRecycler.setLayoutManager(linearLayoutManager);

        noticeAdapter = new MyPageAppNoticeAdapter(this, noticeDataRawList);

        appNoticeRecycler.setAdapter(noticeAdapter);
    }

    // 최근 것이 앞쪽으로 온다
    Comparator<AppNoticeVO> byDate = new Comparator<AppNoticeVO>() {
        @Override
        public int compare(AppNoticeVO vo1, AppNoticeVO vo2) {
            return vo2.getCreatedAt().compareTo(vo1.getCreatedAt());
        }
    };

}