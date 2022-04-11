package com.example.moimingrelease.moiming_model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.moimingrelease.MainActivity;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.GroupExitDialogListener;
import com.example.moimingrelease.app_listener_interface.MainFixedGroupRefreshListener;
import com.example.moimingrelease.app_listener_interface.PaymentSettingDialogListener;
import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;

import org.json.JSONArray;
import org.json.JSONException;

public class PaymentLongTouchDialog extends Dialog {

    private Context mContext;
    private PaymentSettingDialogListener paymentDialogListener;
    private GroupPaymentVO paymentData;

    private LinearLayout btnEdit, btnDelete;

    // 여기에서 하는 moiming_user_db 에서의 일은 insert / create 밖에 없음!
    public PaymentLongTouchDialog(Context mContext, PaymentSettingDialogListener paymentDialogListener, GroupPaymentVO paymentData) {

        super(mContext);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.mContext = mContext;
        this.paymentDialogListener = paymentDialogListener;
        this.paymentData = paymentData;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_payment_setting);

        initView();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentDialogListener.touchEdit(paymentData);

                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentDialogListener.touchDelete(paymentData.getUuid().toString(), paymentData.getPaymentName());
                
                finish();

            }
        });

    }

    private void initView() {

        btnEdit = findViewById(R.id.btn_payment_edit);
        btnDelete = findViewById(R.id.btn_payment_delete);
    }

    private void finish() {

        this.dismiss();
    }


}
