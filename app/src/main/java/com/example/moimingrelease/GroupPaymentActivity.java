package com.example.moimingrelease;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.AppExtraMethods;
import com.example.moimingrelease.app_adapter.GroupPaymentViewAdapter;
import com.example.moimingrelease.app_listener_interface.PaymentSendFcmListener;
import com.example.moimingrelease.app_listener_interface.PaymentSettingDialogListener;
import com.example.moimingrelease.moiming_model.extras.PaymentViewData;
import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.response_dto.GroupPaymentResponseDTO;
import com.example.moimingrelease.moiming_model.dialog.CreatePaymentDialog;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupPaymentRetrofitService;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.fcm.FCMRequest;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GroupPaymentActivity extends AppCompatActivity {

    public static final String GROUP_PAYMENT_TAG = "GROUP_PAYMENT_TAG";
    /**
     * ???????????? Payment ??? ????????? ?????? ????????????
     * sorting ??? ???, ????????? ??????????????? total?????? ????????? ???????????? ??????
     */
    private Map<UUID, String> memberFcmTokenMap;

    public int calculated = 0;
    private boolean isRefreshing = false;

    private List<GroupPaymentVO> paymentList;
    private List<PaymentViewData> paymentRecyclerList;

    private MoimingUserVO curUser;
    private MoimingGroupVO curGroup;
    private ExtendedFloatingActionButton btnAdd;
    private RecyclerView paymentRecycler;
    private GroupPaymentViewAdapter paymentAdapter;
    private TextView textCurCost;

    CreatePaymentDialog paymentDialog;

    private PaymentSendFcmListener fcmListener = new PaymentSendFcmListener() {
        @Override
        public void sendFcm(String fcmMsg) throws JSONException {

            List<String> fcmTokenList = new ArrayList<>(memberFcmTokenMap.values());

            for (int i = 0; i < fcmTokenList.size(); i++) {

                JSONObject jsonSend = FCMRequest.getInstance()
                        .buildFcmJsonData("group", String.valueOf(2), "?????? ??????", fcmMsg, ""
                                , curGroup.getUuid().toString()
                                , "", fcmTokenList.get(i));

                RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                        , String.valueOf(jsonSend));

                FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);
            }

        }
    };

    PaymentSettingDialogListener paymentDialogListener = new PaymentSettingDialogListener() {

        @Override
        public void touchEdit(GroupPaymentVO paymentData) {

            // ???????????? PaymentListener ??? ??????????????? ??????. Data ????????????!
            paymentDialog.show();

            // 1. set Type
            paymentDialog.setPaymentType(paymentData.getPaymentType());

            // 2. set Date
            paymentDialog.changeSelectedDate(paymentData.getPaymentDate());

            // 3. set Name
            paymentDialog.setPaymentName(paymentData.getPaymentName());

            // 4. set Cost
            paymentDialog.setPaymentCost(paymentData.getPaymentCost());

            // 5. set isUpdating
            paymentDialog.setIsPaymentUpdating(true, paymentData.getUuid().toString());

        }

        @Override
        public void touchDelete(String paymentUuid, String paymentName) {

            // Payment ??? ?????? ???, Refresh ????????? ??????. NotifyData??? ??????!
            deletePayment(paymentUuid, paymentName);

        }
    };

    DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {

            if (paymentDialog.isPaymentAdded()) { // ????????? ?????? ???????????? ?????? ????????????.

                isRefreshing = true;

                calculated = 0;
                paymentList.clear();
                paymentRecyclerList.clear();

                getGroupPaymentHistory();

            }

            paymentDialog = null;

            paymentDialog = new CreatePaymentDialog(GroupPaymentActivity.this, fcmListener, curGroup, curUser);

            paymentDialog.setOnDismissListener(dismissListener);
        }
    };

    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent.getExtras() != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
            curGroup = (MoimingGroupVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));
