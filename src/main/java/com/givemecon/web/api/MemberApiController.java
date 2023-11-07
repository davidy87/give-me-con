package com.givemecon.web.api;

import com.givemecon.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        return memberService.delete(id);
    }
}
