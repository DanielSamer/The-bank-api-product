package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.dto.BankResponse;
import com.mudson.The_bank_api_product.dto.CreditDebitRequest;
import com.mudson.The_bank_api_product.dto.EnquiryRequest;
import com.mudson.The_bank_api_product.dto.UserRequest;

public interface UserService {
    BankResponse createAccount (UserRequest userRequest);

    BankResponse balanceEnquiry (EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);


    BankResponse debitAccount(CreditDebitRequest request);
}
