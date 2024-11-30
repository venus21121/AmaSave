package com.example.retailpricetracker;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonDataExtractor {
    public static String extractAsin(String url) {
        String asin = null;

        // Decode URL first
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // Regular expression pattern to match ASIN
        String pattern = "/dp/([A-Z0-9]{10})";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(decodedUrl);

        if (m.find()) {
            asin = m.group(1);
        }

        return asin;
    }

    public static BigDecimal priceConversion(String price) {
        if (price != null) {
            String cleanedPrice = price.replaceAll("[^0-9.]", "");
            // Ensure the cleaned string is not empty
            if (!cleanedPrice.isEmpty()) {
                return new BigDecimal(cleanedPrice);
            }        }
        return BigDecimal.ZERO; // Return if price is null
    }
}
