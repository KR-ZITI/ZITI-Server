package server.server.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import server.server.global.error.ErrorCode;
import server.server.global.error.exception.CustomException;
import server.server.global.security.jwt.JwtProvider;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = jwtProvider.resolveToken(request);

            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.authentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            // CustomException 안에 ErrorCode 정보가 있다고 가정
            ErrorCode errorCode = e.getErrorCode();

            response.setStatus(errorCode.getHttpStatus()); // 예: 401
            response.setContentType("application/json; charset=UTF-8");

            String jsonResponse = String.format(
                    "{\"status\": %d, \"code\": \"%s\", \"message\": \"%s\"}",
                    errorCode.getHttpStatus(),
                    errorCode.name(),
                    errorCode.getMessage()
            );

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        }
    }
}
