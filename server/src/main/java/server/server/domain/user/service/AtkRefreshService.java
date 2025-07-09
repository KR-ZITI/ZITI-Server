package server.server.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.domain.user.domain.User;
import server.server.domain.user.domain.facade.UserFacade;
import server.server.global.security.jwt.JwtProvider;
import server.server.global.security.jwt.dto.AtkResponse;

@Service
@RequiredArgsConstructor
public class AtkRefreshService {
    private final JwtProvider jwtProvider;
    private final UserFacade userFacade;

    @Transactional
    public AtkResponse execute() {

        User user = userFacade.getCurrentUser();

        return jwtProvider.reissueAccessToken(user.getEmail());
    }
}
