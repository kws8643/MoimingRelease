package com.example.moimingrelease.moiming_model.view;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.moimingrelease.R;

import org.jetbrains.annotations.NotNull;

public class GroupInfoMemberViewer extends ConstraintLayout {

    private ImageView imgMemberPf;
    private TextView textMemberName;

    public GroupInfoMemberViewer(@NonNull @NotNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.view_custom_member_viewer, this);

        imgMemberPf = view.findViewById(R.id.img_group_info_member_pf);
        imgMemberPf.setClipToOutline(true);

        textMemberName = view.findViewById(R.id.text_group_info_member_name);

    }

    public void setTextUserName(String userName) {

        textMemberName.setText(userName);

    }


    public void setImgUserPfImg(String userPfImg) {

        if (!userPfImg.equals("")) {
            Glide.with(getContext()).load(userPfImg).into(imgMemberPf);
        }
    }

}
