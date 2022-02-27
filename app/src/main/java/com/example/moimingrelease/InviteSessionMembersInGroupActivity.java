package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moimingrelease.app_listener_interface.UserCheckBoxListener;
import com.example.moimingrelease.frag_session_creation.TabNonMoimingSessionMembers;
import com.example.moimingrelease.frag_session_creation.TabSessionMembersInGroupFragment;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.example.moimingrelease.moiming_model.extras.KakaoMoimingFriendsDTO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;
import com.example.moimingrelease.moiming_model.view.CustomUserViewer;
import com.example.moimingrelease.network.kakao_api.KakaoMoimingFriends;
import com.google.android.material.tabs.TabLayout;
import com.kakao.sdk.talk.model.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 그룹에서 정산활동을 생성할 때

/**
 * 이 액티비티 FLOW /
 * 정산 생성에서 들어오게 되어 있음 / 현 UserVO / 현 GroupVO 정보를 가지고 들어와야 함.
 * <p>
 * 1. Activity Create
 * 1) 현 User 를 위에 띄워놓고 총 1 명
 * 2) 그룹원 탭 자동선택 및 Frame Layout 에 담그기.
 * 3) FrameLayout 내부 RecyclerView init.
 * 4) 초대 예정인 회원들 Map 을 만든다.
 * <p>
 * 2. Recycler 선택시 / 해제 시
 * 1) 초대할 멤버 리스트에 추가 / 제거 된다. -> 누가 제거인지 알기 위해 Map을 사용한다. (User uuid / 아무거나)
 * 2) (아답터) 추가 / 제거 액션
 * --> 리스너 내부 1) context로 접근) ISM 액티비티 Map의 추가 제거 함수 활용
 * 2) 전송용 Map 에 추가 제거하고 뷰용 자료구조 객체에서 제거해준다.(CustomViewer)
 * -->          3) CustomViwer 모아 둔곳에서 제거 하고, 뷰에서도 제거해준다 (해당 뷰를 찾아서 제거..? or Reset?)
 **/


public class InviteSessionMembersInGroupActivity extends AppCompatActivity {

    private ConstraintLayout nmuInviteViewer;
    private TextView textNmuCnt;
    private ImageView btnRemoveNmuViewer;

    KakaoMoimingFriends kmf = KakaoMoimingFriends.getInstance();
    private List<KakaoMoimingFriendsDTO> kmfList;
    private List<Friend> moimingUserFriendsList; // 리사이클러뷰에 전달하는 데이터 (모이밍 사용 중인 친구들)

    private HorizontalScrollView inGroupInviteScroll;
    private LinearLayout layoutUsersHolder;
    private ConstraintLayout aboutNmu;
    private TextView textInviteCnt;
    private Button btnInviteMembers;
    private TabLayout inviteTab;
    private int cnt = 1; // 전체 몇 명인지
    private int prevNmu;
    private boolean fromNewGroupCreation;

    // 탭에 의해 나눠지는 프레그먼트
    TabSessionMembersInGroupFragment tabGroupMember;
    TabNonMoimingSessionMembers tabNmuMember;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;


    MoimingUserVO curUser;
    List<MoimingMembersDTO> curGroupMembers; // 일반용.
    List<MoimingMembersDTO> kakaoMoimingFriends; // 카카오 전체 친구들 초기화 용
    List<MoimingMembersDTO> sessionInvitedMembers;

    Map<String, MoimingMembersDTO> invitingMembersMap; // 정산을 만드는 유저는 이 맵에 없음!
    Map<String, CustomUserViewer> userViewerMap; // 지속적인 업데이트를 위해서!

