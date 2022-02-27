package com.example.moimingrelease.moiming_model.request_dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class USLinkerRequestDTO { // 이걸 리스트로 묶어서 보낸다.

    @SerializedName("session_uuid")
    private UUID uuid; // 만드는 세션.

    @SerializedName("moiming_user_uuid")
    private UUID moimingUserUuid;

    @SerializedName("is_moiming_user")
    private boolean isMoimingUser;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("personal_cost")
    private Integer personalCost;


    // for Member
    public USLinkerRequestDTO(UUID sessionUuid, UUID moimingUserUuid, boolean isMoimingUser, String userName, Integer personalCost) {

        this.uuid = sessionUuid;
        this.moimingUserUuid = moimingUserUuid;
        this.isMoimingUser = isMoimingUser;
        this.userName = userName;
        this.personalCost = personalCost;
    }


    // for NMU
    public USLinkerRequestDTO(UUID sessionUuid, boolean isMoimingUser, String userName, Integer personalCost) {

        this.uuid = sessionUuid;
        this.isMoimingUser = isMoimingUser;
        this.userName = userName;
        this.personalCost = personalCost;

    }

    public String toString(){

        String jsonType = "{\n"
                + "uuid:\"" + uuid.toString() + "\""
                + "\nisMoimingUser:\"" + isMoimingUser + "\""
                + "\nuserName:\"" + userName + "\""
                + "\npersonalCost:\"" + personalCost + "\""
                + "\n}";

        return jsonType;
    }

}
