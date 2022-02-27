package com.example.moimingrelease.moiming_model.extras;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.moimingrelease.moiming_model.response_dto.NotificationResponseDTO;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceivedNotificationDTO implements Parcelable {

    @SerializedName("sent_user_name")
    private String sentUserName;

    @SerializedName("sent_group_name")
    private String sentGroupName;

    @SerializedName("sent_session_name")
    private String sentSessionName;

    @SerializedName("notification")
    private NotificationResponseDTO notification;



    public static final Creator<ReceivedNotificationDTO> CREATOR = new Creator<ReceivedNotificationDTO>() {
        @Override
        public ReceivedNotificationDTO createFromParcel(Parcel in) {
            return new ReceivedNotificationDTO(in);
        }

        @Override
        public ReceivedNotificationDTO[] newArray(int size) {
            return new ReceivedNotificationDTO[size];
        }
    };

    public String getSentUserName() {
        return sentUserName;
    }

    public void setSentUserName(String sentUserName) {
        this.sentUserName = sentUserName;
    }

    public String getSentGroupName() {
        return sentGroupName;
    }

    public void setSentGroupName(String sentGroupName) {
        this.sentGroupName = sentGroupName;
    }

    public String getSentSessionName() {
        return sentSessionName;
    }

    public void setSentSessionName(String sentSessionName) {
        this.sentSessionName = sentSessionName;
    }

    public NotificationResponseDTO getNotification() {
        return notification;
    }

    public void setNotification(NotificationResponseDTO notification) {
        this.notification = notification;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ReceivedNotificationDTO(Parcel in) {
        sentUserName = in.readString();
        sentGroupName = in.readString();
        sentSessionName = in.readString();
        notification = in.readParcelable(NotificationResponseDTO.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sentUserName);
        dest.writeString(sentGroupName);
        dest.writeString(sentSessionName);
        dest.writeParcelable(notification, flags);
    }
}

