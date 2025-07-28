package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.dto.TransactionDto;
import com.mudson.The_bank_api_product.entity.Transaction;
import com.mudson.The_bank_api_product.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component //? Compenent msh fahmha
public class TransactionImpl implements TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status("Success")
                .build();


        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");

    }
}
