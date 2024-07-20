package com.givemecon.domain.repository;

import com.givemecon.domain.entity.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.givemecon.domain.entity.member.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void saveAndFindAll() {
        // given
        String email = "test@gmail.com";
        String username = "tester";

        Member member = Member.builder()
                .email(email)
                .username(username)
                .role(USER)
                .build();

        // when
        memberRepository.save(member);
        List<Member> memberList = memberRepository.findAll();

        // then
        Member found = memberList.get(0);
        assertThat(found.getEmail()).isEqualTo(email);
        assertThat(found.getUsername()).isEqualTo(username);
        assertThat(found.getRole()).isEqualTo(USER);
    }

    @Test
    void BaseTimeEntityTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(USER)
                .build());

        // when
        List<Member> memberList = memberRepository.findAll();

        // then
        Member posts = memberList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", posts.getCreatedDate(), posts.getModifiedDate());
        assertThat(posts.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(posts.getModifiedDate()).isAfterOrEqualTo(now);
    }
}