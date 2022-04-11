package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.SessionCreationPreviousFundingActivity;
import com.example.moimingrelease.app_listener_interface.ViewPrevSessionListener;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingSessionVO;
import com.example.moimingrelease.moiming_model.moiming_vo.MoimingUserVO;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PreviousFundingViewAdapter extends RecyclerView.Adapter<PreviousFundingViewAdapter.PreviousFundingHolder>{

    private Context context;
    private List<MoimingSessionVO> prevFundingDataList;
    private ViewPrevSessionListener sessionListener;


    public PreviousFundingViewAdapter(Context context, List<MoimingSessionVO> prevFundingDataList, ViewPrevSessionListener sessionListener){

        this.context = context;
        this.prevFundingDataList = prevFundingDataList;
        this.sessionListener = sessionListener;

    }

    @NonNull
    @NotNull
    @Override
    public PreviousFundingHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View viewHolder = LayoutInflater.from(context).inflate(R.layout.view_prev_funding_viewer, parent, false);

        return new PreviousFundingHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PreviousFundingHolder holder, int position) {

        MoimingSessionVO sessionVO = prevFundingDataList.get(position);

        holder.textName.setText(sessionVO.getSessionName());
        holder.textCost.setText(String.valueOf(sessionVO.getSingleCost()));
        holder.textDate.setText(sessionVO.getCreatedAt().format(DateTimeFormatter.ofPattern("yy.MM.dd")));

        holder.btnSelectFunding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionListener.moveToViewSession(sessionVO);
            }
        });

    }

    @Override
    public int getItemCount() {
        return prevFundingDataList.size();
    }

    class PreviousFundingHolder extends RecyclerView.ViewHolder {

        private TextView textDate, textName, textCost;
        private ConstraintLayout btnSelectFunding;

        public PreviousFundingHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textDate = itemView.findViewById(R.id.text_funding_date);
            textCost = itemView.findViewById(R.id.text_funding_cost);
            textName = itemView.findViewById(R.id.text_funding_name);
            btnSelectFunding = itemView.findViewById(R.id.btn_select_funding);

        }

    }
}
