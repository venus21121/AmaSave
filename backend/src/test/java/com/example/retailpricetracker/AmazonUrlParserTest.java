package com.example.retailpricetracker;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class AmazonUrlParserTest {
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
    public static void main(String[] args) {
        String amazonUrl ="https://www.amazon.com/sspa/click?ie=UTF8&spc=MTo1NzczNTQ3MDYwNDQzOTMxOjE3MjI5NzM5MDI6c3BfYXRmOjIwMDAxNzUzMDE1NTE4MTo6MDo6&url=%2FAmazonBasics-Swivel-Lounge-headrest-Adjustable%2Fdp%2FB07Q2PGH2P%2Fref%3Dsr_1_1_ffob_sspa%3Fcrid%3D2FPP4ZFZG4BPH%26dib%3DeyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY%26dib_tag%3Dse%26keywords%3Dchair%26qid%3D1722973902%26sprefix%3Dchair%252Caps%252C214%26sr%3D8-1-spons%26sp_csd%3Dd2lkZ2V0TmFtZT1zcF9hdGY%26psc%3D1";
        String asin = extractAsin(amazonUrl);
        System.out.println("ASIN: " + asin); // Output: ASIN: B0CV4QQFGK
    }
}
