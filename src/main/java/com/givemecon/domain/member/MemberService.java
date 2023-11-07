package com.givemecon.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Long delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        memberRepository.delete(member);

        return id;
    }
}
