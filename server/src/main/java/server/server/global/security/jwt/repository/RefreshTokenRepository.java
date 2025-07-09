package server.server.global.security.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import server.server.global.security.jwt.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    boolean existsByAccountId(String accountId);
    Optional<RefreshToken> findRefreshTokenByRtk(String rtk);
}
