package com.example.moimingrelease.moiming_model.extras;

import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;

public class PaymentViewData {

    private GroupPaymentVO paymentData;
    private int calculatedCost;

    public PaymentViewData(GroupPaymentVO paymentData, int calculatedCost){

        this.paymentData = paymentData;
        this.calculatedCost = calculatedCost;

    }

    public GroupPaymentVO getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(GroupPaymentVO paymentData) {
        this.paymentData = paymentData;
    }

    public int getCalculatedCost() {
        return calculatedCost;
    }

    public void setCalculatedCost(int calculatedCost) {
        this.calculatedCost = calculatedCost;
    }
}
