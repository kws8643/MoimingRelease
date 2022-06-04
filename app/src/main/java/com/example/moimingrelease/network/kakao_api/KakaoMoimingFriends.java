package com.example.moimingrelease.network.kakao_api;

import android.util.Log;

import com.example.moimingrelease.moiming_model.extras.KakaoMoimingFriendsDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UserRetrofitService;
import com.kakao.sdk.talk.model.Friend;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// 사용중인 카카오 유저 친구들. 한번 불러온 후 지속적인 사용을 위해.
public class KakaoMoimingFriends {

    private static KakaoMoimingFriends INSTANCE = new KakaoMoimingFriends();

    List<Friend> kakaoFriends = KakaoFriends.getINSTANCE().getKakaoFriendsList(); // 카카오 친구들.

    private List<KakaoMoimingFriendsDTO> kmfList;

    private List<MoimingMembersDTO> moimingMemberDataList; // 중간 데이터 주고받기 위해

    private LocalDateTime createdTime; // 모이밍 유저 생성 시간을 방지하기 위해 업데이트를 위해서.

    // 통신을 통해 리스트를 만든다.
    public KakaoMoimingFriends() {

        this.createdTime = LocalDateTime.now();

        parseMoimingUser();

    }


    // Kakao Friend 들의 oauth_uid 들을 가져온다.
    public void parseMoimingUser() {

        List<String> requestModel = new ArrayList<>();

        kmfList = new ArrayList<>();


        for (int i = 0; i < kakaoFriends.size(); i++) {

            String oauthUid = String.valueOf(kakaoFriends.get(i).component1());
            requestModel.add(oauthUid);
        }


        TransferModel<List<String>> parseModel = new TransferModel(requestModel);
        UserRetrofitService userRetrofitService = GlobalRetrofit.getInstance().getRetrofit().create(UserRetrofitService.class);
        userRetrofitService.parseMoimingUserFromKakao(parseModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<MoimingMembersDTO>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                        Log.w("KAKAO_FRIEND", "1");
                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<MoimingMembersDTO>> response) {

                        moimingMemberDataList = response.getData(); // 파싱된 친구들

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Log.e("KAKAO_FRIEND", e.getMessage());

                    }

                    @Override
                    public void onComplete() {


                        // TODO: Search Algorithm 필요
                        //       친구가 600명인데 500명이 모이밍 유저라면..?
                        ///      N^2
                        for (int i = 0; i < kakaoFriends.size(); i++) {

                            Friend thisFriend = kakaoFriends.get(i);

                            String friendUid = String.valueOf(thisFriend.getId()); // oauthUuid

                            for (int j = 0; j < moimingMemberDataList.size(); j++) {

                                MoimingMembersDTO memberData = moimingMemberDataList.get(j);

                                if (memberData.getOauthUid().equals(friendUid)) { // 모이밍 유저임.

                                    KakaoMoimingFriendsDTO kmfData = new KakaoMoimingFriendsDTO(memberData, thisFriend.getUuid());
                                    kmfList.add(kmfData);
                                }
                            }
                        }

                    }
                });
                /*.subscribe(new Observer<TransferModel<List<String>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                        Log.w("KAKAO_FRIEND", "1");

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<String>> response) {

                        Log.w("KAKAO_FRIEND", response.toString());

                        moimingFriendUids = response.getData();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Log.e("KAKAO_FRIEND", e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                        // TODO: Search Algorithm 필요
                        //       친구가 600명인데 500명이 모이밍 유저라면..?
                        for (String uid : moimingFriendUids) {

                            for (int i = 0; i < kakaoFriends.size(); i++) {

                                Friend thisFriend = kakaoFriends.get(i); // 카카오 친구들 중

                                String friendUid = String.valueOf(thisFriend.component1());

                                if (uid.equals(friendUid)) { // 파싱된 결과의 uuid 인 친구와 같은 친구는 모이밍 유저이다.
                                    resultFriends.add(thisFriend); // resultFreind 에 반환

                                    KakaoMoimingFriendsDTO kmfd = new KakaoMoimingFriendsDTO(UUID.fromString(uid), thisFriend);
                                    kmfList.add(kmfd);

                                }
                            }
                        }

                        setMoimingUserFriends(resultFriends);
                        Log.w("KAKAO_FRIEND", "DONE: " + moimingUserFriends.toString());

                    }
                });*/

        // MoimingUserFriendList 준비 완료.
    }
/*
    public List<Friend> getMoimingUserFriends() {

        return this.moimingUserFriends;
    }

    public List<KakaoMoimingFriendsDTO> getKmfList() {

        return this.kmfList;
    }*/

    public List<KakaoMoimingFriendsDTO> getKmfList() {
        return kmfList;
    }

    public void setKmfList(List<KakaoMoimingFriendsDTO> kmfList) {
        this.kmfList = kmfList;
    }

    public static KakaoMoimingFriends getInstance() {

        if (INSTANCE == null) { // 혹시 모르니.

            INSTANCE = new KakaoMoimingFriends();
        }

        // 1시간 뒤에 다시 요청할 경우는 카카오 유저를 다시 초기화한다.
        LocalDateTime calledTime = LocalDateTime.now();

        Long difference = ChronoUnit.HOURS.between(INSTANCE.createdTime, calledTime);
        int updateRef = difference.intValue();

        if (updateRef > 2) { // 생성된지 두시간이 넘었을 경우.
            Log.w("KAKAO_FRIEND", "UPDATED");
            INSTANCE = new KakaoMoimingFriends();
        }

        return INSTANCE;
    }

}
