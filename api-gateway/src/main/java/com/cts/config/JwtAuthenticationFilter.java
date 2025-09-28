package com.cts.config;

import com.cts.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    // Define public paths that do not require a token
    private final List<String> publicPaths = List.of("/api/auth/login", "/api/auth/register");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Check if the request path is a public one
        if (isPublicPath(request)) {
            return chain.filter(exchange); // If public, let it pass
        }

        // 2. Get the token from the request header
        String token = getTokenFromRequest(request);

        // 3. Validate the token
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            return chain.filter(exchange); // If token is valid, let it pass
        }

        // 4. If token is invalid or missing, reject the request
        return this.onError(exchange, "Authorization header is invalid or missing", HttpStatus.UNAUTHORIZED);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isPublicPath(ServerHttpRequest request) {
        return publicPaths.stream().anyMatch(p -> request.getURI().getPath().contains(p));
    }

    private String getTokenFromRequest(ServerHttpRequest request) {
        final String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
