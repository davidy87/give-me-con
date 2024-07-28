package com.givemecon.controller.admin;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.application.service.MemberService;
import com.givemecon.common.auth.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@RestController
public class AdminMemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public MemberDto.SignupResponse adminSignup(@RequestBody MemberDto.SignupRequest signupRequest) {
        return memberService.signup(signupRequest);
    }

    @PostMapping("/login")
    public TokenInfo adminLogin(@RequestBody MemberDto.LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }
}
