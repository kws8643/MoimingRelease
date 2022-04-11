package com.example.moimingrelease.app_listener_interface;

import org.json.JSONException;

public interface PaymentSendFcmListener {

    void sendFcm(String fcmMsg) throws JSONException;
}
