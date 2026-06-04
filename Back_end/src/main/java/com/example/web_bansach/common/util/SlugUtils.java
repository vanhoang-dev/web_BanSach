package com.example.web_bansach.common.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class for slug generation and URL handling
 */
public class SlugUtils {

    private SlugUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    private static final Pattern PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Normalize Vietnamese characters
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String nfd = PATTERN.matcher(normalized).replaceAll("");

        // Convert to lowercase and replace spaces with hyphens
        return nfd.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public static String removeDiacritics(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return PATTERN.matcher(normalized).replaceAll("");
    }
}
