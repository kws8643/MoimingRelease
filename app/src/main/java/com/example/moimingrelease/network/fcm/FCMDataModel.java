package com.example.moimingrelease.network.fcm;

public class FCMDataModel {

    private String activity;

    private String type;

    private String title;

    private String text;

    private String icon; // 아직 미정

    private String groupUuid;

    private String sessionUuid;

    public FCMDataModel(String activity, String type, String title, String text, String icon, String groupUuid, String sessionUuid){

        this.activity = activity;
        this.type = type;
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.groupUuid = groupUuid;
        this.sessionUuid = sessionUuid;

    }



}
