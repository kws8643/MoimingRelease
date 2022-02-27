package com.example.moimingrelease.moiming_model.extras;

public class KakaoMoimingFriendsDTO {

    private MoimingMembersDTO memberData;
    private String kakaoId;

    public KakaoMoimingFriendsDTO(MoimingMembersDTO memberData, String kakaoId) {

        this.memberData = memberData;
        this.kakaoId = kakaoId;
    }

    public MoimingMembersDTO getMemberData() {
        return memberData;
    }

    public void setMemberData(MoimingMembersDTO memberData) {
        this.memberData = memberData;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public void setKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }
}
