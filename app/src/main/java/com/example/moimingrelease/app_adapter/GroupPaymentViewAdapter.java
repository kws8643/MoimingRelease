package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.PaymentSettingDialogListener;
import com.example.moimingrelease.moiming_model.dialog.PaymentLongTouchDialog;
import com.example.moimingrelease.moiming_model.extras.PaymentViewData;
import com.example.moimingrelease.moiming_model.moiming_vo.GroupPaymentVO;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class GroupPaymentViewAdapter extends RecyclerView.Adapter<GroupPaymentViewAdapter.PaymentViewHolder> {

    private Context context;
    private List<PaymentViewData> paymentList;
    private PaymentSettingDialogListener paymentDialogListener;

    public GroupPaymentViewAdapter(Context context, List<PaymentViewData> paymentList, PaymentSettingDialogListener paymentDialogListener) {

        this.context = context;
        this.paymentList = paymentList;
        this.paymentDialogListener = paymentDialogListener;

    }

    @NonNull
    @NotNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.view_payment_item, parent, false);
        PaymentViewHolder viewHolder = new PaymentViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PaymentViewHolder holder, int position) {

        PaymentViewData paymentViewData = paymentList.get(position);
        GroupPaymentVO paymentData = paymentViewData.getPaymentData();

        holder.textName.setText(paymentData.getPaymentName());
        holder.textDate.setText(transferDateToText(paymentData.getPaymentDate()));

        String calculatedCost = AppExtraMethods.moneyToWonWon(paymentViewData.getCalculatedCost()) + "원";
        holder.textCal.setText(calculatedCost);

        String paymentCost = "";

        if (paymentData.getPaymentType()) { // 수입

            paymentCost += "+";
            holder.textCost.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.textBlue, null));

        } else { //지출

            paymentCost += "-";
            holder.textCost.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.textRed, null));

        }

        paymentCost += AppExtraMethods.moneyToWonWon(paymentData.getPaymentCost()) + "원";
        holder.textCost.setText(paymentCost);

        holder.layoutPayment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PaymentLongTouchDialog paymentDialog = new PaymentLongTouchDialog(context, paymentDialogListener, paymentData);
                paymentDialog.show();

                return false;
            }
        });


    }


    private String transferDateToText(LocalDate date) {

        String year = String.valueOf(date.getYear());

        String month = "0";

        int intMonth = date.getMonthValue();

        if (intMonth < 10) {
            month += String.valueOf(intMonth);
        } else {
            month = String.valueOf(intMonth);
        }

        String day = "0";

        int intDayOfMonth = date.getDayOfMonth();

        if (intDayOfMonth < 10) {
            day += String.valueOf(intDayOfMonth);
        } else {
            day = String.valueOf(intDayOfMonth);
        }

        String dateText = year.substring(2) + "." + month + "." + day + " ";

        return dateText;

    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout layoutPayment;

        private TextView textName, textCost, textDate, textCal;


        public PaymentViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);

            layoutPayment = itemView.findViewById(R.id.layout_payment_item);
            textName = itemView.findViewById(R.id.text_payment_item_name);
            textCost = itemView.findViewById(R.id.text_payment_cost);
            textDate = itemView.findViewById(R.id.text_payment_item_date);
            textCal = itemView.findViewById(R.id.text_payment_cal);

        }
    }
}
