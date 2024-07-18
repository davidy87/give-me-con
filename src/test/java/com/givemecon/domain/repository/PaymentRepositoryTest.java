package com.givemecon.domain.repository;

import com.givemecon.domain.entity.payment.OrderInfo;
import com.givemecon.domain.entity.payment.Payment;
import com.givemecon.domain.entity.payment.PaymentMethod;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void saveAndFind() {
        // given
        OrderInfo orderInfo = new OrderInfo("order-id", "Americano T", 4_000L);

        Payment payment = Payment.builder()
                .paymentKey("paymentKey")
                .method(PaymentMethod.CARD)
                .receiptUrl("receiptUrl")
                .orderInfo(orderInfo)
                .build();

        // when
        paymentRepository.save(payment);
        List<Payment> found = paymentRepository.findAll();

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0)).isEqualTo(payment);
    }
}