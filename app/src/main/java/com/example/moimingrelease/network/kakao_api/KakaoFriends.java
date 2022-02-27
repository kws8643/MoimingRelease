package com.example.moimingrelease.network.kakao_api;

import com.kakao.sdk.talk.model.Friend;

import java.util.List;

public class KakaoFriends {

    private static KakaoFriends INSTANCE = new KakaoFriends();
    private List<Friend> kakaoFriendsList;

    public KakaoFriends() {


    }

    public static KakaoFriends getINSTANCE() {

        if (INSTANCE == null) {

            INSTANCE = new KakaoFriends();
        }

        return INSTANCE;
    }

    public void setKakaoFriendsList(List<Friend> kakaoFriendsList) {

        this.kakaoFriendsList = kakaoFriendsList;

    }

    public List<Friend> getKakaoFriendsList() {

        return this.kakaoFriendsList;
    }


}
