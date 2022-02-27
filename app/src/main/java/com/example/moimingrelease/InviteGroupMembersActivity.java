package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.KakaoFriendRecyclerAdapter;
import com.example.moimingrelease.app_listener_interface.FCMCallBack;
import com.example.moimingrelease.app_listener_interface.InviteKmfCheckBoxListener;
import com.example.moimingrelease.moiming_model.extras.KakaoMoimingFriendsDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.UserGroupUuidDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.view.CustomUserViewer;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.TransferModel;
import com.example.moimingrelease.network.UGLinkerRetrofitService;
import com.example.moimingrelease.network.fcm.FCMAppToken;
import com.example.moimingrelease.network.fcm.FCMRequest;
import com.example.moimingrelease.network.kakao_api.KakaoMoimingFriends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.link.LinkClient;
import com.kakao.sdk.link.model.LinkResult;
import com.kakao.sdk.template.model.Content;
import com.kakao.sdk.template.model.DefaultTemplate;
import com.kakao.sdk.template.model.FeedTemplate;
import com.kakao.sdk.template.model.Link;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class InviteGroupMembersActivity extends AppCompatActivity {

    public String INVITE_MEMBER_TAG = "INVITE_MEMBER_TAG";

    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();

    private MoimingGroupVO curGroup;
    private List<KakaoMoimingFriendsDTO> kmfAdapterList;
    private List<KakaoMoimingFriendsDTO> kmfRawDataList;

    KakaoMoimingFriends kmf = KakaoMoimingFriends.getInstance();

    KakaoFriendRecyclerAdapter friendRecyclerAdapter;
    private RecyclerView friendsRecyclerView;

    private Button btnSendInvitation, btnKakaoLink;
    private EditText searchKmfList;
    private TextView textNoResult;
    public TextView textMemberCnt;
    private HorizontalScrollView horizontalViewMembers;
    private LinearLayout layoutMembersHolder;

    private List<MoimingMembersDTO> groupMembers; // 나를 제외한 그룹 멤버들을 가져오고, 여기서 파싱하여 속해 있는 사람은 체크해둔다.
    private List<String> groupMembersUuid;
    private List<String> groupMemberFcmTokens;

    private Map<String, CustomUserViewer> invitingUserViewerMap;

    private int curInvitingCnt = 0;


    // TODO: 초대된 대상도 알림을 받나? 나도 알림을 받나??

    private void receiveFcmTokens() { // TODO: 이거 될 때까지 ProgressBar 해놓는 것도 나쁘지 않을듯.

        // TODO: 나도 알림을 받나?

        for (int i = 0; i < groupMembersUuid.size(); i++) {

            DocumentReference keyDocs = firebaseDB.collection("UserInfo")
                    .document(groupMembersUuid.get(i)); // uuid 가 문서 명임.

            keyDocs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull @NotNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        DocumentSnapshot snapshot = task.getResult();

                        if (snapshot.exists()) {

                            Map<String, Object> responseSnap = snapshot.getData();
                            String memberFcmToken = (String) responseSnap.get("fcm_token");

                            groupMemberFcmTokens.add(memberFcmToken);

                        } else {

                            Log.e(INVITE_MEMBER_TAG, "해당 유저의 토큰 정보가 없습니다");
                        }

                    } else {
                        Log.e(INVITE_MEMBER_TAG, "해당 문서를 불러오지 못했습니다");
                    }
                }
            });

        }


    }

    public InviteKmfCheckBoxListener kmfCheckBoxListener = new InviteKmfCheckBoxListener() {
        @Override
        public void onKmfCheckBoxClick(boolean isAdded, MoimingMembersDTO selectedMember) {

            if (isAdded) { // 친구들 체크 박스 선택할 시

                if (curInvitingCnt == 0) {
                    textMemberCnt.setVisibility(View.VISIBLE);
                    horizontalViewMembers.setVisibility(View.VISIBLE);
                }

                addViewInHorizontalScroll(selectedMember);

                curInvitingCnt += 1;

            } else { // 친구들 체크 박스 해제할 시

                if (curInvitingCnt > 0) curInvitingCnt -= 1;

                CustomUserViewer thisUserViewer = invitingUserViewerMap.get(selectedMember.getUuid().toString());
                thisUserViewer.setVisibility(View.GONE);
                thisUserViewer = null;

                invitingUserViewerMap.remove(selectedMember.getUuid().toString());


                if (curInvitingCnt == 0) {
                    textMemberCnt.setVisibility(View.GONE);
                    horizontalViewMembers.setVisibility(View.GONE);
                }
            }

            String changeText = curInvitingCnt + "명 초대";
            textMemberCnt.setText(changeText);

        }
    };


    private void addViewInHorizontalScroll(MoimingMembersDTO member) {

        CustomUserViewer userViewer = new CustomUserViewer(this, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendRecyclerAdapter.removeMember(member.getUuid().toString());

                if (curInvitingCnt > 0) curInvitingCnt -= 1;

                CustomUserViewer thisUserViewer = invitingUserViewerMap.get(member.getUuid().toString());
                thisUserViewer.setVisibility(View.GONE);
                thisUserViewer = null;

                invitingUserViewerMap.remove(member.getUuid().toString());


                if (curInvitingCnt == 0) {
                    textMemberCnt.setVisibility(View.GONE);
                    horizontalViewMembers.setVisibility(View.GONE);
                }
                String changeText = curInvitingCnt + "명 초대";

                textMemberCnt.setText(changeText);

            }
        });

        userViewer.setTextUserName(member.getUserName());
        userViewer.setImgUserPfImg(member.getUserPfImg());

        layoutMembersHolder.addView(userViewer);
        invitingUserViewerMap.put(member.getUuid().toString(), userViewer);

        giveMarginInScrollView(userViewer);


    }

    private void giveMarginInScrollView(CustomUserViewer viewer) {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewer.getLayoutParams();

        params.setMargins(0, 0, dpToPx(8), 0);

        viewer.setLayoutParams(params);

    }

    // dp -> pixel 단위로 변경
    private int dpToPx(int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = searchKmfList.getText().toString();
            searchKmfs(text);
        }
    };

    private void searchKmfs(String userName) {

        kmfAdapterList.clear();

        if (userName.length() == 0) {

            // 그냥 RawData 다시 보여줌
            kmfAdapterList.addAll(kmfRawDataList);

            textNoResult.setVisibility(View.GONE);
            friendsRecyclerView.setVisibility(View.VISIBLE);

        } else { // 검색된 유저 이름을 토대로 kmfAdapterList 를 Reset 해줌.

            for (int i = 0; i < kmfRawDataList.size(); i++) {

                KakaoMoimingFriendsDTO kmfDTO = kmfRawDataList.get(i);

                if (kmfDTO.getMemberData().getUserName().toLowerCase().contains(userName)) {

                    kmfAdapterList.add(kmfDTO);
                }

            }

            if (kmfAdapterList.size() == 0) {
                textNoResult.setVisibility(View.VISIBLE);
                friendsRecyclerView.setVisibility(View.GONE);
            } else {
                textNoResult.setVisibility(View.GONE);
                friendsRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        friendRecyclerAdapter.notifyDataSetChanged();
    }


    private void receiveIntent() {

        Intent received = getIntent();

        if (received.getExtras() != null) {

            curGroup = (MoimingGroupVO) received.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));

            groupMembers = received.getParcelableArrayListExtra(getResources().getString(R.string.group_members_data_key));

        } else {

            // 잘못된 접근.
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group_members);

        receiveIntent();

        initView();

        initParams();

        receiveFcmTokens();

        initRecyclerView();


        btnSendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                makeInviteFeed();

                addSelectedMembersToGroup();

            }
        });

        btnKakaoLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAndSendKakaoTemplate();
            }
        });

    }

    private void initView() {

        friendsRecyclerView = findViewById(R.id.friends_recycler_view);
        btnSendInvitation = findViewById(R.id.btn_send_invitation);
        btnKakaoLink = findViewById(R.id.btn_send_kakao_link);

        textNoResult = findViewById(R.id.text_kmf_no_result);

        searchKmfList = findViewById(R.id.input_friends_search);
        searchKmfList.addTextChangedListener(textWatcher);

        // 추가된 회원들 보여주기, 시작 시 Visibility = gone
        textMemberCnt = findViewById(R.id.text_invite_group_member_cnt);
        horizontalViewMembers = findViewById(R.id.horizontal_group_members_invite);
        layoutMembersHolder = findViewById(R.id.layout_members_holder); // visibility visible

    }


    private void initParams() {

//        moimingUserFriendsList = kmf.getMoimingFriendData();

        kmfRawDataList = kmf.getKmfList(); // Raw Data List 에서 FCM 토큰들 집합 필요

        kmfAdapterList = new ArrayList<>();
        kmfAdapterList.addAll(kmfRawDataList);

        invitingUserViewerMap = new HashMap<>(); // CustomViewerMap 관리 용
        groupMemberFcmTokens = new ArrayList<>();

        groupMembersUuid = new ArrayList<>(); // 아답터 전송 용

        for (int i = 0; i < groupMembers.size(); i++) {

            groupMembersUuid.add((groupMembers.get(i)).getUuid().toString());

        }
    }

    private void addSelectedMembersToGroup() {

        List<String> invitedUuidList = friendRecyclerAdapter.getInvitedIdList();
        List<UserGroupUuidDTO> requestModelList = new ArrayList<>();

        for (String userUuid : invitedUuidList) {
            UserGroupUuidDTO singleLinker = new UserGroupUuidDTO(UUID.fromString(userUuid), curGroup.getUuid());
            requestModelList.add(singleLinker);
        }

        TransferModel<List<UserGroupUuidDTO>> requestModel = new TransferModel<>(requestModelList);

        UGLinkerRetrofitService retrofit = GlobalRetrofit.getInstance().getRetrofit().create(UGLinkerRetrofitService.class);
        retrofit.addMembersToGroupRequest(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel transferModel) {

                        Toast.makeText(getApplicationContext(), "멤버 추가를 완료하였습니다", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null) {

                            e.printStackTrace();
                            Log.e(INVITE_MEMBER_TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                        // TODO: 기존 앱 멤버들에게 멤버 초대 사실을 알린다.
                        try {

                            GroupActivity.GROUP_INFO_REFRESH_FLAG = true;

                            sendToInvitedUser(); // 새로 초대된 친구들에게 알려야 하는데 ...... 음.. UUID 필요!

                            sendFcmToMembers(); // 그룹원들에게 그룹원 초대 사실을 알림

                        } catch (JSONException e) {

                            e.printStackTrace();
                            Log.e(INVITE_MEMBER_TAG, e.getMessage());

                        }

                        // TODO: 초대했으면 그룹 액티비티 Refresh 필요!
                        finish();
                    }
                });

    }

    //TODO: 추후 FB FCM 토큰들이 다들 생기면 재 Test
    private void sendToInvitedUser() throws JSONException {


    }


    private void sendFcmToMembers() throws JSONException {


        for (int i = 0; i < groupMemberFcmTokens.size(); i++) {

            JSONObject jsonSend = FCMRequest.getInstance().buildJsonBody("안녕하세요", "그룹원들이 초대되었습니다", groupMemberFcmTokens.get(i));

            /*JSONObject jsonData = new JSONObject();

            jsonData.put("title", "안녕하세요");
            jsonData.put("text", "그룹원들이 초대되었습니다");

            JSONObject jsonMessage = new JSONObject();

            jsonMessage.put("token", groupMemberFcmTokens.get(i));
            jsonMessage.put("data", jsonData);

            JSONObject jsonSend = new JSONObject();
            jsonSend.put("message", jsonMessage);*/


            Log.w(INVITE_MEMBER_TAG, jsonSend.toString());


            RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                    , String.valueOf(jsonSend));


            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + FCMAppToken.moimingAccessToken)
                    .url("https://fcm.googleapis.com/v1/projects/moimingofficial-rel/messages:send/")
                    .post(reBody)
                    .build();


            OkHttpClient fcmClient = new OkHttpClient();
            fcmClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    // 메시지 전송이 실패했을 경우.
                    Log.e(INVITE_MEMBER_TAG, e.getMessage());

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    if (response.code() == 401) { // app access token expired

                        FCMAppToken.getFcmAppAccessToken(getApplicationContext(), new FCMCallBack() {
                            @Override
                            public void onSuccess() {

                                Request request = new Request.Builder()
                                        .header("Content-Type", "application/json")
                                        .addHeader("Authorization", "Bearer " + FCMAppToken.moimingAccessToken)
                                        .url("https://fcm.googleapis.com/v1/projects/moimingofficial-rel/messages:send/")
                                        .post(reBody)
                                        .build();

                                fcmClient.newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(okhttp3.Call call, IOException e) {

                                        Log.e(INVITE_MEMBER_TAG, "재호출시 error:: " + e.getMessage());

                                    }

                                    @Override
                                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                                        if (response.isSuccessful()) {
                                            // 전송이 완료됨.
                                            Log.w(INVITE_MEMBER_TAG, "Successfully Sent Message");

                                        } else {
                                            Toast.makeText(getApplicationContext(), "통신을 실패하였습니다..", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {

                                Log.e(INVITE_MEMBER_TAG, "Token Refresh Error");

                            }
                        });

                    }

                    if (response.isSuccessful()) {

                        Log.w(INVITE_MEMBER_TAG, "Successfully Sent Message (No Refresh)");

                    }
                }
            });

        }
    }


