package com.example.retailpricetracker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
public class JsonReader {


    // Extract Products detail from webhook and save them in database
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AmazonUrlParserTest urlParser = new AmazonUrlParserTest();
            // Read the JSON file and map it to JsonNode
            JsonNode rootNode = mapper.readTree(new File("C:/Users/venus/RetailPriceTracker/src/test/java/com/example/retailpricetracker/ProductData.json"));

            JsonNode event = rootNode.get("event");
            System.out.println(event.asText());
            // Access the "products" array
            JsonNode productsNode = rootNode.get("task").get("capturedLists").get("Products");
            System.out.println(productsNode);

            // Loop through the array and extract product information
            for (JsonNode productNode : productsNode) {
                String name = productNode.get("Title").asText();

                String url = productNode.get("Product Link").asText(); //Todo: extract Asin from url
                String asin = urlParser.extractAsin(url);

                String price_string = productNode.get("Price").asText(); // Todo: Change string into Double
                BigDecimal price = urlParser.priceConversion(price_string);

                String img_url = productNode.get("Image").asText();


////                // Optionally, create Product objects and store them in a list

                // Print or use the product information
                //System.out.println("Name: " + name);
                System.out.println("Price: " + price);
                System.out.println("Products attempt to save to database.");
                //System.out.println("Img Url" + img_url);
                //System.out.println("URL: " + url);
                //System.out.println("ASIN: " + asin);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
