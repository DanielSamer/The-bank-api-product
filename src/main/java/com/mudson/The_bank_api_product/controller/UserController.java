package com.mudson.The_bank_api_product.controller;

import com.mudson.The_bank_api_product.dto.BankResponse;
import com.mudson.The_bank_api_product.dto.UserRequest;
import com.mudson.The_bank_api_product.service.Impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount (@RequestBody UserRequest userRequest ) {
        return userService.createAccount(userRequest);
    }

}