/*
    private void sendFcmToMembers2() throws JSONException {

        // 이걸 모든 그룹 멤버들에게 보내면 된다.

        for (int i = 0; i < groupMemberFcmTokens.size(); i++) {

            JSONObject jsonData = new JSONObject();

            jsonData.put("title", "안녕하세요");
            jsonData.put("text", "그룹원들이 초대되었습니다");

            JSONObject jsonMessage = new JSONObject();

            jsonMessage.put("token", groupMemberFcmTokens.get(i));
            jsonMessage.put("data", jsonData);

            JSONObject jsonSend = new JSONObject();
            jsonSend.put("message", jsonMessage);

            Log.w(INVITE_MEMBER_TAG, jsonSend.toString());

            FCMRetrofitService fcmService = FCMRetrofit.getInstance().getRetrofit().create(FCMRetrofitService.class);
            fcmService.sendMessageToUser(FCMAppToken.moimingAccessToken, jsonSend.toString())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {

                                JSONObject jsonResponse = new JSONObject(response.body());

                                int errorCode = (int) jsonResponse.get("code");

                                if (errorCode == 401) { // 만료되었을 경우? call back 으로 재요청

                                    FCMAppToken.getFcmAppAccessToken(getApplicationContext(), new FCMCallBack() {
                                        @Override
                                        public void onSuccess() {

                                            fcmService.sendMessageToUser(FCMAppToken.moimingAccessToken, jsonSend.toString())
                                                    .enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {

                                                            if (response.isSuccessful()) {
                                                                // 전송이 완료됨.
                                                                Log.w(INVITE_MEMBER_TAG, "Successfully Sent Message");

                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "통신을 실패하였습니다..", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {

                                                            Log.e(INVITE_MEMBER_TAG, "재호출시 error:: " + t.getMessage());

                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    });

                                }

                            } catch (JSONException | IOException e) {


                                Log.e(INVITE_MEMBER_TAG, "Response 형성 JSON Failure:: " + e.getMessage());
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            // 메시지 전송이 실패했을 경우.
                            Log.e(INVITE_MEMBER_TAG, t.getMessage());


                        }
                    });
        }
    }
*/

    private DefaultTemplate buildTemplate() {
        String title = curGroup.getGroupName() + "에서 회원님을 초대합니다!";
        String description = curGroup.getGroupName() + " 모임에서 회원님을 초대하였습니당~!";

        Link sendTo = new Link(null, "");

        com.kakao.sdk.template.model.Button templateBtn = new com.kakao.sdk.template.model.Button("초대 수락하기", sendTo);
        List<com.kakao.sdk.template.model.Button> listBtn = new ArrayList<>();
        listBtn.add(templateBtn);

        DefaultTemplate indvTemplate = new FeedTemplate(new Content(title, "", sendTo, description), null, listBtn);


        return indvTemplate;
    }

    private void openAndSendKakaoTemplate() {

//        DefaultTemplate defTemp = buildTemplate();
        FeedTemplate feedTemplate = new FeedTemplate(new Content("title", "imageUrl",    //메시지 제목, 이미지 url
                new Link("https://www.naver.com"), "description",                    //메시지 링크, 메시지 설명
                300, 300));

        if (LinkClient.getInstance().isKakaoLinkAvailable(getApplicationContext())) {


            LinkClient.Companion.getInstance().defaultTemplate(getApplicationContext(), feedTemplate, new Function2<LinkResult, Throwable, Unit>() {
                @Override
                public Unit invoke(LinkResult linkResult, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else if (linkResult != null) {

                        Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                    }

                    return null;
                }
            });

        } else {


            Toast.makeText(getApplicationContext(), "불가", Toast.LENGTH_SHORT).show();
        }

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        friendsRecyclerView.setLayoutManager(linearLayoutManager);

        friendRecyclerAdapter = new KakaoFriendRecyclerAdapter(getApplicationContext(), kmfAdapterList, groupMembersUuid, kmfCheckBoxListener);
        friendsRecyclerView.setAdapter(friendRecyclerAdapter);

    }

