package com.mudson.The_bank_api_product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class EnquiryRequest {

    private String accountNumber;
}
