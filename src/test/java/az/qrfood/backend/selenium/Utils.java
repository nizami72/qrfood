package az.qrfood.backend.selenium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    /**
     * Replaces placeholders like {someString} in a URL sequentially with the given arguments.
     * The first occurrence of a placeholder is replaced by args[0], the second by args[1], and so on.
     *
     * @param url  The string containing placeholders, e.g., "/zzz/xxx/{userId}/posts/{postId}"
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

    /**
     * Returns a new list containing a specified number of random elements from the source list.
     * <p>
     * This method is generic and works with a list of any type. It does not modify the original list.
     *
     * @param sourceList The list to draw random elements from. Must not be null.
     * @param n          The number of random elements to return. Must not be negative.
     * @param <T>        The type of elements in the list.
     * @return A new {@code List<T>} containing 'n' elements in random order from the source list.
     * If 'n' is greater than the source list's size, a shuffled copy of the entire source list is returned.
     * Returns an empty list if the source list is empty or if 'n' is 0.
     * @throws IllegalArgumentException if the sourceList is null or if n is negative.
     */
    public static <T> List<T> getRandomElements(List<T> sourceList, int n) {
        // Validate the inputs
        if (sourceList == null) {
            throw new IllegalArgumentException("Source list cannot be null.");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Number of elements (n) cannot be negative.");
        }

        // Handle cases where an empty list should be returned
        if (n == 0 || sourceList.isEmpty()) {
            return new ArrayList<>();
        }

        // Create a mutable copy to avoid modifying the original list.
        List<T> listCopy = new ArrayList<>(sourceList);

        // Shuffle the entire copy to randomize the order of its elements.
        Collections.shuffle(listCopy);

        // Determine the actual number of elements to return.
        // It's either n or the list size, whichever is smaller.
        int count = Math.min(n, listCopy.size());

        // Return a new list containing the first 'count' elements from the shuffled copy.
        // We create a new ArrayList from the sublist to ensure it's a standalone, mutable list.
        return new ArrayList<>(listCopy.subList(0, count));
    }

}