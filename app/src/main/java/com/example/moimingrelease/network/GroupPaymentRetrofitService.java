package com.example.moimingrelease.network;

import com.example.moimingrelease.moiming_model.extras.PaymentAndSenderDTO;
import com.example.moimingrelease.moiming_model.request_dto.GroupPaymentRequestDTO;
import com.example.moimingrelease.moiming_model.request_dto.MoimingGroupRequestDTO;
import com.example.moimingrelease.moiming_model.response_dto.GroupPaymentResponseDTO;
import com.example.moimingrelease.moiming_model.response_dto.MoimingGroupResponseDTO;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GroupPaymentRetrofitService {

    @POST("api/payment/create")
    Observable<TransferModel<GroupPaymentResponseDTO>> paymentCreate(@Body TransferModel<PaymentAndSenderDTO> requestModel);

    @GET("api/payment/{uuid}")
    Observable<TransferModel<List<GroupPaymentResponseDTO>>> getGroupPayments(@Path("uuid") String groupUuid);

    @PUT("api/payment/update")
    Observable<TransferModel<GroupPaymentResponseDTO>> updatePayment(@Body TransferModel<PaymentAndSenderDTO> requestModel, @Path("uuid") String paymentUuid);

    @DELETE("api/payment/{uuid}")
    Observable<TransferModel> deleteGroupPayment(@Path("uuid") String paymentUuid);
}
