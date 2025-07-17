package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.dto.BankResponse;
import com.mudson.The_bank_api_product.dto.UserRequest;

public interface UserService {
    BankResponse createAccount (UserRequest userRequest);

}