//            groupUuid = receivedIntent.getExtras().getString("group_uuid");
            memberFcmTokenMap = (Map<UUID, String>) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.fcm_token_map));
        }
    }

    Comparator<GroupPaymentVO> byDate = new Comparator<GroupPaymentVO>() {
        @Override
        public int compare(GroupPaymentVO payment1, GroupPaymentVO payment2) {
            return payment1.getPaymentDate().compareTo(payment2.getPaymentDate());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_payment);

        receiveIntent();

        initView();

        initParams();

        getGroupPaymentHistory();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddPaymentDialog();

            }
        });

        paymentDialog.setOnDismissListener(dismissListener);

    }


    private void initView() {

        btnAdd = findViewById(R.id.btn_add_payment);

        paymentRecycler = findViewById(R.id.payment_recycler);
        textCurCost = findViewById(R.id.text_payment_cur_cost);

    }

    private void initParams() {

        paymentDialog = new CreatePaymentDialog(this, fcmListener, curGroup, curUser);
        paymentList = new ArrayList<>();
        paymentRecyclerList = new ArrayList<>();


    }


    private void getGroupPaymentHistory() { // ?????? UUID ??? ????????? ???????????? ????????????.

        GroupPaymentRetrofitService paymentRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupPaymentRetrofitService.class);
        paymentRetrofit.getGroupPayments(curGroup.getUuid().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<GroupPaymentResponseDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<GroupPaymentResponseDTO>> responseModel) {

                        List<GroupPaymentResponseDTO> responseData = responseModel.getData();

                        for (GroupPaymentResponseDTO dto : responseData) {

                            GroupPaymentVO paymentVO = dto.convertToVO();
                            paymentList.add(paymentVO);

                        }
                        makeRecyclerDataList();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {
                            Log.e(GROUP_PAYMENT_TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        if (!isRefreshing) { // ?????? ???????????? ??????

                            initRecyclerView();

                        } else { // Dialog ??? ?????? ???????????? ??????

                            paymentAdapter.notifyDataSetChanged();
                            isRefreshing = false;
                            GroupActivity.GROUP_INFO_REFRESH_FLAG = true;

                        }


                    }
                });
    }

    private void deletePayment(String paymentUuid, String paymentName) {

        List<String> requestData = new ArrayList<>();
        requestData.add(paymentUuid);
        requestData.add(curUser.getUuid().toString());

        TransferModel<List<String>> requestModel = new TransferModel<>(requestData);

        GroupPaymentRetrofitService retrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupPaymentRetrofitService.class);

        retrofit.deleteGroupPayment(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel responseModel) {

                        Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();

                        if (e.getMessage() != null) {
                            Log.e(GROUP_PAYMENT_TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                        // ??????????????? Refresh ??????.
                        isRefreshing = true;

                        calculated = 0;
                        paymentList.clear();
                        paymentRecyclerList.clear();

                        String fcmMsg = curUser.getUserName() + "?????? " + curGroup.getGroupName() + "??? ?????????????????? "
                                + paymentName + "??? ???????????????";

                        try {
                            sendDeleteFcm(fcmMsg);
                        } catch (JSONException e) {
                            if (e.getMessage() != null) {
                                Log.e("FCM Error:: ", e.getMessage());
                            }
                        }
                        getGroupPaymentHistory();

                    }
                });


    }


    private void sendDeleteFcm(String fcmMsg) throws JSONException {

        List<String> fcmTokenList = new ArrayList<>(memberFcmTokenMap.values());

        for (int i = 0; i < fcmTokenList.size(); i++) {

            JSONObject jsonSend = FCMRequest.getInstance()
                    .buildFcmJsonData("group", String.valueOf(2), "?????? ??????", fcmMsg, ""
                            , curGroup.getUuid().toString()
                            , "", fcmTokenList.get(i));


            RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                    , String.valueOf(jsonSend));

            FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);
        }


    }

    private void makeRecyclerDataList() {

        Collections.sort(paymentList, byDate); // ????????? ???????????? ???????????????,

        for (GroupPaymentVO payment : paymentList) {

            if (payment.getPaymentType()) { // ??????

                calculated += payment.getPaymentCost();

            } else { // ??????

                calculated -= payment.getPaymentCost();

            }

            paymentRecyclerList.add(new PaymentViewData(payment, calculated));

        }

        String modify = AppExtraMethods.moneyToWonWon(calculated) + " ???";
        textCurCost.setText(modify);

        Collections.reverse(paymentRecyclerList);
    }


    private void showAddPaymentDialog() {

        paymentDialog.show();

    }

    private void initRecyclerView() {

        LinearLayoutManager sessionLayoutManager = new LinearLayoutManager(this);

        paymentRecycler.setLayoutManager(sessionLayoutManager);
        paymentAdapter = new GroupPaymentViewAdapter(this, paymentRecyclerList, paymentDialogListener); // ????????? ?????? ????????? ????????????.
        paymentRecycler.setAdapter(paymentAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    LocalDate selectedDate = (LocalDate) data.getSerializableExtra("selected_date");

                    paymentDialog.changeSelectedDate(selectedDate);

                }
            }
        }
    }
}