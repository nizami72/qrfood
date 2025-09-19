package az.qrfood.backend.selenium;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    /**
     * Replaces placeholders like {someString} in a URL sequentially with the given arguments.
     * The first occurrence of a placeholder is replaced by args[0], the second by args[1], and so on.
     *
     * @param url  The string containing placeholders, e.g., "/api/users/{userId}/posts/{postId}"
     * @param args The values to substitute into the placeholders.
     * @return The formatted string with placeholders replaced.
     */
    public static String replacePlaceHolders(String url, String... args) {
        // Return the original URL if no arguments are provided to avoid unnecessary processing
        if (args == null || args.length == 0) {
            return url;
        }
        // Regex to find any characters inside curly braces, non-greedily
        Pattern pattern = Pattern.compile("\\{.+?}");
        Matcher matcher = pattern.matcher(url);
        StringBuilder sb = new StringBuilder();
        int i = 0;

        // Iterate over all matches
        while (matcher.find()) {
            // Ensure we don't go out of bounds on the args array
            if (i < args.length) {
                // Replace the current match with the next argument
                // Matcher.quoteReplacement handles any special characters ($) in the replacement string
                matcher.appendReplacement(sb, Matcher.quoteReplacement(args[i]));
                i++;
            } else {
                // If there are more placeholders than arguments, you might want to break
                // or throw an exception. Here, we'll stop replacing.
                break;
            }
        }
        // Append the remainder of the string after the last match
        matcher.appendTail(sb);
        return sb.toString();
    }

}