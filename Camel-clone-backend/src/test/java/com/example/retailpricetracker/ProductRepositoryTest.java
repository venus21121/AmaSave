package com.example.retailpricetracker;
import com.example.retailpricetracker.entity.Product;
import com.example.retailpricetracker.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

//    @BeforeEach
//    void setUp() {
//        // Adding sample data
//        productRepository.save(new Product(null, "Apple iPhone 13", new BigDecimal("999.99"), "img_url_1", "B08Z2Y1KGN", "Smartphone", "Detail1"));
//        productRepository.save(new Product(null, "Samsung Galaxy S21", new BigDecimal("799.99"), "img_url_2", "B08Z2Y1KGF", "Smartphone", "Detail2"));
//        productRepository.save(new Product(null, "Google Pixel 6", new BigDecimal("599.99"), "img_url_3", "B08Z2Y1KGL", "Smartphone", "Detail3"));
//        productRepository.save(new Product(null, "Painting of apples", new BigDecimal("539.99"), "img_url_4", "B09Z2Y1KGL", "Smartphone", "Detail4"));
//
//    }

    @Test
    void testFindByProductNameContainingIgnoreCase() {
        // Testing case insensitive search
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase("iphone");
        assertEquals(1, products.size());
        assertEquals("Apple iPhone 13", products.get(0).getProductName());

        products = productRepository.findByProductNameContainingIgnoreCase("samsung");
        assertEquals(1, products.size());
        assertEquals("Samsung Galaxy S21", products.get(0).getProductName());


        // Testing case insensitive search for "phone"
        products = productRepository.findByProductNameContainingIgnoreCase("phone");
        assertEquals(1, products.size()); // Should match only "Apple iPhone 13"
        assertEquals("Apple iPhone 13", products.get(0).getProductName());

        products = productRepository.findByProductNameContainingIgnoreCase("apple");
        // Optionally, check the actual products in the result
        assertEquals("Apple iPhone 13", products.get(0).getProductName());
        assertEquals("Painting of apples", products.get(1).getProductName());
        assertEquals(2, products.size());
    }
}
