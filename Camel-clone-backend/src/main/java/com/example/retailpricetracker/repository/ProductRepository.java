package com.example.retailpricetracker.repository;

// Spring Data JPA can automatically generate queries for you based on the method names defined and the matching values
// You will need to write custom queries using @Query when:
// - The method name doesn't follow the naming convention, and Spring Data JPA cannot generate the query automatically
// - You need more complex queries, such as those involving joins, custom logic, or non-standard SQL operations.
import com.example.retailpricetracker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    // Used when user searches by Asin
    Optional<Product> findByProductSku(String sku);

    // Used when user searches by keyword
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

}
