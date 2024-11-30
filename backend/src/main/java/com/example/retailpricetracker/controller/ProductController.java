package com.example.retailpricetracker.controller;

// When you use ResponseEntity.ok(), you're creating a ResponseEntity object, which gives you more control over the HTTP response.
// Control Over Response Status: ResponseEntity.ok() automatically sets the HTTP status code to 200 (OK).
import com.example.retailpricetracker.entity.Product;
import com.example.retailpricetracker.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin("http://localhost:3000")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<List<Product>>  getAllProducts(){
        return ResponseEntity.ok(this.productService.getAllProducts());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Product> getSingleProduct(@PathVariable Long id){
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @PathVariable deals with parts of the URL path itself.
    // used when you need to filter or modify the behavior of the endpoint, like searching or paginating results.
    @GetMapping("/{asin}")
    public ResponseEntity<Product> getProductByASIN(@PathVariable String asin) {
        return productService.getProductByASIN(asin)
                .map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Todo: Make post request to BrowseAI
    //

    // @RequestParam deals with query parameters in the URL, which are typically after a ? symbol.
    // used when you need to identify a resource, like a specific product or user, based on the URL structure.

    // Searching by Keyword,
    @GetMapping("/search")
    public ResponseEntity<?> searchProductsByInput(@RequestParam("input") String input) {

        List<Product> products = productService.searchProductsByInput(input);
        if (!products.isEmpty()) {
            // Retrieve one product or list of products
            return ResponseEntity.ok(products);
        }

        // Handles errors when no products found, Returns a String if no products are found
        if(productService.isAsin(input)) {
            System.out.println("Product with Asin is not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with ASIN " + input + " not found.");
        } else if (productService.isUrl(input)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product found for the URL provided.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with " + input + " not found.");
        }

        // If BrowseAI task is triggered, return an accepted status
        //return ResponseEntity.status(HttpStatus.ACCEPTED).body(Collections.emptyList());
    }

}


