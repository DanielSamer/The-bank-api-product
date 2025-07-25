package com.mudson.The_bank_api_product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EmailDetails {

    private String recipient;

    private String messageBody;

    private String subject;

    private String attachment;

}

