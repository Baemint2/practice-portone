package moz1mozi.practiceportone.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import moz1mozi.practiceportone.dto.PaymentCallBackRequest;
import moz1mozi.practiceportone.dto.RequestPayDto;
import moz1mozi.practiceportone.entity.Order;
import moz1mozi.practiceportone.entity.PaymentStatus;
import moz1mozi.practiceportone.repository.OrderRepository;
import moz1mozi.practiceportone.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Transactional
    public RequestPayDto findRequestDto(String orderUid) {
        Order order = orderRepository.findOrderAndPaymentAndMember(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        return RequestPayDto.builder()
                .buyerName(order.getMember().getUsername())
                .buyerEmail(order.getMember().getEmail())
                .buyerAddress(order.getMember().getAddress())
                .paymentPrice(order.getPayment().getPrice())
                .itemName(order.getItemName())
                .orderUid(orderUid)
                .build();
    }

    @Transactional
    public IamportResponse<Payment> paymentByCallBack(PaymentCallBackRequest request) {

        try {
            //결제 단건 조회(아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            //주문 내역 조회
            Order order = orderRepository.findOrderAndPayment(request.getOrderUid())
                    .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

            // 결제 완료가 아니면
            if(!iamportResponse.getResponse().getStatus().equals("paid")) {
                // 주문, 결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                throw new RuntimeException("결제 미완료");
            }

            // DB에 저장된 결제 금액
            Long price = order.getPayment().getPrice();
            // 실 결제 금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

            if(iamportPrice != price) {
                //주문, 결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                // 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                throw new RuntimeException("결제금액 위변조 의심");
            }
            //결제 상태 변경
            order.getPayment().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());

            return iamportResponse;

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
