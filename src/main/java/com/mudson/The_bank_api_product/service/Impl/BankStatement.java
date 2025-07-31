package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.entity.Transaction;

import java.io.FileNotFoundException;
import java.util.List;

public interface BankStatement {
    List<Transaction> generateStatement(String accountNumber) throws FileNotFoundException;
}