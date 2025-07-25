package com.mudson.The_bank_api_product.controller;

import com.mudson.The_bank_api_product.dto.*;
import com.mudson.The_bank_api_product.service.Impl.UserService;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount (@RequestBody UserRequest userRequest ) {
        return userService.createAccount(userRequest);
    }

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry (@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);

    }

    @GetMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request ){
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount (@RequestBody CreditDebitRequest request ){
        return userService.debitAccount(request);

    }

    @PostMapping("/transfer")
    public BankResponse transfer (@RequestBody TransferRequest request ){
        return userService.transfer(request);
    }

}
