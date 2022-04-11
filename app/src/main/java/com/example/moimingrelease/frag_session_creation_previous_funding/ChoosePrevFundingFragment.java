package com.example.moimingrelease.frag_session_creation_previous_funding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationPreviousFundingActivity;
import com.example.moimingrelease.app_adapter.PreviousFundingViewAdapter;
import com.example.moimingrelease.app_listener_interface.ViewPrevSessionListener;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingSessionResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.SessionRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChoosePrevFundingFragment extends Fragment {

    private SessionCreationPreviousFundingActivity activity;
    private RecyclerView recyclerPrevFunding;
    private PreviousFundingViewAdapter recyclerAdapter;
    private List<MoimingSessionVO> prevFundingDataList;

    private ImageView btnBack;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_prev_funding_choose, container, false);

        initView(view);
        initParams();

        getPrevFundingList();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        return view;
    }

    private void initView(View view) {

        recyclerPrevFunding = view.findViewById(R.id.prev_funding_recycler);
        btnBack = view.findViewById(R.id.btn_back_choose_funding);

    }

    private void initParams() {

        activity = (SessionCreationPreviousFundingActivity) getActivity();

        prevFundingDataList = new ArrayList<>();


    }

    private void getPrevFundingList() {

        SessionRetrofitService sessionRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(SessionRetrofitService.class);
        sessionRetrofit.getGroupFundings(activity.getCurGroup().getUuid().toString()) // 바로 호출 함수 실행
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<MoimingSessionResponseDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<MoimingSessionResponseDTO>> receivedModel) {

                        // TODO: 받아오는 Data 구경 좀 해보자...
                        List<MoimingSessionResponseDTO> dataList = receivedModel.getData();

                        for (MoimingSessionResponseDTO dto : dataList) {

                            prevFundingDataList.add(dto.convertToVO());

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e.getMessage() != null) {
                            Log.e(activity.PREV_FUNDING_LOG, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                        initRecyclerView();

                    }
                });

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        recyclerPrevFunding.setLayoutManager(linearLayoutManager);

        recyclerAdapter = new PreviousFundingViewAdapter(activity.getApplicationContext(), prevFundingDataList, sessionListener);
        recyclerPrevFunding.setAdapter(recyclerAdapter);

    }

    public ViewPrevSessionListener sessionListener = new ViewPrevSessionListener() {
        @Override
        public void moveToViewSession(MoimingSessionVO sessionVO) {

            activity.changeFragments(1, sessionVO);
        }
    };
}
