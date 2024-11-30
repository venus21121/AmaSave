package com.example.retailpricetracker.repository;

import com.example.retailpricetracker.entity.PriceWatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceWatchRepository extends JpaRepository<PriceWatch, Long> {
    List<PriceWatch> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
    List<PriceWatch> findByUser_UserId(Long userId);

}
