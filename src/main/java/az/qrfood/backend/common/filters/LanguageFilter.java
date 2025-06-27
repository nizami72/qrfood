package az.qrfood.backend.common.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

/**
 * A servlet filter responsible for setting the application's locale based on the
 * "Accept-Language" header in incoming HTTP requests.
 * <p>
 * This ensures that internationalized messages and content are presented to the user
 * in their preferred language. If no "Accept-Language" header is provided, it defaults to "az" (Azerbaijani).
 * </p>
 */
@Component
public class LanguageFilter implements Filter {

    /**
     * Performs the filtering logic for setting the locale.
     * <p>
     * It extracts the "Accept-Language" header from the HTTP request, sets the
     * {@link LocaleContextHolder} with the appropriate locale, and then proceeds
     * with the filter chain. The locale context is reset after the chain completes.
     * </p>
     *
     * @param request  The {@link ServletRequest} object.
     * @param response The {@link ServletResponse} object.
     * @param chain    The {@link FilterChain} to proceed with.
     * @throws IOException      If an I/O error occurs during filtering.
     * @throws ServletException If a servlet-related error occurs during filtering.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            String langHeader = httpRequest.getHeader("Accept-Language");
            Locale locale = Locale.forLanguageTag(langHeader != null ? langHeader : "az");
            LocaleContextHolder.setLocale(locale);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}
