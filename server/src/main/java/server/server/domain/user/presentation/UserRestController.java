package server.server.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.server.domain.user.presentation.dto.request.LoginRequest;
import server.server.domain.user.presentation.dto.request.SignupRequest;
import server.server.domain.user.service.AtkRefreshService;
import server.server.domain.user.service.LoginService;
import server.server.domain.user.service.SignupService;
import server.server.global.security.jwt.dto.AtkResponse;
import server.server.global.security.jwt.dto.TokenResponse;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "User API", description = "User에 대한 API입니다.")
public class UserRestController {
    private final SignupService signupService;
    private final LoginService loginService;
    private final AtkRefreshService atkRefreshService;

    @Operation(summary = "회원가입", description = "유저 회원가입을 해주는 API")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        signupService.execute(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();  // 201 Created, Body 없음
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse response = loginService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "토큰 재발급", description = "atk 만료시 rtk로 atk 재발급 하는 API")
    @PostMapping("/reissue")
    public ResponseEntity<AtkResponse> reissueAtk() {
        AtkResponse atkResponse = atkRefreshService.execute();
        return ResponseEntity.status(HttpStatus.CREATED).body(atkResponse);
    }
}
