package com.givemecon.controller;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.domain.member.MemberDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/signup")
    public SignupResponse adminSignup(@RequestBody SignupRequest signupRequest) {
        return memberService.signup(signupRequest);
    }

    @PostMapping("/admin/login")
    public TokenInfo adminLogin(@RequestBody LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        memberService.delete(id);
    }
}
