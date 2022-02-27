package com.example.moimingrelease.moiming_model.response_dto;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NotificationVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class NotificationResponseDTO implements Parcelable {

    @SerializedName("sent_user_uuid")
    private UUID sentUserUuid;

    @SerializedName("sent_activity")
    private String sentActivity;

    @SerializedName("sent_group_uuid")
    private UUID sentGroupUuid;

    @SerializedName("sent_session_uuid")
    private UUID sentSessionUuid;

    @SerializedName("msg_type")
    private Integer msgType;

    @SerializedName("msg_text")
    private String msgText;

    @SerializedName("is_read")
    private Boolean isRead;

    @SerializedName("created_at")
    private String createdAt;



    public static final Creator<NotificationResponseDTO> CREATOR = new Creator<NotificationResponseDTO>() {
        @Override
        public NotificationResponseDTO createFromParcel(Parcel in) {
            return new NotificationResponseDTO(in);
        }

        @Override
        public NotificationResponseDTO[] newArray(int size) {
            return new NotificationResponseDTO[size];
        }
    };

    public NotificationVO convertToVO(){

        // 여기서 LocalDateTime 으로 CreatedAt / UpdatedAt 변환 후 VO로 저장.
        NotificationVO generateVO = new NotificationVO();

        generateVO.setSentUserUuid(this.sentUserUuid);
        generateVO.setSentActivity(this.sentActivity);
        generateVO.setSentGroupUuid(this.sentGroupUuid);
        generateVO.setSentSessionUuid(this.sentSessionUuid);
        generateVO.setMsgType(this.msgType);
        generateVO.setMsgText(this.msgText);
        generateVO.setRead(this.isRead);

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        generateVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        return generateVO;

    }

    public LocalDateTime getCreatedAtForm(){

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        return createdAtForm;
    }

    public UUID getSentUserUuid() {
        return sentUserUuid;
    }

    public void setSentUserUuid(UUID sentUserUuid) {
        this.sentUserUuid = sentUserUuid;
    }

    public String getSentActivity() {
        return sentActivity;
    }

    public void setSentActivity(String sentActivity) {
        this.sentActivity = sentActivity;
    }

    public UUID getSentGroupUuid() {
        return sentGroupUuid;
    }

    public void setSentGroupUuid(UUID sentGroupUuid) {
        this.sentGroupUuid = sentGroupUuid;
    }

    public UUID getSentSessionUuid() {
        return sentSessionUuid;
    }

    public void setSentSessionUuid(UUID sentSessionUuid) {
        this.sentSessionUuid = sentSessionUuid;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    protected NotificationResponseDTO(Parcel in) {
        sentUserUuid = UUID.fromString(in.readString());
        sentActivity = in.readString();
        sentGroupUuid = UUID.fromString(in.readString());
        sentSessionUuid= UUID.fromString(in.readString());
        msgType = in.readInt();
        msgText = in.readString();
        isRead = Boolean.parseBoolean(in.readString());
        createdAt = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sentUserUuid.toString());
        dest.writeString(sentActivity);
        dest.writeString(sentGroupUuid.toString());
        dest.writeString(sentSessionUuid.toString());
        dest.writeInt(msgType);
        dest.writeString(msgText);
        dest.writeString(String.valueOf(isRead));
        dest.writeString(createdAt);
    }
}
