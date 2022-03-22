package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

public class UserInfoActivity extends AppCompatActivity {

    private MoimingUserVO curUser;

    private ImageView imgUserPf;
    private TextView textName, textPhone, textEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        receiveIntent();

        initView();

        initParams();

    }


    private void receiveIntent() {

        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            curUser = (MoimingUserVO) receivedIntent.getExtras().getSerializable(getResources().getString(R.string.moiming_user_data_key));
        }
    }

    private void initView() {

        imgUserPf = findViewById(R.id.img_user_info_pf);
        imgUserPf.setBackground(new ShapeDrawable(new OvalShape()));
        imgUserPf.setClipToOutline(true);
        assert curUser.getUserPfImg() != null;
        setImgUserPfImg(curUser.getUserPfImg());

        textName = findViewById(R.id.text_user_info_name);
        textName.setText(curUser.getUserName());
        textPhone = findViewById(R.id.text_user_info_phone);
        textPhone.setText(curUser.getPhoneNumber());
        textEmail = findViewById(R.id.text_user_info_email);
        textEmail.setText(curUser.getUserEmail());


    }

    private void initParams() {
    }

    public void setImgUserPfImg(String userPfImg) {

        if (!userPfImg.equals("")) {
            Glide.with(this).load(userPfImg).into(imgUserPf);
        }
    }
}