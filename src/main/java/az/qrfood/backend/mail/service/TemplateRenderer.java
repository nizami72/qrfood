package az.qrfood.backend.mail.service;

import az.qrfood.backend.mail.entity.EmailTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class TemplateRenderer {

    // We use two different engines as per your original configuration:
    // 1. Text engine for database-stored strings
    // 2. Spring engine for file-based layouts (e.g., resources/templates/email/layout.html)
    private final TemplateEngine textTemplateEngine;
    private final TemplateEngine springTemplateEngine;

    public TemplateRenderer(@Qualifier("textTemplateEngine") TemplateEngine textTemplateEngine,
                            @Qualifier("springTemplateEngine") TemplateEngine springTemplateEngine) {
        this.textTemplateEngine = textTemplateEngine;
        this.springTemplateEngine = springTemplateEngine;
    }

    /**
     * Renders the final HTML by processing the inner body first, and then wrapping it
     * in the global layout.
     */
    public String render(EmailTemplate templateDb, String languageCode, Map<String, Object> variables) {

        Context context = new Context(Locale.forLanguageTag(languageCode));
        context.setVariables(variables);
        String innerBodyHtml = textTemplateEngine.process(templateDb.getBodyHtml(), context);
        context.setVariable("mainContent", innerBodyHtml);
        return springTemplateEngine.process("email/layout", context);
    }

    /**
     * Renders the subject line, which might also contain variables (e.g., "Welcome, ${name}!")
     */
    public String renderSubject(EmailTemplate templateDb, String languageCode, Map<String, Object> variables) {
        Context context = new Context(Locale.forLanguageTag(languageCode));
        context.setVariables(variables);
        String subjectTemplate = templateDb.getSubjectTemplate();
        if (subjectTemplate == null) {
            return "";
        }

        String trimmed = subjectTemplate.trim();
        // In TEXT template mode, message (#{...}) and variable (${...}) expressions
        // need text inlining [[...]] to be evaluated. If DB stores plain '#{key}' or
        // '${var}', wrap them so Thymeleaf resolves them instead of outputting literally.
        boolean containsExpr = trimmed.contains("#{") || trimmed.contains("${");
        boolean alreadyInlined = trimmed.contains("[[") && trimmed.contains("]]");
        String toProcess = (containsExpr && !alreadyInlined)
                ? "[[" + subjectTemplate + "]]"
                : subjectTemplate;

        return textTemplateEngine.process(toProcess, context);
    }
}
