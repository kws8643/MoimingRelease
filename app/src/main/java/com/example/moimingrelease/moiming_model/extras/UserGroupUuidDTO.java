package com.example.moimingrelease.moiming_model.extras;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class UserGroupUuidDTO {

    // 두 관계의 행위에 대한 요청을 하는 유저
    @SerializedName("request_user_uuid")
    private UUID requestUserUuid;

    @SerializedName("user_uuid")
    private UUID userUuid;

    @SerializedName("group_uuid")
    private UUID groupUuid;

    public UserGroupUuidDTO(UUID requestUserUuid, UUID userUuid, UUID groupUuid) {

        this.requestUserUuid = requestUserUuid;
        this.userUuid = userUuid;
        this.groupUuid = groupUuid;

    }

    public UserGroupUuidDTO(UUID userUuid, UUID groupUuid) {

        this.userUuid = userUuid;
        this.groupUuid = groupUuid;

    }

    public UUID getRequestUserUuid() {
        return requestUserUuid;
    }

    public void setRequestUserUuid(UUID requestUserUuid) {
        this.requestUserUuid = requestUserUuid;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public UUID getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(UUID groupUuid) {
        this.groupUuid = groupUuid;
    }
}
