package com.mudson.The_bank_api_product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mudson.The_bank_api_product.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
//bind the Interface to the enitites (in this case "user")

    Boolean existsByEmail(String email);
}
