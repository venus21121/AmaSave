package com.example.retailpricetracker.service;

import com.example.retailpricetracker.entity.Product;
import com.example.retailpricetracker.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Fetch all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Fetch a product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Fetch a product by ASIN
    public Optional<Product> getProductByASIN(String asin) {
        return productRepository.findByProductSku(asin);
    }


    // Main service method to handle input (keyword or URL or ASIN)
    public List<Product> searchProductsByInput(String input) {
        // Determine if the input is a URL or a keyword
        boolean isUrl = isUrl(input);
        boolean isAsin = isAsin(input);

        if (isAsin) {
            System.out.println("User is searching by asin");
            // If input is an ASIN, search for the product by ASIN
            Optional<Product> product = getProductByASIN(input);
            if (product.isPresent()) {
                return Collections.singletonList(product.get());
            } else {
                // Return empty list or a message indicating ASIN not found
                return Collections.emptyList();
            }
        }
        if (isUrl) {
            // If the input is a URL, search by URL
            System.out.println("User is searching by URL");
            return searchByUrl(input);
        } else {
            System.out.println("User is searching by KEYWORD");
            // Otherwise, search by keyword
            return searchByKeyword(input);
        }
    }

    // Method to check if the input is a URL
    public boolean isUrl(String input) {
        //String urlPattern = "^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$|^www\\.[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$";
        String urlPattern = "^(https://|ftp://|www\\.|amazon\\.com)([^\\s]*)(/dp/)([A-Z0-9]{10})([^\\s]*)$";
        return input.matches(urlPattern);
    }

    // Method to check if the input is Asin
    public boolean isAsin(String input) {
        // Regular expression to check if input is a valid ASIN (10 alphanumeric characters)
        String asinPattern = "^[A-Z0-9]{10}$";
        return input != null && input.matches(asinPattern);
    }


    // Placeholder for searching products by URL
    private List<Product> searchByUrl(String url) {
        String asin = extractAsin(url);
        if (asin != null) {
            Optional<Product> product = getProductByASIN(asin);
            if (product.isPresent()) {
                return Collections.singletonList(product.get());
            } else {
                // Return empty list or message indicating ASIN not found
                return Collections.emptyList();
            }
        } else {
            // Return empty list or error message for invalid URL
            return Collections.emptyList();
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
    // Search products by keyword
    public List<Product> searchByKeyword(String keyword) {
        // Query products from the repository
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);

        // If products are found, return them
        if (!products.isEmpty()) {
            return products;
        }

        return Collections.emptyList();

    }

}
