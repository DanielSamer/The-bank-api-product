package com.mudson.The_bank_api_product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AccountInfo {
    private String accountName;
    private BigDecimal accountBalance;
    private String accountNumber;



}
