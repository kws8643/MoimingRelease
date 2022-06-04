package com.example.moimingrelease.app_adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.text.DecimalFormat;

public class AppExtraMethods {

    public static DecimalFormat wonFormat = new DecimalFormat("#,###");

    // 10000 --> 10,000 원으로 변경하는 method
    public static String moneyToFormatWon2(String inputMoney){

//        return wonFormat.format(inputMoney);
        return null;
    }

    public static String moneyToWonWon(int inputMoney){
        return wonFormat.format(inputMoney);
    }

    // 10,000 --> 10000 원으로 변경하는 method
    public static String wonToNormal(String money){

        StringBuilder result = new StringBuilder();

        for(int i = 0; i < money.length(); i++){
            if(money.charAt(i) != ','){
                result.append(money.charAt(i));
            }
        }

        return result.toString();
    }
    // TODO: watcher 는 그 때 그 때 edit text 에 적용해야 할듯!
}
