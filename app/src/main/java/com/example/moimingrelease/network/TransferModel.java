package com.example.moimingrelease.network;

import com.google.gson.annotations.SerializedName;


public class TransferModel<T> {

    @SerializedName("transaction_time") // 이건 걍 String으로 받고 필요시!
    private String transactionTime;

    @SerializedName("result_code")
    private int resultCode;

    private String description;

    private T data;

    public TransferModel(T data) { // Request 용

        this.data = data;

    }

    public TransferModel(){

    }

    public TransferModel(String transactionTime, int resultCode, String description, T data) { // Response 용

        this.transactionTime = transactionTime;
        this.resultCode = resultCode;
        this.description = description;
        this.data = data;

    }

    public boolean hasData() {

        return this.data != null;

    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {

        String jsonType = "{\n"
                + "transaction_time:\"" + String.valueOf(transactionTime) + "\""
                + "\nresultCode:" + String.valueOf(resultCode)
                + "\ndescription:\"" + description + "\""
                + "\ndata:\n"
                + data.toString()
                + "\n"
                + "}";

        return jsonType;

    }
}
