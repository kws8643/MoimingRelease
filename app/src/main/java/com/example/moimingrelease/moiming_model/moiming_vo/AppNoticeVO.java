package com.example.moimingrelease.moiming_model.moiming_vo;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppNoticeVO {

    private UUID uuid;

    @SerializedName("notice_title")
    private String noticeTitle;

    @SerializedName("notice_info")
    private String noticeInfo;

    @SerializedName("is_open")
    private Boolean isOpen;

    @SerializedName("is_url_linked")
    private Boolean isUrlLinked;

    @SerializedName("notice_url")
    private String noticeUrl;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeInfo() {
        return noticeInfo;
    }

    public void setNoticeInfo(String noticeInfo) {
        this.noticeInfo = noticeInfo;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Boolean getUrlLinked() {
        return isUrlLinked;
    }

    public void setUrlLinked(Boolean urlLinked) {
        isUrlLinked = urlLinked;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString() {
        return "AppNoticeVO{" +
                "uuid=" + uuid +
                ", noticeTitle='" + noticeTitle + '\'' +
                ", noticeInfo='" + noticeInfo + '\'' +
                ", isOpen=" + isOpen +
                ", isUrlLinked=" + isUrlLinked +
                ", noticeUrl='" + noticeUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
