package com.givemecon.domain.repository;

import com.givemecon.common.configuration.JpaConfig;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    Member buyer;

    @Test
    void saveAndFind() {
        // given
        Order order = new Order("ORDER-NUMBER", buyer);

        // when
        Order saved = orderRepository.save(order);
        Optional<Order> found = orderRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    void findByOrderNumber() {
        // given
        Order order = orderRepository.save(new Order("ORDER-NUMBER", buyer));

        // when
        Optional<Order> found = orderRepository.findByOrderNumber(order.getOrderNumber());

        // then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(order);
    }
}