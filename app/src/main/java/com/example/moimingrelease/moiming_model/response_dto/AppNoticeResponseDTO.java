package com.example.moimingrelease.moiming_model.response_dto;

import com.example.moimingrelease.moiming_model.moiming_vo.AppNoticeVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AppNoticeResponseDTO {

    private UUID uuid;

    @SerializedName("notice_title")
    private String noticeTitle;

    @SerializedName("notice_info")
    private String noticeInfo;

    @SerializedName("is_url_linked")
    private Boolean isUrlLinked;

    @SerializedName("notice_url")
    private String noticeUrl;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public AppNoticeVO convertToVO() {

        AppNoticeVO appNoticeVO = new AppNoticeVO();

        appNoticeVO.setUuid(this.uuid);
        appNoticeVO.setNoticeTitle(this.noticeTitle);
        appNoticeVO.setNoticeInfo(this.noticeInfo);
        appNoticeVO.setUrlLinked(this.isUrlLinked);
        appNoticeVO.setOpen(true);
        appNoticeVO.setNoticeUrl(this.noticeUrl);


        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        appNoticeVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if (this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            appNoticeVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return appNoticeVO;

    }

}
