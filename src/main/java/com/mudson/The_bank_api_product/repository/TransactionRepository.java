package com.mudson.The_bank_api_product.repository;

import com.mudson.The_bank_api_product.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


}
