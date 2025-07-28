package com.mudson.The_bank_api_product.controller;

import com.mudson.The_bank_api_product.dto.*;
import com.mudson.The_bank_api_product.service.Impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")

@Component
public class UserController {

    @Autowired //? msh fahem awi bt3ml eh (bt5aly el UserService Class yshta8al fy el controller?)
    UserService userService;
    @Operation( //Documentation
            summary = "Create new user account",
            description = "Creating a new user and assigning an account IS"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 created"

            )


    @PostMapping //For post requests 
    public BankResponse createAccount (@RequestBody UserRequest userRequest ) {
        return userService.createAccount(userRequest);
    }


    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much the user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"

    )

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry (@RequestBody EnquiryRequest request){ //Request body used to enter parameters 
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry") //FOr get requests 
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
