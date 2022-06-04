package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.moiming_model.moiming_vo.AppNoticeVO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyPageAppNoticeAdapter extends RecyclerView.Adapter<MyPageAppNoticeAdapter.AppNoticeViewHolder> {

    private Context mContext;
    private List<AppNoticeVO> noticeDataList;

    public MyPageAppNoticeAdapter(Context mContext, List<AppNoticeVO> noticeDataList) {

        this.mContext = mContext;
        this.noticeDataList = noticeDataList;

    }

    @NonNull
    @NotNull
    @Override
    public AppNoticeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_app_notice_item, parent, false);

        return new AppNoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AppNoticeViewHolder holder, int position) {

        AppNoticeVO singleNotice = noticeDataList.get(position);

        holder.textTitle.setText(singleNotice.getNoticeTitle());
        holder.textInfo.setText(singleNotice.getNoticeInfo());

    }

    @Override
    public int getItemCount() {
        return noticeDataList.size();
    }

    class AppNoticeViewHolder extends RecyclerView.ViewHolder {

        private TextView textTitle, textInfo;

        public AppNoticeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.text_notice_title);
            textInfo = itemView.findViewById(R.id.text_notice_info);
        }
    }
}
