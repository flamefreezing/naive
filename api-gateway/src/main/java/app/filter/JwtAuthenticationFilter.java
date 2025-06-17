package app.filter;

import app.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private final List<String> excludedPaths = Arrays.asList("/auth/login", "/auth/register");

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath().replaceFirst("^/api", "");

        // Skip authentication for excluded paths
        if (excludedPaths.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleUnauthorized(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            return handleUnauthorized(exchange);
        }

        // Add user context to headers for downstream services
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", jwtUtil.getUserIdFromToken(token))
                .header("X-Username", jwtUtil.getUsernameFromToken(token))
                .header("X-User-Roles", String.join(",", jwtUtil.getRolesFromToken(token)))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
