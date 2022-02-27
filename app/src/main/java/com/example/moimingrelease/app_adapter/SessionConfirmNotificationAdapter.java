package com.example.moimingrelease.app_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moimingrelease.R;
import com.example.moimingrelease.app_listener_interface.SessionCreatorNotificationCallBack;
import com.example.moimingrelease.moiming_model.extras.NotificationUserAndActivityDTO;
import com.example.moimingrelease.moiming_model.extras.ReceivedNotificationDTO;
import com.example.moimingrelease.moiming_model.response_dto.NotificationResponseDTO;
import com.example.moimingrelease.network.GlobalRetrofit;
import com.example.moimingrelease.network.NotificationRetrofitService;
import com.example.moimingrelease.network.TransferModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SessionConfirmNotificationAdapter extends RecyclerView.Adapter<SessionConfirmNotificationAdapter.SessionConfirmNotificationViewHolder> {

    private Context context;
    private List<ReceivedNotificationDTO> notificationList;
    private SessionCreatorNotificationCallBack confirmCallback;
    private Map<UUID, CheckBox> checkBoxMap;
    private UUID sessionUuid;

    public SessionConfirmNotificationAdapter(Context context, List<ReceivedNotificationDTO> notificationList
            , SessionCreatorNotificationCallBack confirmCallback, UUID sessionUuid) {

        this.context = context;
        this.notificationList = notificationList;
        this.confirmCallback = confirmCallback;
        this.sessionUuid = sessionUuid;

        checkBoxMap = new HashMap<>();

    }

    @NonNull
    @NotNull
    @Override
    public SessionConfirmNotificationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.view_session_confirm_notification, parent, false);

        SessionConfirmNotificationViewHolder itemHolder = new SessionConfirmNotificationViewHolder(itemView);

        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SessionConfirmNotificationViewHolder holder, int position) {

        ReceivedNotificationDTO data = notificationList.get(position);

        String sentUser = data.getSentUserName();
        NotificationResponseDTO notificationData = data.getNotification();
        UUID sentUserUuid = notificationData.getSentUserUuid();

        String notiMsg = notificationData.getMsgText();
        String showText = sentUser + "님이 " + notiMsg;

        holder.textMsg.setText(showText);

        checkBoxMap.put(sentUserUuid, holder.isSentConfirmed);
        holder.isSentConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox cb = (CheckBox) v;

                if (!cb.isChecked()) { // 체크 해제시

                    confirmCallback.changingUserState(sentUserUuid, false, true);

                } else { // 체크시

                    confirmCallback.changingUserState(sentUserUuid, true, true);

                }
            }
        });


        holder.btnCancelNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationUserAndActivityDTO uuidData = new NotificationUserAndActivityDTO(null, sentUserUuid, sessionUuid);

                // 그냥 가서 해당 Notification 삭제하면 저쪽에서 알아서 curUserStatus 가 변경됨.
                NotificationRetrofitService notiRetrofit = GlobalRetrofit.getInstance().getRetrofit().create(NotificationRetrofitService.class);
                notiRetrofit.deleteNotification("session"
                        , String.valueOf(2)
                        , uuidData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TransferModel>() {
                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull TransferModel transferModel) {

                                if(transferModel.getResultCode()!= 404){

                                    confirmCallback.deleteNotification(sentUserUuid);

                                    Toast.makeText(context.getApplicationContext(), "정산 확인 요청을 거절하였습니다", Toast.LENGTH_SHORT).show();


                                }else{
                                    Toast.makeText(context.getApplicationContext(), "요청을 수행할 수 없습니다", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                if(e.getMessage() != null){

                                    Log.e("Delete Notification Error:: ", e.getMessage());
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });

    }

    public void checkConfirmingUser(List<UUID> stateChangedUserUuid) {

        Iterator<UUID> iterator = checkBoxMap.keySet().iterator();

        while (iterator.hasNext()) {

            UUID userUuid = iterator.next();
            if (stateChangedUserUuid.contains(userUuid)) {
                checkBoxMap.get(userUuid).setChecked(true);
            } else {
                checkBoxMap.get(userUuid).setChecked(false);
            }

        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class SessionConfirmNotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView textMsg;
        private CheckBox isSentConfirmed;
        private ImageView btnCancelNoti;

        public SessionConfirmNotificationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textMsg = itemView.findViewById(R.id.text_notification_msg);
            isSentConfirmed = itemView.findViewById(R.id.checkbox_confirm_sent);
            btnCancelNoti = itemView.findViewById(R.id.btn_cancel_notification);

        }
    }
}
