package com.example.retailpricetracker.controller;

import com.example.retailpricetracker.entity.Product;
import com.example.retailpricetracker.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class WebhookController {

    @Autowired
    private ProductRepository productRepository;

    // Testing Endpoint
    @GetMapping("/webhook/status")
    public ResponseEntity<String> handleGetRequest() {
        return new ResponseEntity<>("This endpoint only supports POST requests.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Have to run "ngrok http 8080" to update webhook URL on BrowseAI

    // After runBot finishes running task (scraping), the data will be sent to this endpoint
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        // Todo: save the details of the products
        // Returns after a bot is finish scraping data:
        System.out.println("Received payload successfully, now lets save the data..");
        System.out.println("Received payload: " + payload);
        processWebhookData(payload);
        return new ResponseEntity<>("Received payload successfully", HttpStatus.OK);

    }

    // Handles the retrieved data from webhook
    public void processWebhookData(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Read the JSON file and map it to JsonNode
            JsonNode rootNode = mapper.readTree(payload);
            String event = rootNode.get("event").asText();

            // handle different event types:
            switch(event) {
                case "task.finishedSuccessfully":
                    // Access the "products" array
                    JsonNode productsNode = rootNode.get("task").get("capturedLists").get("Products");
                    // Loop through the array and extract product information
                    for (JsonNode productNode : productsNode) {
                        String status = productNode.has("_STATUS") ? productNode.get("_STATUS").asText(null) : null;

                        // Save product if the status field is "added" or of there is no field status
                        if ("ADDED".equals(status) || status == null) {
                            saveOrUpdateProduct(productNode);
                            }
                        else {
                            System.out.println("Product status is not `ADDED`, skipping ...");

                        }
                    }
                    break;
                case "task.finishedWithError":
                    // Handle error case
                    System.err.println("Task finished with error");
                    break;
                case "task.capturedDataChanged":
                    // Handle data change if needed
                    System.out.println("Captured data changed");
                    break;
                default:
                    System.err.println("Unknown event type: " + event);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save or update product to database if key fields exists
    private void saveOrUpdateProduct(JsonNode productNode){
        String name =  productNode.has("Title") ? productNode.get("Title").asText(): null;
        String url = productNode.has("Product Link") ?productNode.get("Product Link").asText():null;
        String imgUrl = productNode.has("Image") ?productNode.get("Image").asText(): null;
        // Validate important fields
        if (name ==null || url == null || imgUrl == null) {
            System.out.println("Product is missing important fiels, skipping ...");
            return;
        }

        String price_string =  productNode.has("Price") ? productNode.get("Price").asText(): null;
        BigDecimal price = priceConversion(price_string);
        String asin = extractAsin(url);

        // Check if there is existing product in database
        Optional<Product> existingProduct = productRepository.findByProductSku(asin);

        // if there is existing asin product in database, update price only
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setCurrentPrice(price);
            productRepository.save(product);
            System.out.println("Successfully updated product price\n"+ name+ "update price to: "+ price);

        } else {
            // Save the whole product
            Product product = new Product(null, name, price, imgUrl, asin, "", "");
            productRepository.save(product);
            System.out.println("Saved new product: " + name + ", price: " + price + ", ASIN: " + asin + ", ImgUrl: " + imgUrl);
        }

    }

    // Extract Asin number from Amazon URL
    private String extractAsin(String url) {
        try {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
            String pattern = "/dp/([A-Z0-9]{10})";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(decodedUrl);

            if (m.find()) {
                return m.group(1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Convert type for price from String to BigDecimal
    private BigDecimal priceConversion(String price) {
        if (price != null) {
            String cleanedPrice = price.replaceAll("[^0-9.]", "");
            // Ensure the cleaned string is not empty
            if (!cleanedPrice.isEmpty()) {
                return new BigDecimal(cleanedPrice);
            }
        }
        return null; // Return if price is null
    }

}


// Return: Received payload: {"task":{"id":"10631185-1154-4f11-acaa-1b63c43ba26d","status":"successful","createdAt":1722973898513,"finishedAt":1722973919206,"retriedOriginalTaskId":"2235df3a-03c9-4ca9-a4b0-28d9cd6bfd23","retriedTaskId":"2235df3a-03c9-4ca9-a4b0-28d9cd6bfd23","retriedByTaskId":null,"startedAt":1722973898666,"robotId":"f8446b2a-a745-4ede-b478-00ab3f1f6f48","triedRecordingVideo":false,"robotBulkRunId":null,"runByAPI":true,"runByTaskMonitorId":null,"runByUserId":null,"userFriendlyError":null,"inputParameters":{"amazon_url":"https://www.amazon.com/s?k=chair&crid=2FPP4ZFZG4BPH&sprefix=chair%2Caps%2C214&ref=nb_sb_noss_1","max_products":10},"videoRemovedAt":null,"videoUrl":null,"capturedDataTemporaryUrl":null,"capturedTexts":{},"capturedScreenshots":{},"capturedLists":{"Products":[{"Position":"1","Title":"Amazon Basics Swivel Foam Lounge Chair with Headrest, Adjustable, 26.3\"D x 23.5\"W x 13.7\"H, Grey","Product Link":"https://www.amazon.com/sspa/click?ie=UTF8&spc=MTo1NzczNTQ3MDYwNDQzOTMxOjE3MjI5NzM5MDI6c3BfYXRmOjIwMDAxNzUzMDE1NTE4MTo6MDo6&url=%2FAmazonBasics-Swivel-Lounge-headrest-Adjustable%2Fdp%2FB07Q2PGH2P%2Fref%3Dsr_1_1_ffob_sspa%3Fcrid%3D2FPP4ZFZG4BPH%26dib%3DeyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY%26dib_tag%3Dse%26keywords%3Dchair%26qid%3D1722973902%26sprefix%3Dchair%252Caps%252C214%26sr%3D8-1-spons%26sp_csd%3Dd2lkZ2V0TmFtZT1zcF9hdGY%26psc%3D1","Image":"https://m.media-amazon.com/images/I/A1cczIB-pOL._AC_UL320_.jpg","Price":"$134.53","Reviews":"1,883","Rating":"4.3 out of 5 stars","Sponsored":"Featured from Amazon brands"},{"Position":"2","Title":"Office Chair, High Back Ergonomic Desk Chair with Lumbar Support, Breathable Mesh Computer Chair with Adjustable Headrest and Flip-Up Armrests,Grey Recliner Chair for Home Office","Product Link":"https://www.amazon.com/sspa/click?ie=UTF8&spc=MTo1NzczNTQ3MDYwNDQzOTMxOjE3MjI5NzM5MDI6c3BfYXRmOjMwMDI4MTkyMjk2MjYwMjo6MDo6&url=%2FZYFFHYLAI-Ergonomic-Breathable-Computer-Adjustable%2Fdp%2FB0CV4QQFGK%2Fref%3Dsr_1_2_sspa%3Fcrid%3D2FPP4ZFZG4BPH%26dib%3DeyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY%26dib_tag%3Dse%26keywords%3Dchair%26qid%3D1722973902%26sprefix%3Dchair%252Caps%252C214%26sr%3D8-2-spons%26sp_csd%3Dd2lkZ2V0TmFtZT1zcF9hdGY%26psc%3D1","Image":"https://m.media-amazon.com/images/I/817dXPm84kL._AC_UL320_.jpg","Price":"$129.99","Reviews":"56","Rating":"4.3 out of 5 stars","Sponsored":"Sponsored"},{"Position":"3","Title":"Mesh Office Chair, Ergonomic Computer Chair with Flip-up Arms and Lumbar Support, Height Adjustable Home Office Desk Chairs, Black","Product Link":"https://www.amazon.com/sspa/click?ie=UTF8&spc=MTo1NzczNTQ3MDYwNDQzOTMxOjE3MjI5NzM5MDI6c3BfYXRmOjIwMDEwNzUzNTI5Nzc5ODo6MDo6&url=%2FYouhauchair-Ergonomic-Computer-Flip-up-Adjustable%2Fdp%2FB0B8MDPJZN%2Fref%3Dsr_1_3_sspa%3Fcrid%3D2FPP4ZFZG4BPH%26dib%3DeyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY%26dib_tag%3Dse%26keywords%3Dchair%26qid%3D1722973902%26sprefix%3Dchair%252Caps%252C214%26sr%3D8-3-spons%26sp_csd%3Dd2lkZ2V0TmFtZT1zcF9hdGY%26psc%3D1","Image":"https://m.media-amazon.com/images/I/81iVWkCvBBL._AC_UL320_.jpg","Price":"$119.99","Reviews":"475","Rating":"4.5 out of 5 stars","Sponsored":"Sponsored"},{"Position":"4","Title":"Duramont Ergonomic Office Chair - Adjustable Desk Chair with Lumbar Support and Rollerblade Wheels - High Back Chairs with Breathable Mesh - Thick Seat Cushion, Head, and Arm Rests - Reclines","Product Link":"https://www.amazon.com/sspa/click?ie=UTF8&spc=MTo1NzczNTQ3MDYwNDQzOTMxOjE3MjI5NzM5MDI6c3BfYXRmOjMwMDA1NjQyMDg4MzQwMjo6MDo6&url=%2FDuramont-Ergonomic-Adjustable-Support-Rollerblade%2Fdp%2FB0797HZ8W1%2Fref%3Dsr_1_4_sspa%3Fcrid%3D2FPP4ZFZG4BPH%26dib%3DeyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY%26dib_tag%3Dse%26keywords%3Dchair%26qid%3D1722973902%26sprefix%3Dchair%252Caps%252C214%26sr%3D8-4-spons%26sp_csd%3Dd2lkZ2V0TmFtZT1zcF9hdGY%26psc%3D1","Image":"https://m.media-amazon.com/images/I/61NKBZLoUtL._AC_UL320_.jpg","Price":"$271.99","Reviews":"6,863","Rating":"4.2 out of 5 stars","Sponsored":"Sponsored"},{"Position":"5","Title":"DUMOS Armless Home Office Chair Ergonomic Desk with Comfy Low Back Lumbar Support, Height Adjustable PU Leather Computer Task with 360° Swivel Wheels, for Small Space, Kids and Adults, Beige White","Product Link":"https://www.amazon.com/DUMOS-Armless-Ergonomic-Adjustable-Computer/dp/B0CL4S7FRC/ref=sr_1_5?crid=2FPP4ZFZG4BPH&dib=eyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY&dib_tag=se&keywords=chair&qid=1722973902&sprefix=chair%2Caps%2C214&sr=8-5","Image":"https://m.media-amazon.com/images/I/71rP8xBGJ9L._AC_UL320_.jpg","Price":null,"Reviews":"655","Rating":"4.6 out of 5 stars","Sponsored":null},{"Position":"6","Title":"Home Office Chair Ergonomic Desk Chair Mesh Computer Chair with Lumbar Support Armrest Executive Rolling Swivel Adjustable Mid Back Task Chair for Women Adults, Black","Product Link":"https://www.amazon.com/Black-Office-Chair-Computer-Adjustable/dp/B00FS3VJAO/ref=sr_1_6?crid=2FPP4ZFZG4BPH&dib=eyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY&dib_tag=se&keywords=chair&qid=1722973902&sprefix=chair%2Caps%2C214&sr=8-6","Image":"https://m.media-amazon.com/images/I/71jeulFxQ9L._AC_UL320_.jpg","Price":"$35.99","Reviews":"56,168","Rating":"4.3 out of 5 stars","Sponsored":null},{"Position":"7","Title":"FDW Office Chair Computer High Back Adjustable Ergonomic Desk Chair Executive PU Leather Swivel Task Chair with Armrests Lumbar Support (Black)","Product Link":"https://www.amazon.com/Office-Chair-Adjustable-Ergonomic-Executive/dp/B08JGGJZY2/ref=sr_1_7?crid=2FPP4ZFZG4BPH&dib=eyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY&dib_tag=se&keywords=chair&qid=1722973902&sprefix=chair%2Caps%2C214&sr=8-7","Image":"https://m.media-amazon.com/images/I/61VqPRU2-UL._AC_UL320_.jpg","Price":"$78.99","Reviews":"7,643","Rating":"4.3 out of 5 stars","Sponsored":null},{"Position":"8","Title":"Marsail Ergonomic Office Chair: Office Computer Desk Chair with High Back Mesh and Adjustable Lumbar Support Rolling Work Swivel Task Chairs with Wheel 3D Armrests and Headrest","Product Link":"https://www.amazon.com/Marsail-Ergonomic-Office-Chair-Adjustable/dp/B0CP22DQQS/ref=sr_1_8?crid=2FPP4ZFZG4BPH&dib=eyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY&dib_tag=se&keywords=chair&qid=1722973902&sprefix=chair%2Caps%2C214&sr=8-8","Image":"https://m.media-amazon.com/images/I/71-tTLbGokL._AC_UL320_.jpg","Price":"$127.06","Reviews":"153","Rating":"4.4 out of 5 stars","Sponsored":null},{"Position":"9","Title":"GABRYLLY Ergonomic Mesh Office Chair, High Back Desk Chair - Adjustable Headrest with Flip-Up Arms, Tilt Function, Lumbar Support and PU Wheels, Swivel Computer Task Chair","Product Link":"https://www.amazon.com/Gabrylly-Ergonomic-Mesh-Office-Chair/dp/B07Y8BXBX8/ref=sr_1_9?crid=2FPP4ZFZG4BPH&dib=eyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY&dib_tag=se&keywords=chair&qid=1722973902&sprefix=chair%2Caps%2C214&sr=8-9","Image":"https://m.media-amazon.com/images/I/91Fa+MzIKEL._AC_UL320_.jpg","Price":"$188.00","Reviews":"13,329","Rating":"4.5 out of 5 stars","Sponsored":null},{"Position":"10","Title":"Dowinx Gaming Chair Fabric with Pocket Spring Cushion, Massage Game Chair Cloth with Headrest, Ergonomic Computer Chair with Footrest 290LBS, Black and Grey","Product Link":"https://www.amazon.com/Dowinx-Headrest-Ergonomic-Computer-Footrest/dp/B09WTM88B6/ref=sr_1_10?crid=2FPP4ZFZG4BPH&dib=eyJ2IjoiMSJ9.uqkhQyLsEbS2T3DhFrqeUM6CBdTOLKR_6G4gTUufaFhX4hONq2M3KGCz8RJ9tlbaSTjydGZrTV_ahnZY-dyJ-_b7dR8xMOwuWgtkK9sVSqvkK3fih57q-RU3TFrk_uLI_KcyF6fsLogGF4NYVtOBJqgbjFMroMoAn7zyRCsdSySRFzhYIGRhIrReStghbTCwr2lTlnCqCvXF1lMWdvTlcZfwCeX6JnUplbbiapoXXDfwmoqzlokyDPF2nm0gMQCMbspwjmJ1FWNKlH4sUpRCPgfGAfMtUBmHL2q_kB3i2j8.TMvJ_61RCJSlqXHeQu-s7fIjeLeiu21e0j75AP4syeY&dib_tag=se&keywords=chair&qid=1722973902&sprefix=chair%2Caps%2C214&sr=8-10","Image":"https://m.media-amazon.com/images/I/71wTMO+iQIL._AC_UL320_.jpg","Price":"$189.99","Reviews":"2,880","Rating":"4.3 out of 5 stars","Sponsored":null}]}},"event":"task.finishedSuccessfully"}
