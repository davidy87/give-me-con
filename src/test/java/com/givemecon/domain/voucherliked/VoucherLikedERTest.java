package com.givemecon.domain.voucherliked;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class VoucherLikedERTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherLikedRepository voucherLikedRepository;

    @Test
    void voucherLiked() {
        // given
        Member member = Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucher = Voucher.builder()
                .title("Americano T")
                .price(15_000L)
                .image("americano.jpg")
                .build();

        VoucherLiked voucherLiked = new VoucherLiked();

        Member memberSaved = memberRepository.save(member);
        Voucher voucherSaved = voucherRepository.save(voucher);
        VoucherLiked voucherLikedSaved = voucherLikedRepository.save(voucherLiked);

        // when
        voucherLikedSaved.setMember(memberSaved);
        voucherLikedSaved.setVoucher(voucherSaved);
        List<VoucherLiked> voucherLikedList = voucherLikedRepository.findAll();

        // then
        VoucherLiked found = voucherLikedList.get(0);
        assertThat(found.getMember()).isEqualTo(memberSaved);
        assertThat(found.getVoucher()).isEqualTo(voucherSaved);
    }
}
