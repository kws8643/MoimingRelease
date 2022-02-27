package com.example.moimingrelease.moiming_model.extras;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class UserGroupUuidDTO {

    @SerializedName("user_uuid")
    private UUID userUuid;

    @SerializedName("group_uuid")
    private UUID groupUuid;

    public UserGroupUuidDTO(UUID userUuid, UUID groupUuid) {

        this.userUuid = userUuid;
        this.groupUuid = groupUuid;

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
