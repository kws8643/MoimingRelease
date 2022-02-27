package com.example.moimingrelease.moiming_model.view;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomUserViewer extends ConstraintLayout implements Serializable {

    // 리스너 같은건..?
    private ImageView imgUserPfImg, btnRemoveUser;
    private TextView textUserName;

    public CustomUserViewer(Context context, boolean isRemoverOn, View.OnClickListener removerListener) {
        super(context);
        init(context, isRemoverOn, removerListener);
    }

    private void init(Context context, boolean isRemoverOn, View.OnClickListener removerListener) {

        View view = View.inflate(context, R.layout.view_custom_user_viewer, this);

        imgUserPfImg = findViewById(R.id.img_user_pf_img);
        imgUserPfImg.setBackground(new ShapeDrawable(new OvalShape()));
        imgUserPfImg.setClipToOutline(true);

        btnRemoveUser = findViewById(R.id.btn_remove_user);
        textUserName = findViewById(R.id.text_user_name);

        if(isRemoverOn){
            btnRemoveUser.setOnClickListener(removerListener);
        }else{
            disableRemoverBtn();
        }

    }

    public void setTextUserName(String userName) {

        textUserName.setText(userName);

    }


    public void setImgUserPfImg(String userPfImg) {

        if (!userPfImg.equals("")) {
            Glide.with(getContext()).load(userPfImg).into(imgUserPfImg);
        }
    }

    public void disableRemoverBtn() {

        btnRemoveUser.setVisibility(View.GONE);
    }


    public void setRemoveBtnListener(View.OnClickListener removeBtnListener){

        btnRemoveUser.setOnClickListener(removeBtnListener);
    }


}
