package moz1mozi.practiceportone.repository;

import moz1mozi.practiceportone.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
