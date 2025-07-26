package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.dto.*;

public interface UserService {
    BankResponse createAccount (UserRequest userRequest);

    BankResponse balanceEnquiry (EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer (TransferRequest request);


}
