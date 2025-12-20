package az.qrfood.backend.config;

import az.qrfood.backend.common.Util;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE+100) // Make this filter run first
public class RequestLoggingFilter extends OncePerRequestFilter {

    // Define a key for the MDC
    private static final String REQUEST_ID_KEY = "requestId";
    private String pattern = ">>>>>> %s";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String requestId = String.valueOf(Util.getRandomInt(1000, 9999));
        MDC.put(REQUEST_ID_KEY, requestId);

        log.info(String.format(pattern, "REQUEST: {}, RemoteAddr=[{}]"),
                request.getMethod() + ":" + request.getRequestURI(),
                request.getRemoteAddr());

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info(String.format(pattern, "RESPONSE: Status=[{}] for URI=[{}]"),
                    response.getStatus(),
                    request.getRequestURI());

            MDC.remove(REQUEST_ID_KEY);

        }
    }
}