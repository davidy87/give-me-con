package com.givemecon.controller.admin;

import com.givemecon.application.service.MemberService;
import com.givemecon.common.auth.dto.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.application.dto.MemberDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@RestController
public class AdminMemberController {

    private final MemberService memberService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public SignupResponse adminSignup(@RequestBody SignupRequest signupRequest) {
        return memberService.signup(signupRequest);
    }

    @PostMapping("/login")
    public TokenInfo adminLogin(@RequestBody LoginRequest loginRequest) {
        return memberService.adminLogin(loginRequest);
    }
}
