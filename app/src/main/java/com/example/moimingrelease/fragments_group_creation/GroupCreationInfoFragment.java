package com.example.moimingrelease.fragments_group_creation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.moimingrelease.GroupCreationActivity;
import com.example.moimingrelease.R;
import com.google.android.material.textfield.TextInputEditText;

public class GroupCreationInfoFragment extends Fragment {

    GroupCreationActivity groupCreationActivity;

    private TextInputEditText inputGroupName, inputGroupInfo;
    private ImageView btnCancel;
    private Button btnNext;
    private LinearLayout btnAddBgImg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_group_creation_info, container, false);

        initView(view);

        initParams();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!inputGroupName.getText().toString().equals("")) {

                    Bundle fragData = new Bundle();
                    fragData.putString("group_name", inputGroupName.getText().toString());

                    if(inputGroupInfo.getText() != null){


                    }else{ // group_info 입력이 없으면 ""으로 처리. Null 방지.

                        fragData.putString("group_info", "");

                    }


                    // TODO BG IMG 에 대한 정보도 전달되어야 함.


//                    groupCreationActivity.changeFragment(GroupCreationActivity.FRAGMENT_MEMBERS_INDEX, fragData);

                }else{


                    Toast.makeText(groupCreationActivity.getApplicationContext(), "그룹명을 입력하세요", Toast.LENGTH_SHORT).show();

                }


            }
        });

        inputGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().trim().length()>0){

                    btnNext.setClickable(true);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.moimingTheme));

                }else{

                    btnNext.setClickable(false);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.moimingLightTheme));

                }
            }
        });

        return view;

    }

    private void initView(View view) {

        inputGroupName = view.findViewById(R.id.input_group_name);
        inputGroupInfo = view.findViewById(R.id.input_group_info);

        btnCancel = view.findViewById(R.id.btn_cancel_group_creation);
        btnNext = view.findViewById(R.id.btn_group_creation_next);
        btnAddBgImg = view.findViewById(R.id.btn_add_bg_img);


    }

    private void initParams() {

        groupCreationActivity = (GroupCreationActivity) getActivity();

    }
}
