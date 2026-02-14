package com.wikicoding.bank_account_lifecycle_engine.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.boot.servlet.filter.ApplicationContextHeaderFilter.HEADER_NAME;

@Configuration
public class RequestIdFilter extends OncePerRequestFilter {
    private static final String MDC_KEY = "requestId";
    private static final String HEADER_NAME = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(HEADER_NAME);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(MDC_KEY, requestId);

            response.setHeader(HEADER_NAME, requestId); // helpful for debugging

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
