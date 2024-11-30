package com.example.retailpricetracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@Table(name = "priceWatch")
public class PriceWatch {
    @Id
    @Column(name = "pricewatch_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceWatchId;

    ///fetch = FetchType.LAZY: Delays the loading of the associated entity until it's explicitly accessed.
//optional = false: Specifies that the relationship is required, i.e., the foreign key cannot be null.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Avoid lazy loading issues
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Avoid lazy loading issues
    private Product product;

    @Column(name = "desired_price", nullable = false)
    private BigDecimal desiredPrice;

    @Column(name = "is_Alert_Active", nullable = false )
    private Boolean isAlertActive = true;

    // Empty constructor
    public PriceWatch() {

    }
    // Constructor
    public PriceWatch(Long priceWatchId, User user, Product product, BigDecimal desiredPrice) {
        this.priceWatchId = priceWatchId;
        this.user = user;
        this.product = product;
        this.desiredPrice = desiredPrice;
    }
    // getters
    public Long getPriceWatchId() {
        return priceWatchId;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getDesiredPrice() {
        return desiredPrice;
    }

    public Boolean getAlertActive() {
        return isAlertActive;
    }

    // setters
    public void setPriceWatchId(Long priceWatchId) {
        this.priceWatchId = priceWatchId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setDesiredPrice(BigDecimal desiredPrice) {
        this.desiredPrice = desiredPrice;
    }

    public void setAlertActive(Boolean alertActive) {
        isAlertActive = alertActive;
    }
}
