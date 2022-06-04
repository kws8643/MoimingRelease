package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.example.moimingrelease.GroupPaymentActivity;
import com.example.moimingrelease.GroupPaymentDateSelectActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_adapter.AppExtraMethods;
import com.example.moimingrelease.app_listener_interface.PaymentSendFcmListener;
import com.example.moimingrelease.moiming_model.extras.PaymentAndSenderDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.request_dto.GroupPaymentRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.GroupPaymentResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.GroupPaymentRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreatePaymentDialog extends Dialog {

    private Context context;

    private MoimingUserVO curUser;
    private MoimingGroupVO curGroup;

    private ConstraintLayout btnSelectDate;
    private EditText inputName, inputCost;
    private TextView btnCancel, btnCreate; // Button 역할
    private TextView typeExpense, typeIncome; // Radio Button 역할
    private TextView textDate;

    private boolean type = false; // Expense = false, Income = true

    private LocalDate paymentDate = LocalDate.now();
    private boolean isPaymentAdded = false; // 통신 결과상 payment 가 added 되었는가?

    private PaymentSendFcmListener fcmListener;

    private String moneyResult = "";

    // Update 중일때 필요한 변수들.
    private boolean isPaymentUpdating = false;
    private String updatingPaymentUuid;

    public boolean isPaymentAdded() {

        return this.isPaymentAdded;

    }

    public boolean isPaymentUpdating() {

        return this.isPaymentUpdating;
    }


    public CreatePaymentDialog(@NonNull Context context, PaymentSendFcmListener fcmListener, MoimingGroupVO curGroup, MoimingUserVO curUser) {

        super(context);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.context = context;
        this.fcmListener = fcmListener;
        this.curGroup = curGroup;
        this.curUser = curUser;

    }

    private TextWatcher moneyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(moneyResult)) {

                moneyResult = AppExtraMethods.wonFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                inputCost.setText(moneyResult);
                inputCost.setSelection(moneyResult.length());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_payment_add);

        initView();

        inputCost.addTextChangedListener(moneyWatcher);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPayment();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        typeIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = true;
                typeIncome.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_selected, null));
                typeIncome.setTextColor(context.getResources().getColor(R.color.moimingWhite, null));

                typeExpense.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_normal, null));
                typeExpense.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));

            }
        });

        typeExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = false;
                typeExpense.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_selected, null));
                typeExpense.setTextColor(context.getResources().getColor(R.color.moimingWhite, null));

                typeIncome.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_normal, null));
                typeIncome.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent selectDateIntent = new Intent(context, GroupPaymentDateSelectActivity.class);

                selectDateIntent.putExtra("cur_date", paymentDate);

                ((GroupPaymentActivity) context).startActivityForResult(selectDateIntent, 100);

            }
        });
    }

    private void initView() {

        inputName = findViewById(R.id.input_payment_name);
        inputCost = findViewById(R.id.input_payment_cost);

        btnCancel = findViewById(R.id.btn_cancel);
        btnCreate = findViewById(R.id.btn_create_payment);

        typeExpense = findViewById(R.id.type_expense);
        typeIncome = findViewById(R.id.type_income);

        textDate = findViewById(R.id.text_payment_date);
        btnSelectDate = findViewById(R.id.btn_select_payment_date);
        textDate.setText(transferDateToText()); // 현재 날짜로 일단 구현되게끔 해놓는다
        // + 현재 날짜가 LocalDateTime 이도록

    }

    private String getDow(int dayValue) {

        String textDow = "";

        switch (dayValue) {
            case 1:
                textDow = "월";
                break;
            case 2:
                textDow = "화";
                break;
            case 3:
                textDow = "수";
                break;
            case 4:
                textDow = "목";
                break;
            case 5:
                textDow = "금";
                break;
            case 6:
                textDow = "토";
                break;
            case 7:
                textDow = "일";
                break;
        }

        return textDow;

    }

    private String transferDateToText() {


        String year = String.valueOf(paymentDate.getYear());

        String month = "0";

        int intMonth = paymentDate.getMonthValue();

        if (intMonth < 10) {
            month += String.valueOf(intMonth);
        } else {
            month = String.valueOf(intMonth);
        }

        String day = "0";

        int intDayOfMonth = paymentDate.getDayOfMonth();

        if (intDayOfMonth < 10) {
            day += String.valueOf(intDayOfMonth);
        } else {
            day = String.valueOf(intDayOfMonth);
        }

        DayOfWeek dow = paymentDate.getDayOfWeek();
        String textDow = getDow(dow.getValue());

        String dateText = year.substring(2) + "." + month + "." + day + " " + textDow;

        return dateText;

    }

    private void createPayment() {

        // 1. null checking 을 진행한다
        if (inputName.getText().length() == 0 || inputCost.getText().length() == 0) {

            Toast.makeText(context, "모든 값을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. payment creation 을 진행한다.
        String paymentName = inputName.getText().toString();
        Integer paymentCost = Integer.parseInt(AppExtraMethods.wonToNormal(inputCost.getText().toString()));
        Boolean paymentType = type;

        GroupPaymentRequestDTO paymentRequest = new GroupPaymentRequestDTO(curGroup.getUuid(), paymentName, paymentCost, paymentType, paymentDate.toString());
        PaymentAndSenderDTO paymentData = new PaymentAndSenderDTO(curUser.getUuid(), paymentRequest);

        TransferModel<PaymentAndSenderDTO> requestModel = new TransferModel<>(paymentData);

        if (!isPaymentUpdating) { // 새로 추가하는 Payment 라면!
            GroupPaymentRetrofitService paymentRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupPaymentRetrofitService.class);
            paymentRetrofit.paymentCreate(requestModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TransferModel<GroupPaymentResponseDTO>>() {

                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull TransferModel<GroupPaymentResponseDTO> responseModel) {

                            GroupPaymentVO paymentVO = responseModel.getData().convertToVO();

                            Toast.makeText(context, paymentVO.getPaymentName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            if (e.getMessage() != null) {
                                Log.e(GroupPaymentActivity.GROUP_PAYMENT_TAG, e.getMessage());
                            }

                        }

                        @Override
                        public void onComplete() {

                            isPaymentAdded = true;

                            String msg = curUser.getUserName() + "님이 " + curGroup.getGroupName() + "의 회계장부에서 "
                                    + paymentName + "을 추가했어요";

                            // TODO: FCM 알림을 보낸다
                            try {
                                fcmListener.sendFcm(msg);
                            } catch (JSONException e) {
                                if (e.getMessage() != null) {
                                    Log.e("FCM Error:: ", e.getMessage());
                                }
                            }
                            // 3. dialog dismiss 를 한다.
                            finish();

                        }
                    });

        } else { // 수정하는 Payment 라면!

            GroupPaymentRetrofitService paymentRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(GroupPaymentRetrofitService.class);
            paymentRetrofit.updatePayment(requestModel, updatingPaymentUuid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TransferModel<GroupPaymentResponseDTO>>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull TransferModel<GroupPaymentResponseDTO> responseModel) {

                            GroupPaymentVO paymentVO = responseModel.getData().convertToVO();

                            Toast.makeText(context, paymentVO.getPaymentName() + "가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            if (e.getMessage() != null) {
                                Log.e(GroupPaymentActivity.GROUP_PAYMENT_TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void onComplete() {
                            isPaymentAdded = true;

                            String msg = curUser.getUserName() + "님이 " + curGroup.getGroupName() + "의 회계장부에서 "
                                    + paymentName + "을 수정했어요";

                            // TODO: FCM 알림을 보낸다
                            try {
                                fcmListener.sendFcm(msg);
                            } catch (JSONException e) {
                                if (e.getMessage() != null) {
                                    Log.e("FCM Error:: ", e.getMessage());
                                }
                            }

                            finish(); // Dismiss 하면 알아서 자동으로 List Re-update 한다.
                        }
                    });
        }


    }

    public void changeSelectedDate(LocalDate changedDate) {

        paymentDate = changedDate;

        textDate.setText(transferDateToText());

    }

    public void setPaymentType(boolean paymentType) {

        if (paymentType) { //수입
            type = true;

            typeIncome.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_selected, null));
            typeIncome.setTextColor(context.getResources().getColor(R.color.moimingWhite, null));

            typeExpense.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_normal, null));
            typeExpense.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));

        } else { // 지출
            type = false;
            typeExpense.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_selected, null));
            typeExpense.setTextColor(context.getResources().getColor(R.color.moimingWhite, null));

            typeIncome.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.shape_payment_type_normal, null));
            typeIncome.setTextColor(context.getResources().getColor(R.color.moimingTheme, null));
        }
    }

    public void setPaymentName(String paymentName) {

        inputName.setText(paymentName);

    }

    public void setPaymentCost(int paymentCost) {

        inputCost.setText(String.valueOf(paymentCost));

    }

    public void setIsPaymentUpdating(boolean isUpdating, String updatingPaymentUuid) {

        this.isPaymentUpdating = isUpdating;
        this.updatingPaymentUuid = updatingPaymentUuid;
    }

    private void finish() {

        this.dismiss();

    }


}
