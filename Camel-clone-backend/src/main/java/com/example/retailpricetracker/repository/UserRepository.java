package com.example.retailpricetracker.repository;

import com.example.retailpricetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    //Optional<User> findByUserEmailAndPassword(String userEmail, String password);
    Optional<User> findByUserEmail(String userEmail);
    //List<User> findByLoginStatus(Boolean loginStatus);
}


