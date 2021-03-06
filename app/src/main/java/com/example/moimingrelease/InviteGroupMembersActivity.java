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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moimingrelease.app_adapter.KakaoFriendRecyclerAdapter;
import com.example.moimingrelease.app_listener_interface.CancelActivityCallBack;
import com.example.moimingrelease.app_listener_interface.FCMCallBack;
import com.example.moimingrelease.app_listener_interface.InviteKmfCheckBoxListener;
import com.example.moimingrelease.moiming_model.dialog.CancelActivityDialog;
import com.example.moimingrelease.moiming_model.extras.KakaoMoimingFriendsDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.UserGroupUuidDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingGroupVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
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

    private MoimingUserVO curUser;
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

    //    private List<MoimingMembersDTO> groupMembers; // ?????? ????????? ?????? ???????????? ????????????, ????????? ???????????? ?????? ?????? ????????? ???????????????.
    private Map<UUID, String> memberFcmTokenMap;
    private List<String> groupMembersUuid;

    private Map<String, CustomUserViewer> invitingUserViewerMap;

    private int curInvitingCnt = 0;

/*
    // TODO: ????????? ????????? ????????? ??????? ?????? ????????? ????????

    private void receiveFcmTokens() { // TODO: ?????? ??? ????????? ProgressBar ????????? ?????? ????????? ?????????.

        // TODO: ?????? ????????? ???????

        for (int i = 0; i < groupMembersUuid.size(); i++) {

            DocumentReference keyDocs = firebaseDB.collection("UserInfo")
                    .document(groupMembersUuid.get(i)); // uuid ??? ?????? ??????.

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

                            Log.e(INVITE_MEMBER_TAG, "?????? ????????? ?????? ????????? ????????????");
                        }

                    } else {
                        Log.e(INVITE_MEMBER_TAG, "?????? ????????? ???????????? ???????????????");
                    }
                }
            });

        }


    }*/

    public InviteKmfCheckBoxListener kmfCheckBoxListener = new InviteKmfCheckBoxListener() {
        @Override
        public void onKmfCheckBoxClick(boolean isAdded, MoimingMembersDTO selectedMember) {

            if (isAdded) { // ????????? ?????? ?????? ????????? ???

                if (curInvitingCnt == 0) {
                    textMemberCnt.setVisibility(View.VISIBLE);
                    horizontalViewMembers.setVisibility(View.VISIBLE);
                }

                addViewInHorizontalScroll(selectedMember);

                curInvitingCnt += 1;

            } else { // ????????? ?????? ?????? ????????? ???

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

            String changeText = curInvitingCnt + "??? ??????";
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
                String changeText = curInvitingCnt + "??? ??????";

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

    // dp -> pixel ????????? ??????
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

            // ?????? RawData ?????? ?????????
            kmfAdapterList.addAll(kmfRawDataList);

            textNoResult.setVisibility(View.GONE);
            friendsRecyclerView.setVisibility(View.VISIBLE);

        } else { // ????????? ?????? ????????? ????????? kmfAdapterList ??? Reset ??????.

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

            curUser = (MoimingUserVO) received.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));

            curGroup = (MoimingGroupVO) received.getExtras().getSerializable(getResources().getString(R.string.moiming_group_data_key));

            groupMembersUuid = received.getStringArrayListExtra("group_members_uuid");

            memberFcmTokenMap = (Map<UUID, String>) received.getExtras().getSerializable(getResources().getString(R.string.fcm_token_map));

        } else {

            // ????????? ??????.
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

//        receiveFcmTokens();

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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CancelActivityDialog dialog = new CancelActivityDialog(InviteGroupMembersActivity.this, finishCallback, "????????? ???????????? ????????????");

                dialog.show();
            }
        });
    }

    private CancelActivityCallBack finishCallback = new CancelActivityCallBack() {
        @Override
        public void finishActivity() {

            finish();
        }
    };

    private ImageView btnBack;

    private void initView() {

        btnBack = findViewById(R.id.btn_back_invite_members);

        friendsRecyclerView = findViewById(R.id.friends_recycler_view);
        btnSendInvitation = findViewById(R.id.btn_send_invitation);
        btnKakaoLink = findViewById(R.id.btn_send_kakao_link);

        textNoResult = findViewById(R.id.text_kmf_no_result);

        searchKmfList = findViewById(R.id.input_friends_search);
        searchKmfList.addTextChangedListener(textWatcher);

        // ????????? ????????? ????????????, ?????? ??? Visibility = gone
        textMemberCnt = findViewById(R.id.text_invite_group_member_cnt);
        horizontalViewMembers = findViewById(R.id.horizontal_group_members_invite);
        layoutMembersHolder = findViewById(R.id.layout_members_holder); // visibility visible

    }


    private void initParams() {

//        moimingUserFriendsList = kmf.getMoimingFriendData();

        kmfRawDataList = kmf.getKmfList(); // Raw Data List ?????? FCM ????????? ?????? ??????

        kmfAdapterList = new ArrayList<>();
        kmfAdapterList.addAll(kmfRawDataList);

        invitingUserViewerMap = new HashMap<>(); // CustomViewerMap ?????? ???

    }

    private List<String> addedUserUuid;

    private void addSelectedMembersToGroup() {

        List<String> invitedUuidList = friendRecyclerAdapter.getInvitedIdList();
        List<UserGroupUuidDTO> requestModelList = new ArrayList<>();

        for (String userUuid : invitedUuidList) {
            UserGroupUuidDTO singleLinker = new UserGroupUuidDTO(curUser.getUuid(), UUID.fromString(userUuid), curGroup.getUuid());
            requestModelList.add(singleLinker);
        }

        TransferModel<List<UserGroupUuidDTO>> requestModel = new TransferModel<>(requestModelList);

        UGLinkerRetrofitService retrofit = GlobalRetrofit.getInstance().getRetrofit().create(UGLinkerRetrofitService.class);
        retrofit.addMembersToGroupRequest(requestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TransferModel<List<String>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TransferModel<List<String>> responseModel) {

                        Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();

                        addedUserUuid = responseModel.getData();
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

                        // TODO: ?????? ??? ??????????????? ?????? ?????? ????????? ?????????.
                        try {

                            GroupActivity.GROUP_INFO_REFRESH_FLAG = true;
                            MainActivity.IS_NOTIFICATION_REFRESH_NEEDED = true;

                            // TODO: FCM TOKEN MAP ????????? GroupActivity?????? ??????????????? ????????? ???????????? ???????????? ??????
                            /*if (addedUserUuid != null) {
                                sendToInvitedUser(); //1. ????????? ???????????? ??????????????? ??????
                            }*/

                            if (addedUserUuid != null) {
                                sendFcmToMembers(); //2. ??????????????? ????????? x ??? ?????????????????? ??????.
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                            Log.e(INVITE_MEMBER_TAG, e.getMessage());

                        }

                        // TODO: ??????????????? ?????? ???????????? Refresh ??????!
                        Intent finishIntent = new Intent();

                        if (addedUserUuid != null) {
                            finishIntent.putStringArrayListExtra("added_user_uuid", (ArrayList<String>) addedUserUuid);
                            setResult(RESULT_OK, finishIntent);
                        } else {
                            setResult(RESULT_CANCELED);
                        }

                        finish();
                    }
                });

    }


    private void sendFcmToMembers() throws JSONException {

        List<String> fcmTokenList = new ArrayList<>(memberFcmTokenMap.values());

        int addedMemberCnt = addedUserUuid.size();

        String userName = "";

        for (int i = 0; i < kmfRawDataList.size(); i++) { //

            if (kmfRawDataList.get(i).getMemberData().getUuid().toString().equals(addedUserUuid.get(0))) {

                userName = kmfRawDataList.get(i).getMemberData().getUserName();
            }
        }

        if (userName.equals("")) {
            Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        String textNoti;

        if (addedMemberCnt > 1) {
            textNoti = userName + "??? ??? " + (addedMemberCnt - 1) + "?????? " + curGroup.getGroupName() + "??? ?????????????????????! ??????????????????!";
        } else { // 1 ?????????
            textNoti = userName + "?????? " + curGroup.getGroupName() + "??? ?????????????????????! ??????????????????!";
        }


        if (addedMemberCnt != 0) {

            for (int i = 0; i < fcmTokenList.size(); i++) { // ?????? ?????????????????? ?????? ????????? ??????

                JSONObject jsonSend = FCMRequest.getInstance()
                        .buildFcmJsonData("group", String.valueOf(0), "????????? ?????????", textNoti, "", curGroup.getUuid().toString()
                                , "", fcmTokenList.get(i));

//                JSONObject jsonSend = FCMRequest.getInstance().buildJsonBody("????????? ?????? ??????", addedMemberCnt + "?????? ??????????????? ?????????????????????"
//                        , fcmTokenList.get(i));

                RequestBody reBody = RequestBody.create(MediaType.parse("application/json, charset-utf8")
                        , String.valueOf(jsonSend));

                FCMRequest.getInstance().sendFcmRequest(getApplicationContext(), reBody);

            }
        }
    }


/*
    private void sendFcmToMembers2() throws JSONException {

        // ?????? ?????? ?????? ??????????????? ????????? ??????.

        for (int i = 0; i < groupMemberFcmTokens.size(); i++) {

            JSONObject jsonData = new JSONObject();

            jsonData.put("title", "???????????????");
            jsonData.put("text", "??????????????? ?????????????????????");

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

                                if (errorCode == 401) { // ??????????????? ??????? call back ?????? ?????????

                                    FCMAppToken.getFcmAppAccessToken(getApplicationContext(), new FCMCallBack() {
                                        @Override
                                        public void onSuccess() {

                                            fcmService.sendMessageToUser(FCMAppToken.moimingAccessToken, jsonSend.toString())
                                                    .enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {

                                                            if (response.isSuccessful()) {
                                                                // ????????? ?????????.
                                                                Log.w(INVITE_MEMBER_TAG, "Successfully Sent Message");

                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "????????? ?????????????????????..", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {

                                                            Log.e(INVITE_MEMBER_TAG, "???????????? error:: " + t.getMessage());

                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    });

                                }

                            } catch (JSONException | IOException e) {


                                Log.e(INVITE_MEMBER_TAG, "Response ?????? JSON Failure:: " + e.getMessage());
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            // ????????? ????????? ???????????? ??????.
                            Log.e(INVITE_MEMBER_TAG, t.getMessage());


                        }
                    });
        }
    }
*/

    private DefaultTemplate buildTemplate() {
        String title = curGroup.getGroupName() + "?????? ???????????? ???????????????!";
        String description = curGroup.getGroupName() + " ???????????? ???????????? ?????????????????????~!";

        Link sendTo = new Link(null, "");

        com.kakao.sdk.template.model.Button templateBtn = new com.kakao.sdk.template.model.Button("?????? ????????????", sendTo);
        List<com.kakao.sdk.template.model.Button> listBtn = new ArrayList<>();
        listBtn.add(templateBtn);

        DefaultTemplate indvTemplate = new FeedTemplate(new Content(title, "", sendTo, description), null, listBtn);


        return indvTemplate;
    }

    private void openAndSendKakaoTemplate() {

//        DefaultTemplate defTemp = buildTemplate();
        FeedTemplate feedTemplate = new FeedTemplate(new Content("title", "imageUrl",    //????????? ??????, ????????? url
                new Link("https://www.naver.com"), "description",                    //????????? ??????, ????????? ??????
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


            Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();
        }

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        friendsRecyclerView.setLayoutManager(linearLayoutManager);

        friendRecyclerAdapter = new KakaoFriendRecyclerAdapter(getApplicationContext(), kmfAdapterList, groupMembersUuid, kmfCheckBoxListener);
        friendsRecyclerView.setAdapter(friendRecyclerAdapter);

    }

    @Override
    public void onBackPressed() {

        btnBack.performClick();
    }

    /*

    private void makeInviteFeed() {

        Map<String, String> invitedIdMap = friendRecyclerAdapter.getInvitedIdMap();

        // ???????????? ?????? ??????
        String title = curGroup.getGroupName() + "?????? ???????????? ???????????????!";
        String description = curGroup.getGroupName() + " ???????????? ???????????? ?????????????????????~!";
        String baseUrl = GlobalRetrofit.getInstance().getBaseServerUrl() + "api/userGroupLinker";
        String groupUuid = curGroup.getUuid().toString();

        List<String> singleUuidHolder = new ArrayList<>();

        if (!invitedIdMap.isEmpty()) {

            Set<String> uuids = invitedIdMap.keySet();
            Iterator<String> iterator = uuids.iterator();

            while (iterator.hasNext()) { // ??? ??? ???????????? ?????? ?????????.

                String uuid = iterator.next();
                String kakaoUid = invitedIdMap.get(uuid);
                System.out.println(uuid + ", " + kakaoUid);

                singleUuidHolder.add(uuid);

                // TODO: URL ????????? ??? ?????????, ??????????????? ????????? ?????? ????????? ????????? ??????. ???????????? ?????? ????????? ??????.

                String base = GlobalRetrofit.getInstance().getBaseServerUrl(); // ?????????
//                String base = "http://192.168.0.25:8080/"; // home
                String sendUrl = base + "api/userGroupLinker/link/" + groupUuid + "?oauthUid=" + kakaoUid;

                Link sendTo = new Link(sendUrl, sendUrl);

                com.kakao.sdk.template.model.Button templateBtn = new com.kakao.sdk.template.model.Button("?????? ????????????", sendTo);
                List<com.kakao.sdk.template.model.Button> listBtn = new ArrayList<>();
                listBtn.add(templateBtn);

                DefaultTemplate indvTemplate = new FeedTemplate(new Content(title, "", sendTo, description), null, listBtn);

                TalkApiClient.getInstance().sendDefaultMessage(singleUuidHolder, indvTemplate, new Function2<MessageSendResult, Throwable, Unit>() {
                    @Override
                    public Unit invoke(MessageSendResult messageSendResult, Throwable throwable) {
                        if (throwable == null) { // ?????? ??????

                            Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????!", Toast.LENGTH_SHORT).show();


                        } else { // ?????? ??????

                            Log.e(INVITE_MEMBER_TAG, throwable.getMessage());
                            System.out.println("???????????? ????????? ????????? ?????? ???????????????.");

                        }

                        return null;

                    }
                });

                singleUuidHolder.clear();
            }

            finish();

        } else {

            Toast.makeText(getApplicationContext(), "???????????? ????????? ????????? ?????????", Toast.LENGTH_SHORT).show();

        }
    }
*/
}