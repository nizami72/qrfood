package az.qrfood.backend.common.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

    private final LocaleResolver localeResolver;

    public LanguageFilter(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

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

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {

            List<String> supportedLangs = Arrays.asList("en", "ru", "az");
            String selectedLang = null;

            if (httpRequest.getCookies() != null) {
                selectedLang = Arrays.stream(httpRequest.getCookies())
                        .filter(c -> "language".equals(c.getName())) // Assuming cookie name is "lang"
                        .map(Cookie::getValue)
                        .filter(supportedLangs::contains) // Ensure it is explicitly en, ru, or az
                        .findFirst()
                        .orElse(null);
            }

            if (selectedLang == null) {
                Locale browserLocale = httpRequest.getLocale();
                String headerLang = browserLocale.getLanguage(); // Gets just "en", "ru", etc.

                if (supportedLangs.contains(headerLang)) {
                    selectedLang = headerLang;
                }
            }

            if (selectedLang == null) {
                selectedLang = "az";
            }
            Locale newLocale = Locale.of(selectedLang); // e.g., "az"
            localeResolver.setLocale(httpRequest, httpResponse, newLocale);
            LocaleContextHolder.setLocale(Locale.of((selectedLang)));
        }

        try {
            chain.doFilter(request, response);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}
