package com.example.moimingrelease.app_listener_interface;

import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;

import java.util.UUID;

public interface PaymentSettingDialogListener {

    void touchEdit(GroupPaymentVO paymentData);

    void touchDelete(String paymentUuid);

}
