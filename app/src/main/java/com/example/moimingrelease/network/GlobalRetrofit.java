package com.example.moimingrelease.network;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GlobalRetrofit {

    // 싱글톤 Retrofit 객체.
    private static GlobalRetrofit INSTANCE = new GlobalRetrofit();

    private static final String BASE_SERVER_URL = "http://10.0.2.2:8080/api/";
//    private static final String BASE_SERVER_URL = "http://localhost:8080/api/";
    private Retrofit retrofit;


    public GlobalRetrofit() {

        // Snake Case 로 자동으로 변환해주는 GSON 형식
//        final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(this.BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

    }

    public static GlobalRetrofit getInstance() {

        if (INSTANCE == null) { // 필요 없긴 하지만 혹시 모르니까.
            INSTANCE = new GlobalRetrofit();
        }
        return INSTANCE;
    }

    public Retrofit getRetrofit(){

        return this.retrofit;

    }

    // 전체 앱에서 활용할 Retrofit 을 만들었으니, 전체 Retrofit 이 하는
    // 역할 함수 생성.

    // Retrofit 으로 create 함. -> Interface 를 생성해야 함.
    // extends GlobalRetrofitBase


}
