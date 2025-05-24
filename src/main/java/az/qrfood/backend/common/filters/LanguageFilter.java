package az.qrfood.backend.common.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
public class LanguageFilter implements Filter {

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
