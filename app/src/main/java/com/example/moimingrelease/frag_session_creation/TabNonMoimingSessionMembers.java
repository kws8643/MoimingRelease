package com.example.moimingrelease.frag_session_creation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.moimingrelease.InviteSessionMembersInGroupActivity;
import com.example.moimingrelease.R;

public class TabNonMoimingSessionMembers extends Fragment {

    InviteSessionMembersInGroupActivity activity;

    private LinearLayout btnMinusNmu, btnMinusFive, btnMinusTen, btnMinusFifty;
    private LinearLayout btnAddNmu, btnAddFive, btnAddTen, btnAddFifty;
    private TextView forGray;

    private TextView textCntNmu;

    public int nmuCnt = 0;

    View.OnClickListener editNmuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btn_minus_nmu:
                    if (nmuCnt > 0) {
                        nmuCnt -= 1;
                    }
                    break;
                case R.id.btn_minus_five:
                    if (nmuCnt > 5) {
                        nmuCnt -= 5;
                    }
                    break;
                case R.id.btn_minus_ten:
                    if (nmuCnt > 10) {
                        nmuCnt -= 10;
                    }
                    break;
                case R.id.btn_minus_fifty:
                    if (nmuCnt > 50) {
                        nmuCnt -= 50;
                    }
                    break;
                case R.id.btn_add_nmu:
                    if (nmuCnt == 0) {

                        btnMinusNmu.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_add_non_moiming_oval, null));
                        forGray.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minus, null));

                        // 뷰어 생성
                        activity.initNmuViewer();
                    }

                    if (nmuCnt < 99) {
                        nmuCnt += 1;
                    }

                    break;
                case R.id.btn_add_five:
                    if (nmuCnt == 0) {

                        btnMinusNmu.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_add_non_moiming_oval, null));
                        forGray.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minus, null));

                        // 뷰어 생성
                        activity.initNmuViewer();
                    }
                    if (nmuCnt < 95) {
                        nmuCnt += 5;
                    }
                    break;
                case R.id.btn_add_ten:
                    if (nmuCnt == 0) {

                        btnMinusNmu.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_add_non_moiming_oval, null));
                        forGray.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minus, null));

                        // 뷰어 생성
                        activity.initNmuViewer();
                    }
                    if (nmuCnt < 90) {
                        nmuCnt += 10;
                    }
                    break;
                case R.id.btn_add_fifty:
                    if (nmuCnt == 0) {

                        btnMinusNmu.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_add_non_moiming_oval, null));
                        forGray.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minus, null));

                        // 뷰어 생성
                        activity.initNmuViewer();
                    }
                    if (nmuCnt < 49) {
                        nmuCnt += 50;
                    }
                    break;
            }

            if (nmuCnt == 0) {

                btnMinusNmu.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_add_non_moiming_oval_gray, null));
                forGray.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minus_gray, null));

                // 뷰어 제거
                activity.removeNmuViewer();

            }

            changeCntText();

            // 뷰어 수정
            activity.changeViewerNmuCnt(nmuCnt);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_invite_non_moiming_user, container, false);

        initView(view);

        initParams();

        checkPrevNmu();

        return view;
    }


    private void initView(View view) {

        btnMinusNmu = view.findViewById(R.id.btn_minus_nmu);
        forGray = view.findViewById(R.id.forGray);
        btnMinusNmu.setOnClickListener(editNmuListener);
        btnMinusFive = view.findViewById(R.id.btn_minus_five);
        btnMinusFive.setOnClickListener(editNmuListener);
        btnMinusTen = view.findViewById(R.id.btn_minus_ten);
        btnMinusTen.setOnClickListener(editNmuListener);
        btnMinusFifty = view.findViewById(R.id.btn_minus_fifty);
        btnMinusFifty.setOnClickListener(editNmuListener);

        btnAddNmu = view.findViewById(R.id.btn_add_nmu);
        btnAddNmu.setOnClickListener(editNmuListener);
        btnAddFive = view.findViewById(R.id.btn_add_five);
        btnAddFive.setOnClickListener(editNmuListener);
        btnAddTen = view.findViewById(R.id.btn_add_ten);
        btnAddTen.setOnClickListener(editNmuListener);
        btnAddFifty = view.findViewById(R.id.btn_add_fifty);
        btnAddFifty.setOnClickListener(editNmuListener);

        textCntNmu = view.findViewById(R.id.text_cnt_nmu);

    }

    private void initParams() {

        activity = (InviteSessionMembersInGroupActivity) getActivity();


    }

    private void changeCntText() {

        String change
                = nmuCnt + " 명";

        textCntNmu.setText(change);

    }

    public void clickMinusNmuBtn(){

        btnMinusNmu.performClick();
    }


    private void checkPrevNmu() {

        int prevNmuCnt = activity.getPrevNmu();

        if (prevNmuCnt != 0) {

            nmuCnt = prevNmuCnt;

            btnMinusNmu.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_add_non_moiming_oval, null));
            forGray.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minus, null));

            activity.initNmuViewer();
            activity.changeViewerNmuCnt(nmuCnt);
            textCntNmu.setText(nmuCnt + " 명");
        }

    }

}
