package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.InviteKmfCheckBoxListener;
import com.example.moimingrelease.moiming_model.extras.KakaoMoimingFriendsDTO;
import com.example.moimingrelease.moiming_model.extras.MoimingMembersDTO;
import com.kakao.sdk.talk.model.Friend;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KakaoFriendRecyclerAdapter extends RecyclerView.Adapter<KakaoFriendRecyclerAdapter.KakaoFriendViewHolder> {

    private Context context;
    //    private List<Friend> moimingUserFriendList;
    private List<KakaoMoimingFriendsDTO> kmfList;

    //    private Map<String, String> invitedIdMap = new HashMap<>();
    private List<String> invitedUserUuid = new ArrayList<>();

    private List<String> groupMembersUuid;
    private InviteKmfCheckBoxListener kmfCheckBoxListener;

    private Map<String, CheckBox> checkBoxMap;


    public KakaoFriendRecyclerAdapter(Context context, List<KakaoMoimingFriendsDTO> kmfList, List<String> groupMembersUuid, InviteKmfCheckBoxListener kmfCheckBoxListener) {

        this.context = context;
        this.kmfList = kmfList;
        this.groupMembersUuid = groupMembersUuid;
        this.kmfCheckBoxListener = kmfCheckBoxListener;

        checkBoxMap = new HashMap<>();

    }

    @NonNull
    @NotNull
    @Override
    public KakaoFriendViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_user_invite_kakao, parent, false);
        KakaoFriendViewHolder itemHolder = new KakaoFriendViewHolder(view);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull KakaoFriendViewHolder holder, int position) {

        KakaoMoimingFriendsDTO kmfData = kmfList.get(position);

        MoimingMembersDTO member = kmfData.getMemberData();
        String kakaoUuid = member.getOauthUid();
        String kakaoId = kmfData.getKakaoId();

        checkBoxMap.put(member.getUuid().toString(), holder.friendIsChecked); // ?????? ??????????????? ?????? ?????? ??????

        holder.friendNameText.setText(member.getUserName()); // ????????? (????????? ????????? ????????? ????????? ?????? ???)
        Glide.with(context).load(member.getUserPfImg()).into(holder.friendRecyclerImg);

        String curUserUuid = member.getUuid().toString();
        boolean isGroupMember;

        // ?????? ???????????? ?????? ????????????????????? ????????? ????????? ??????
        if (groupMembersUuid.contains(curUserUuid)) { // ?????? ????????? ?????? ????????? ???????????? ??????????
            isGroupMember = true;
            holder.friendIsChecked.setChecked(true);
            holder.friendIsChecked.setClickable(false);
            holder.friendIsChecked.setEnabled(false);
        } else {
            isGroupMember = false;
            holder.friendIsChecked.setChecked(false);
            holder.friendIsChecked.setClickable(true);
            holder.friendIsChecked.setEnabled(true);
        }

        if (invitedUserUuid.contains(curUserUuid)) { // ?????? ????????? ?????? ?????? ???????????? ???????????? ??????????
            holder.friendIsChecked.setChecked(true);
        } else {
            if (!isGroupMember) {
                holder.friendIsChecked.setChecked(false);
            }
        }

        holder.friendIsChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox cb = (CheckBox) view;
                if (cb.isChecked()) {

                    // 1. Data Map ??? ?????????.
//                    invitedIdMap.put(kakaoId, kakaoUuid);
                    invitedUserUuid.add(member.getUuid().toString());

                    kmfCheckBoxListener.onKmfCheckBoxClick(true, member);

                } else {

                    // 1. ?????? ????????? Data Map ?????? ?????????.
//                    invitedIdMap.remove(kakaoId);
                    invitedUserUuid.remove(member.getUuid().toString());

                    kmfCheckBoxListener.onKmfCheckBoxClick(false, member);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return kmfList.size();
    }

    class KakaoFriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView friendRecyclerImg;
        private TextView friendNameText;
        private CheckBox friendIsChecked;

        public KakaoFriendViewHolder(@NotNull View itemView) {
            super(itemView);

            friendRecyclerImg = itemView.findViewById(R.id.img_friend_profile);
            friendRecyclerImg.setBackground(new ShapeDrawable(new OvalShape()));
            friendRecyclerImg.setClipToOutline(true);

            friendNameText = itemView.findViewById(R.id.text_friend_name);
            friendIsChecked = itemView.findViewById(R.id.is_friend_checked);

        }
    }


    public List<String> getInvitedIdList() {

        return this.invitedUserUuid;
    }


    public void removeMember(String memberUuid) {

        /*CheckBox userCheckBox = checkBoxMap.get(memberUuid);

        userCheckBox.performClick();
*/
        invitedUserUuid.remove(memberUuid);

        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
