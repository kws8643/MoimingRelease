package com.example.moimingrelease.moiming_model.response_dto;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserGroupLinkerVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class UGLinkerResponseDTO {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecentNotice() {
        return recentNotice;
    }

    public void setRecentNotice(String recentNotice) {
        this.recentNotice = recentNotice;
    }

    public Integer getNoticeCnt() {
        return noticeCnt;
    }

    public void setNoticeCnt(Integer noticeCnt) {
        this.noticeCnt = noticeCnt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public MoimingUserResponseDTO getMoimingUserResponseDTO() {
        return moimingUserResponseDTO;
    }

    public void setMoimingUserResponseDTO(MoimingUserResponseDTO moimingUserResponseDTO) {
        this.moimingUserResponseDTO = moimingUserResponseDTO;
    }

    public MoimingGroupResponseDTO getMoimingGroupResponseDTO() {
        return moimingGroupResponseDTO;
    }

    public void setMoimingGroupResponseDTO(MoimingGroupResponseDTO moimingGroupResponseDTO) {
        this.moimingGroupResponseDTO = moimingGroupResponseDTO;
    }

    private Long id;

    @SerializedName("recent_notice")
    private String recentNotice;

    @SerializedName("notice_cnt")
    private Integer noticeCnt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("moiming_user")
    private MoimingUserResponseDTO moimingUserResponseDTO;

    @SerializedName("moiming_group")
    private MoimingGroupResponseDTO moimingGroupResponseDTO;

    public UserGroupLinkerVO convertToVO() {

        UserGroupLinkerVO userGroupLinkerVO = new UserGroupLinkerVO();

        userGroupLinkerVO.setId(this.id);
        userGroupLinkerVO.setRecentNotice(this.recentNotice);
        userGroupLinkerVO.setNoticeCnt(this.noticeCnt);

        userGroupLinkerVO.setMoimingUser(moimingUserResponseDTO.convertToVO());
        userGroupLinkerVO.setMoimingGroup(moimingGroupResponseDTO.convertToVO());

        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        userGroupLinkerVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if (this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            userGroupLinkerVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        return userGroupLinkerVO;
    }
}
