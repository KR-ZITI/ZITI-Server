package server.server.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import server.server.domain.gpt.service.StreamCompletionHandler;
import server.server.global.security.filter.JwtHandshakeInterceptor;
import server.server.global.security.jwt.JwtProvider;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    private final StreamCompletionHandler streamCompletionHandler;
    private final JwtProvider jwtProvider;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(streamCompletionHandler, "/chat/stream")
                .addInterceptors(new JwtHandshakeInterceptor(jwtProvider))
                .setAllowedOrigins("*");
    }
}


