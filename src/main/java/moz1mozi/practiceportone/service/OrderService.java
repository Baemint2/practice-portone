package moz1mozi.practiceportone.service;

import lombok.RequiredArgsConstructor;
import moz1mozi.practiceportone.entity.Member;
import moz1mozi.practiceportone.entity.Order;
import moz1mozi.practiceportone.entity.Payment;
import moz1mozi.practiceportone.entity.PaymentStatus;
import moz1mozi.practiceportone.repository.OrderRepository;
import moz1mozi.practiceportone.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Order autoOrder(Member member) {

        //임시 결제내역 생성
        Payment payment = Payment.builder()
                .price(100L)
                .status(PaymentStatus.READY)
                .build();

        paymentRepository.save(payment);

        // 주문 생성
        Order order = Order.builder()
                .member(member)
                .price(100L)
                .itemName("1달러샵 상품")
                .orderUid(UUID.randomUUID().toString())
                .payment(payment)
                .build();
        return orderRepository.save(order);
    }
}
