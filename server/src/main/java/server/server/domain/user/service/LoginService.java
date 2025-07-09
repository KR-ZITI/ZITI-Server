package server.server.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.server.domain.user.domain.User;
import server.server.domain.user.domain.facade.UserFacade;
import server.server.domain.user.presentation.dto.request.LoginRequest;
import server.server.global.error.ErrorCode;
import server.server.global.error.exception.CustomException;
import server.server.global.security.jwt.JwtProvider;
import server.server.global.security.jwt.dto.TokenResponse;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserFacade userFacade;

    @Transactional
    public TokenResponse execute(LoginRequest request) {
        User user = userFacade.getUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISS_MATCHED);
        }

        return jwtProvider.getToken(user);
    }
}