    private TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (tab.getPosition()) {
                case 0:

                    transaction.hide(tabNmuMember);
                    transaction.show(tabGroupMember);
                    transaction.commit();

                    aboutNmu.setVisibility(View.GONE);

                    break;
                case 1:

                    transaction.hide(tabGroupMember);
                    transaction.show(tabNmuMember);
                    transaction.commit();

                    aboutNmu.setVisibility(View.VISIBLE);

                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


    public UserCheckBoxListener checkBoxListener = new UserCheckBoxListener() {
        @Override
        public void onCheckBoxClick(boolean isAdded, MoimingMembersDTO member) {

            if (isAdded) { //  추가되는 거면.

                //1. 맵에 추가.
                invitingMembersMap.put(member.getUuid().toString(), member);

                //2. 위에 호리잔털 뷰 업데이트
                addViewInHorizontalScroll(member);

                //3. 총 x 명 변경
                cnt += 1;
                String text = "총 " + String.valueOf(cnt) + "명";
                textInviteCnt.setText(text);

            } else {

                //1. 맵에서 제거.
                invitingMembersMap.remove(member.getUuid().toString());

                //2. 호리전털 뷰에서 제거.
                CustomUserViewer thisUserViewer = userViewerMap.get(member.getUuid().toString());
                thisUserViewer.setVisibility(View.GONE);
                thisUserViewer = null;

                //3. 호리전털 맵에서 제거.
                userViewerMap.remove(member.getUuid().toString());

                //4. 총 x 명 변경
                cnt -= 1;
                String text = "총 " + String.valueOf(cnt) + "명";
                textInviteCnt.setText(text);


            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_session_members_in_group);

        receiveIntent();

        initView();

        initParams();


        // 1) 현 User 를 위에 띄워놓고 총 1 명
        // 현 유저는 맵에 포함시키지 않는다. 따라서 항상 실행.
        CustomUserViewer curUserViewer = new CustomUserViewer(this, false, null);
        curUserViewer.setTextUserName(curUser.getUserName());
        curUserViewer.setImgUserPfImg(curUser.getUserPfImg());
        layoutUsersHolder.addView(curUserViewer);
        giveMarginInScrollView(curUserViewer);


        transaction.add(R.id.frame_session_creation_in_group, tabGroupMember)
                .add(R.id.frame_session_creation_in_group, tabNmuMember).hide(tabNmuMember).commit();


        btnInviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 현재 담긴 CustomViewer 맵을 둘다 기존 액티비티로 반환한다.
                Intent invitedMembersData = new Intent();

                if (cnt != 0) {
                    invitedMembersData.putExtra(SessionCreationActivity.INVITED_NMU_CNT, prevNmu);
                }
                /*String nmuCnt = textNmuCnt.getText().toString().substring(1);
                int sendNmuCnt = Integer.parseInt(nmuCnt);*/
                invitedMembersData.putParcelableArrayListExtra(SessionCreationActivity.INVITED_MEMBER_DATA_KEY, new ArrayList<>(invitingMembersMap.values()));

                setResult(RESULT_OK, invitedMembersData);

                finish();

            }
        });


        btnRemoveNmuViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tabNmuMember.clickMinusNmuBtn();

            }
        });


    }

    private void receiveIntent() {

        Intent dataIntent = getIntent();

        fromNewGroupCreation = dataIntent.getExtras().getBoolean(getResources().getString(R.string.session_creation_with_new_group_flag), false);

        curUser = (MoimingUserVO) dataIntent.getExtras().getSerializable(MainActivity.MOIMING_USER_DATA_KEY);
        prevNmu = dataIntent.getExtras().getInt(SessionCreationActivity.INVITED_NMU_CNT);
        sessionInvitedMembers = dataIntent.getExtras().getParcelableArrayList(SessionCreationActivity.INVITED_MEMBER_DATA_KEY);

        cnt += prevNmu; // 비모이밍 유저들이 몇 명인지 추가한다

        if (!fromNewGroupCreation) {

            curGroupMembers = dataIntent.getExtras().getParcelableArrayList(GroupActivity.MOIMING_GROUP_MEMBERS_KEY);

        } else {

            kmfList = kmf.getKmfList();

        }
    }


    private void initView() {

        nmuInviteViewer = findViewById(R.id.viewer_non_moiming_member);
        textNmuCnt = findViewById(R.id.text_nmu_cnt);
        btnRemoveNmuViewer = findViewById(R.id.btn_remove_nmu_user);

        inGroupInviteScroll = findViewById(R.id.horizontal_scroll_in_group_invite);
        layoutUsersHolder = findViewById(R.id.layout_users_holder);
        aboutNmu = findViewById(R.id.about_nmu);

        textInviteCnt = findViewById(R.id.text_invite_cnt);
        textInviteCnt.setText("총 " + cnt + "명");

        btnInviteMembers = findViewById(R.id.btn_update_invite_members);
        inviteTab = findViewById(R.id.tab_layout_invite_in_group);
    }

    private void initParams() {

        tabGroupMember = new TabSessionMembersInGroupFragment();
        tabNmuMember = new TabNonMoimingSessionMembers();

        inviteTab.addOnTabSelectedListener(tabListener);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        invitingMembersMap = new HashMap<>();
        userViewerMap = new HashMap<>();

        if (fromNewGroupCreation) {
            kakaoMoimingFriends = new ArrayList<>();
        }

    }


    // 체크박스가 체킹되면 해당 메소드 실행
    private void addViewInHorizontalScroll(MoimingMembersDTO member) {

        CustomUserViewer userViewer = new CustomUserViewer(this, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // - Group 멤버에서 해제
                tabGroupMember.userRemoveBtnClicked(member.getUuid().toString());

                //1. 맵에서 제거.
                invitingMembersMap.remove(member.getUuid().toString());

                //2. 호리전털 뷰에서 제거.
                CustomUserViewer thisUserViewer = userViewerMap.get(member.getUuid().toString());
                thisUserViewer.setVisibility(View.GONE);
                thisUserViewer = null;

                //3. 호리전털 맵에서 제거.
                userViewerMap.remove(member.getUuid().toString());

                //4. 총 x 명 변경
                cnt -= 1;
                String text = "총 " + String.valueOf(cnt) + "명";
                textInviteCnt.setText(text);

            }
        });


        userViewer.setTextUserName(member.getUserName());
        userViewer.setImgUserPfImg(member.getUserPfImg());

        layoutUsersHolder.addView(userViewer);
        userViewerMap.put(member.getUuid().toString(), userViewer);

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


    public List<MoimingMembersDTO> getCurGroupMembers() {

        return this.curGroupMembers;
    }


    public List<MoimingMembersDTO> getKmfMembers() {

        List<MoimingMembersDTO> formattedList = new ArrayList<>();

        for (int i = 0; i < kmfList.size(); i++) {

            formattedList.add(kmfList.get(i).getMemberData());
        }

        return formattedList;
    }


    public boolean getFromNewGroupCreation() {

        return this.fromNewGroupCreation;

    }

    public UserCheckBoxListener getCheckBoxListener() {

        return this.checkBoxListener;
    }


    public List<String> getPreInvitedMembersUuid() {

        List<String> membersUuid = new ArrayList<>();

        for (MoimingMembersDTO member : sessionInvitedMembers) {

            membersUuid.add(member.getUuid().toString());
        }

        return membersUuid;
    }


    public void initNmuViewer() {

        nmuInviteViewer.setVisibility(View.VISIBLE);
    }

    public void removeNmuViewer() {

        nmuInviteViewer.setVisibility(View.GONE);
    }

    public void changeViewerNmuCnt(int nmuCnt) { // nmuCnt = 1

        textNmuCnt.setText("+" + String.valueOf(nmuCnt)); // +1 로

        int changer = nmuCnt - prevNmu; // changer = 1-0 = 1
        prevNmu = nmuCnt; // prevNmu = 1;

        cnt += changer;
        textInviteCnt.setText("총 " + cnt + "명");

    }

    public int getPrevNmu() {

        return this.prevNmu;
    }
}