package com.example.retailpricetracker.service;

import com.example.retailpricetracker.entity.PriceWatch;
import com.example.retailpricetracker.entity.Product;
import com.example.retailpricetracker.entity.User;
import com.example.retailpricetracker.repository.PriceWatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PriceWatchService {

    private final PriceWatchRepository priceWatchRepository;

    @Autowired
    public PriceWatchService(PriceWatchRepository priceWatchRepository) {
        this.priceWatchRepository = priceWatchRepository;
    }
    // Fetch product details
    @Autowired
    private ProductService productService;

    // View PriceWatch by User

    public List<PriceWatch> findPriceWatchByUser(Long userId) {
        return priceWatchRepository.findByUser_UserId(userId);
    }

    // View PriceWatch by Product and User
    public List<PriceWatch> findPriceWatchByUserAndProduct(Long userId, Long productId) {
        return priceWatchRepository.findByUser_UserIdAndProduct_ProductId(userId, productId);
    }
    // Create priceWatch
    public PriceWatch createPriceWatch(User user, Long productId, BigDecimal desiredPrice) {
        // Find the product by ID.
        Optional<Product> productOptional = productService.getProductById(productId);
        if (!productOptional.isPresent()) {
            throw new IllegalArgumentException("Product not found");
        }
        Product product = productOptional.get();
        // Create the PriceWatch entity.
        PriceWatch priceWatch = new PriceWatch();
        priceWatch.setUser(user);  // Set the authenticated user
        priceWatch.setProduct(product);  // Set the product
        priceWatch.setDesiredPrice(desiredPrice);  // Set the desired price

        // Save the PriceWatch
        return priceWatchRepository.save(priceWatch);  // Return newly created price watch
    };


    //Todo: Set up periodic time to check pricedrops from database
    // Get product price from ProductId
    // Compare price with desiredPriece
    // Send alert to User to their email
    @Transactional
    public void monitorPriceDrops() {
        List<PriceWatch> priceWatches = priceWatchRepository.findAll();
        for (PriceWatch pricewatch: priceWatches) {
            if(pricewatch.getAlertActive()) {
                Product p = pricewatch.getProduct();
                if (p.getCurrentPrice().compareTo(pricewatch.getDesiredPrice())<=0) {
                    // Send Alert
                    notifyUser(pricewatch.getUser(), p, pricewatch.getDesiredPrice());
                    // Update pricewatch alert
                    pricewatch.setAlertActive(false);
                    // Save to DB
                    priceWatchRepository.save(pricewatch);
                    System.out.println("Pricewatch updated successfully!");
                }
            }
        }
        System.out.println("Finished scanning through price watch database.");
    }


    //Todo: Send alert if pridcedrops to desired price
    private void notifyUser(User user, Product product, BigDecimal desiredPrice){
        // Todo: Send email to user
        System.out.println("Price Drops for product: " + product.getProductName() + "Notifying user: " + user.getUsername());
        System.out.println("Price has dropped to: " + product.getCurrentPrice() + " that is below your desired price: " + desiredPrice);
    }

    //Todo: Delete PriceWatch

    @Transactional
    public void deletePriceWatch(Long priceWatchId, User user) {
        Optional<PriceWatch> priceWatchOptional = priceWatchRepository.findById(priceWatchId);
        if (!priceWatchOptional.isPresent()) {
            throw new IllegalArgumentException("PriceWatch not found");
        }

        PriceWatch priceWatch = priceWatchOptional.get();
        if (!priceWatch.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Unauthorized to delete this PriceWatch");
        }

        priceWatchRepository.delete(priceWatch);
        System.out.println("Price watch is removed ");

    }

    //Todo: Update pricewatch with desired price
    @Transactional
    public PriceWatch updatePriceWatch(User user, Long priceWatchId, BigDecimal newDesiredPrice) {
        PriceWatch priceWatch = priceWatchRepository.findById(priceWatchId)
                .orElseThrow(() -> new IllegalArgumentException("PriceWatch not found"));

        if (!priceWatch.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("Unauthorized to update this PriceWatch");
        }

        priceWatch.setDesiredPrice(newDesiredPrice);
        return priceWatchRepository.save(priceWatch);
    }

    //Todo: View All PriceWatch made by User
    public List<PriceWatch> getAllPriceWatches() {
        return priceWatchRepository.findAll();
    }

    public Optional<PriceWatch> getPriceWatchById(Long id) {
        return priceWatchRepository.findById(id);
    }



}
