package com.example.retailpricetracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class AmazonProductScraperHTMLTest {
    public static void main(String[] args) {
        try {
            String url = "C:/Users/venus/RetailPriceTracker/src/test/java/com/example/retailpricetracker/AmazonProductHTML.html";
            File input = new File(url);

            Document doc = Jsoup.parse(input, "UTF-8");

            Element table = doc.select("table#productDetails_detailBullets_sections1").first();

            // product name
            String title = doc.select("#productTitle").text();
            System.out.println("Title: " + title);
            // price
            Element corePriceDiv = doc.select("#corePrice_feature_div").first();
            if (corePriceDiv!= null) {
                String p = corePriceDiv.select(".a-offscreen").text().replace("$", "");
                System.out.println("Price: " + p);
            } else {
                System.out.println("Price is not found.");
            }
            // img url
            Element imageElement = doc.select("img#landingImage").first();
            if (imageElement != null) {
                String imgUrl = imageElement.attr("src");
                System.out.println("Image URL: " + imgUrl);
            } else {
                System.out.println("Image not found.");
            }

            String rating = doc.select("span[data-asin]").text();

            // product category
            String category = doc.select("a.a-link-normal.a-color-tertiary").text();
            System.out.println("Category: "+ category);
            String asin = null;
            if (table!= null) {
                Elements rows = table.select("tr");
                for(Element row: rows) {
                    Element th = row.select("th.a-color-secondary.a-size-base.prodDetSectionEntry").first();
                    if (th!= null && th.text().equals("ASIN")) {
                        Element td = row.select("td.a-size-base.prodDetAttrValue").first();
                        if(td != null) {
                            asin = td.text();
                            System.out.println("ASIN: " + asin);
                            break;
                        }
                        else {
                            System.out.println("Cannot find Asin");
                        }
                    }
                }
            } else {
                System.out.println("Product details table not found. ");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
