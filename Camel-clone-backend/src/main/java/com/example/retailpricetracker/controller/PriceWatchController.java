package com.example.retailpricetracker.controller;

import com.example.retailpricetracker.entity.PriceWatch;
import com.example.retailpricetracker.entity.Product;
import com.example.retailpricetracker.entity.User;
import com.example.retailpricetracker.service.PriceWatchService;
import com.example.retailpricetracker.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/pricewatch")
@CrossOrigin("http://localhost:3000")
@RestController
public class PriceWatchController {

    @Autowired
    private PriceWatchService priceWatchService;

    @Autowired
    private ProductService productService;  // To fetch product details

    // Get all price watch of a user
    @GetMapping
    public ResponseEntity<?> getAllPriceWatchByUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        List<PriceWatch> priceWatches = priceWatchService.findPriceWatchByUser(user.getUserId());
        if (priceWatches.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(priceWatches);
    }


    // Find existing pricewatches for a particular product of a user
    @GetMapping("/list")
    public ResponseEntity<?>  getAllPriceWatchByUserAndProduct( @AuthenticationPrincipal User user,
                                                                                      @RequestParam Long productId){
        try {
            List<PriceWatch> priceWatches = priceWatchService.findPriceWatchByUserAndProduct(user.getUserId(), productId);
            // find pricewatches given product and user
            if (priceWatches.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(priceWatches);
        }
        catch(Exception e ){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/create")
    public ResponseEntity<?> createPriceWatch(
            @AuthenticationPrincipal User user,
            @RequestParam Long productId,
            @RequestParam BigDecimal desiredPrice) {
        try {
            PriceWatch savedPriceWatch = priceWatchService.createPriceWatch(user, productId, desiredPrice);
            return ResponseEntity.ok(savedPriceWatch);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
}
    @PatchMapping("/update")
    public ResponseEntity<PriceWatch> updatePriceWatch(@AuthenticationPrincipal User user, @RequestParam Long priceWatchId, @RequestParam BigDecimal newDesiredPrice) {
        PriceWatch savedUpdatedPriceWatch = priceWatchService.updatePriceWatch(user, priceWatchId, newDesiredPrice);
        System.out.println("Price watch Id: " + priceWatchId + " is updated");
        return ResponseEntity.ok(savedUpdatedPriceWatch);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePriceWatch(@RequestParam Long priceWatchId,
                                                   @AuthenticationPrincipal User user) {
        try {
            priceWatchService.deletePriceWatch(priceWatchId, user);
            return ResponseEntity.ok("Price watch deleted successfully");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to delete this PriceWatch");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}