/*

    private void makeInviteFeed() {

        Map<String, String> invitedIdMap = friendRecyclerAdapter.getInvitedIdMap();

        // 템플릿의 고정 인자
        String title = curGroup.getGroupName() + "에서 회원님을 초대합니다!";
        String description = curGroup.getGroupName() + " 모임에서 회원님을 초대하였습니당~!";
        String baseUrl = GlobalRetrofit.getInstance().getBaseServerUrl() + "api/userGroupLinker";
        String groupUuid = curGroup.getUuid().toString();

        List<String> singleUuidHolder = new ArrayList<>();

        if (!invitedIdMap.isEmpty()) {

            Set<String> uuids = invitedIdMap.keySet();
            Iterator<String> iterator = uuids.iterator();

            while (iterator.hasNext()) { // 한 명 한명에게 모두 보낸다.

                String uuid = iterator.next();
                String kakaoUid = invitedIdMap.get(uuid);
                System.out.println(uuid + ", " + kakaoUid);

                singleUuidHolder.add(uuid);

                // TODO: URL 형성은 잘 됐는데, 카카오에서 누르면 주소 형성이 안되어 있음. 템플렛에 링크 주입이 안됨.

                String base = GlobalRetrofit.getInstance().getBaseServerUrl(); // 그밖에
//                String base = "http://192.168.0.25:8080/"; // home
                String sendUrl = base + "api/userGroupLinker/link/" + groupUuid + "?oauthUid=" + kakaoUid;

                Link sendTo = new Link(sendUrl, sendUrl);

                com.kakao.sdk.template.model.Button templateBtn = new com.kakao.sdk.template.model.Button("초대 수락하기", sendTo);
                List<com.kakao.sdk.template.model.Button> listBtn = new ArrayList<>();
                listBtn.add(templateBtn);

                DefaultTemplate indvTemplate = new FeedTemplate(new Content(title, "", sendTo, description), null, listBtn);

                TalkApiClient.getInstance().sendDefaultMessage(singleUuidHolder, indvTemplate, new Function2<MessageSendResult, Throwable, Unit>() {
                    @Override
                    public Unit invoke(MessageSendResult messageSendResult, Throwable throwable) {
                        if (throwable == null) { // 통신 성공

                            Toast.makeText(getApplicationContext(), "초대 요청을 전송하였습니다!", Toast.LENGTH_SHORT).show();


                        } else { // 통신 실패

                            Log.e(INVITE_MEMBER_TAG, throwable.getMessage());
                            System.out.println("카카오톡 메시지 동의는 필수 항목입니다.");

                        }

                        return null;

                    }
                });

                singleUuidHolder.clear();
            }

            finish();

        } else {

            Toast.makeText(getApplicationContext(), "선택하고 초대를 하든가 하세요", Toast.LENGTH_SHORT).show();

        }
    }
*/
}