package com.sponsorando.app.utils;

import java.text.Normalizer;
import java.util.UUID;

public class SlugUtil {
    private static final String DEFAULT_SLUG = "spnsrnd";
    private static final int MIN_LENGTH_UUID = 8;

    public static String generateSlug(String input, boolean appendUuid, int inputMaxLength) {
        StringBuilder slug = new StringBuilder();

        if (input == null || input.isEmpty()) {
            input = DEFAULT_SLUG;
        }

        if (inputMaxLength > 0 && input.length() > inputMaxLength) {
            input = input.substring(0, inputMaxLength);
        }

        slug.append(
                Normalizer.normalize(
                                input
                                        .replaceAll("ä", "ae") // Replace umlauts
                                        .replaceAll("Ä", "Ae")
                                        .replaceAll("ö", "oe")
                                        .replaceAll("Ö", "Oe")
                                        .replaceAll("ü", "ue")
                                        .replaceAll("Ü", "Ue")
                                        .replaceAll("ß", "ss"), // Replace ß with ss
                                Normalizer.Form.NFD
                        )
                        .replaceAll("\\p{M}", "") // Remove diacritical marks
                        .replaceAll("[^a-zA-Z\\s-]", "") // Remove digits and special characters
                        .trim()
                        .toLowerCase()
                        .replaceAll("\\s+", "-") // Replace spaces with hyphens
                        .replaceAll("-+", "-") // Replace multiple hyphens with a single hyphen
        );

        if (slug.isEmpty()) {
            slug.append(DEFAULT_SLUG).append("-").append(UUID.randomUUID().toString(), 0, MIN_LENGTH_UUID);
        }

        if (appendUuid) {
            if (slug.length() < 2) {
                slug.append("-").append(UUID.randomUUID().toString(), 0, (MIN_LENGTH_UUID * 2));
            } else {
                slug.append("-").append(UUID.randomUUID().toString(), 0, MIN_LENGTH_UUID);
            }
        }

        return slug.toString();
    }

    public static String generateSlug(String input, boolean appendUuid) {
        return generateSlug(input, appendUuid, 0);
    }
}
