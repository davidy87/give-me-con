package com.givemecon.web.api;

import com.givemecon.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        memberService.delete(id);
    }
}
