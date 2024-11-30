package com.example.retailpricetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "index_product_sku", columnList = "product_sku")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column (name = "product_name", nullable = false) // Ensure this field is never null
    private String productName;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "img_url", nullable = false) // Ensure this field is never null
    private String imgUrl;

    @Column(name = "product_sku", unique = true, nullable = false) // Ensure this field is never null
    private String productSku;

    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_detail")
    private String productDetail;

    // Empty constructor
    public Product() {

    }
    // Constructors with all parameters
    public Product(Long productId, String productName, BigDecimal currentPrice, String imgUrl, String productSku, String productCategory, String productDetail) {
        this.productId = productId;
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.imgUrl = imgUrl;
        this.productSku = productSku;
        this.productCategory = productCategory;
        this.productDetail = productDetail;
    }


    // getters,

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getProductSku() {
        return productSku;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductDetail() {
        return productDetail;
    }


// setters,

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }


// etc.

}
