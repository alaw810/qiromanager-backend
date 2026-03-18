package com.qiromanager.qiromanager_backend.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting para el endpoint de login.
 *
 * Limita a 5 intentos por IP cada minuto para proteger contra ataques de fuerza bruta.
 * Devuelve HTTP 429 (Too Many Requests) cuando se supera el límite.
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    // Un cubo (bucket) por cada IP que intente hacer login
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_ATTEMPTS)
                .refillGreedy(MAX_ATTEMPTS, REFILL_PERIOD)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket getBucketForIp(String ip) {
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!isLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        Bucket bucket = getBucketForIp(ip);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"error\": \"Too many login attempts. Please try again in 1 minute.\"}"
            );
        }
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && LOGIN_PATH.equals(request.getRequestURI());
    }

    /**
     * Extrae la IP real del cliente.
     * Tiene en cuenta el header X-Forwarded-For que añaden proxies y balanceadores de carga.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For puede contener una cadena de IPs: "clientIp, proxy1, proxy2"
            // La primera es siempre la del cliente real
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
