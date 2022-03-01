package com.example.moimingrelease.moiming_model.response_dto;

import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.NonMoimingUserVO;
import com.example.moimingrelease.moiming_model.moiming_vo.UserSessionLinkerVO;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MoimingSessionResponseDTO {

    private UUID uuid;

    @SerializedName("session_type")
    private Integer sessionType; //0: 모금 1: 더치페이

    @SerializedName("session_name")
    private String sessionName;

    @SerializedName("session_creator_uuid")
    private UUID sessionCreatorUuid;

    @SerializedName("session_member_cnt")
    private Integer sessionMemberCnt;

    @SerializedName("cur_sender_cnt")
    private Integer curSenderCnt;

    @SerializedName("total_cost")
    private Integer totalCost;

    @SerializedName("single_cost")
    private Integer singleCost;

    @SerializedName("cur_cost")
    private Integer curCost;

    @SerializedName("is_finished")
    private Boolean isFinished;

    @SerializedName("deleted_at")
    private String deletedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("nmu_list")
    private List<NonMoimingUserResponseDTO> nmuList;

    @SerializedName("user_session_list")
    private List<USLinkerResponseDTO> userSessionList;

    public String getSessionName() {
        return sessionName;
    }

    public Integer getSessionType() {
        return sessionType;
    }

    public MoimingSessionVO convertToVO() {

        MoimingSessionVO moimingSessionVO = new MoimingSessionVO();

        moimingSessionVO.setUuid(this.uuid);
        moimingSessionVO.setSessionType(this.sessionType);
        moimingSessionVO.setSessionName(this.sessionName);
        moimingSessionVO.setSessionCreatorUuid(this.sessionCreatorUuid);
        moimingSessionVO.setSessionMemberCnt(this.sessionMemberCnt);
        moimingSessionVO.setCurSenderCnt(this.curSenderCnt);
        moimingSessionVO.setTotalCost(this.totalCost);
        moimingSessionVO.setSingleCost(this.singleCost);
        moimingSessionVO.setCurCost(curCost);
        moimingSessionVO.setFinished(isFinished);

        if(userSessionList != null) {
            List<UserSessionLinkerVO> usLinkerList = new ArrayList<>();

            for (USLinkerResponseDTO usLinker : userSessionList) {
                UserSessionLinkerVO usLinkerVO = usLinker.convertToVO();
                usLinkerList.add(usLinkerVO);
            }
            moimingSessionVO.setUsLinkerList(usLinkerList);
        }

        if(nmuList != null) {
            List<NonMoimingUserVO> nmuVoList = new ArrayList<>();

            for (NonMoimingUserResponseDTO nmuDTO : nmuList) {

                NonMoimingUserVO nmu = nmuDTO.convertToVO();
                nmuVoList.add(nmu);

            }
            moimingSessionVO.setNmuList(nmuVoList);
        }


        LocalDateTime createdAtForm = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        moimingSessionVO.setCreatedAt(createdAtForm.truncatedTo(ChronoUnit.SECONDS));

        if (this.updatedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            moimingSessionVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }

        if (this.deletedAt != null) {
            LocalDateTime updatedAtForm = LocalDateTime.parse(this.deletedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            moimingSessionVO.setUpdatedAt(updatedAtForm.truncatedTo(ChronoUnit.SECONDS));
        }


        return moimingSessionVO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setSessionType(Integer sessionType) {
        this.sessionType = sessionType;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public UUID getSessionCreatorUuid() {
        return sessionCreatorUuid;
    }

    public void setSessionCreatorUuid(UUID sessionCreatorUuid) {
        this.sessionCreatorUuid = sessionCreatorUuid;
    }

    public Integer getSessionMemberCnt() {
        return sessionMemberCnt;
    }

    public void setSessionMemberCnt(Integer sessionMemberCnt) {
        this.sessionMemberCnt = sessionMemberCnt;
    }
}
