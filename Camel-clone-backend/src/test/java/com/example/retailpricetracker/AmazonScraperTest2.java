package com.example.retailpricetracker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Random;
import java.io.IOException;
public class AmazonScraperTest2 {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Firefox/89.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edge/91.0.864.48"
    };

    public static void main(String[] args) {
        String productUrl = "https://www.amazon.com/dp/B09FPXGLGF/"; // Example URL
        try {
            String html = fetchHtml(productUrl);
            System.out.println(html);
            parseHtml(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchHtml(String url) throws IOException {
        Random rand = new Random();
        String userAgent = USER_AGENTS[rand.nextInt(USER_AGENTS.length)];
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static void parseHtml(String html) {
        Document document = Jsoup.parse(html);

        // product name
        String title = document.select("#productTitle").text();
        System.out.println("Title: " + title);
        // price
        Element corePriceDiv = document.select("#corePrice_feature_div").first();
        if (corePriceDiv!= null) {
            String p = corePriceDiv.select(".a-offscreen").text().replace("$", "");
            System.out.println("p: " + p);
        } else {
            System.out.println("Price is not found.");
        }
        // img url
        Element imageElement = document.select("img#landingImage").first();
        if (imageElement != null) {
            String imgUrl = imageElement.attr("src");
            System.out.println("Image URL: " + imgUrl);
        } else {
            System.out.println("Image not found.");
        }

        String rating = document.select("span[data-asin]").text();

        // product category
        String category = document.select("a.a-link-normal.a-color-tertiary").text();
        System.out.println("Category: "+ category);
        // product detail, skip for now.

        String asin = null;
        // product sku
        Elements productDetailsRows = document.select("#productDetails_detailBullets_sections1 tr");
        for (Element row : productDetailsRows) {
            Element header = row.select("th").first();
            if (header != null && "ASIN".equalsIgnoreCase(header.text().trim())) {
                Element detail = row.select("td.a-size-base.prodDetAttrValue").first();
                if (detail != null) {
                    asin = detail.text().trim();
                    break;
                }
            }
        }
        System.out.println("Asin: " + asin);
        System.out.println("Rating: " + rating);
    }
}
