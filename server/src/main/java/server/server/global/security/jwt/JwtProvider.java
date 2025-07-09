package server.server.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import server.server.domain.user.domain.User;
import server.server.global.error.ErrorCode;
import server.server.global.error.exception.CustomException;
import server.server.global.security.auth.AuthDetailsService;
import server.server.global.security.jwt.dto.AtkResponse;
import server.server.global.security.jwt.dto.TokenResponse;
import server.server.global.security.jwt.entity.RefreshToken;
import server.server.global.security.jwt.repository.RefreshTokenRepository;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final AuthDetailsService authDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer";

    @Value("${spring.jwt.key}")
    private String secret;

    private SecretKey key;

    @Value("${spring.jwt.live.atk}")
    private Long atkTime;

    @Value("${spring.jwt.live.rtk}")
    private Long rtkTime;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public TokenResponse getToken(User user) {
        String atk = generateAccessToken(user.getEmail());
        String rtk = generateRefreshToken(user.getEmail());

        return new TokenResponse(atk, rtk, atkTime);
    }

    public String generateAccessToken(String accountId) {
        return generateToken(accountId, "access", atkTime);
    }

    public AtkResponse reissueAccessToken(String accountId) {
        String atk = generateAccessToken(accountId);

        return new AtkResponse(atk, atkTime);
    }

    public String generateRefreshToken(String accountId) {
        String refreshToken = generateToken(accountId, "refresh", rtkTime);

        refreshTokenRepository.save(new RefreshToken(accountId, refreshToken, rtkTime));
        return refreshToken;
    }

    private String generateToken(String accountId, String type, Long exp) {
        return Jwts.builder()
                .setSubject(accountId)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp * 1000))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.JWT_INVALID);
        }
    }


    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HEADER);
        return parseToken(bearer);
    }

    public Authentication authentication(String token) {
        String accountId = getTokenSubject(token);
        UserDetails userDetails = authDetailsService.loadUserByUsername(accountId);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String parseToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith(PREFIX)) {
            return bearerToken.replace(PREFIX, "");
        }
        return null;
    }

    private Claims getTokenBody(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED);
        }
    }

    private String getTokenSubject(String token) {
        return getTokenBody(token).getSubject();
    }

    public String getUserEmailFromToken(String token) {
        try {
            Claims claims = getTokenBody(token);
            return claims.getSubject(); // email 값이 토큰의 subject에 저장되어 있다고 가정
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.JWT_INVALID);
        }
    }
}
