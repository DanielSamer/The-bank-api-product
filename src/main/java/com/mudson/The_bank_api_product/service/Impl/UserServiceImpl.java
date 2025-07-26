package com.mudson.The_bank_api_product.service.Impl;

import com.mudson.The_bank_api_product.dto.*;
import com.mudson.The_bank_api_product.entity.User;
import com.mudson.The_bank_api_product.repository.UserRepository;
import com.mudson.The_bank_api_product.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;



    @Override
    @Transactional //This annotation reverts any update happens on the database if any exception happened in the function
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new user into the bd
         * check if user already has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);
        //Send email Alert
//        EmailDetails emailDetails = EmailDetails.builder()
//                .recipient(savedUser.getEmail())
//                .subject("ACCOUNT CREATION")
//                .messageBody("Congratulations! Your account has been successfully created!!\nYour aaccount details: \n " +
//                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
//                .build();
//        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName()+ " " + savedUser.getLastName()+ " " +savedUser.getOtherName())
                        .build())
                .build();

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName()+ " "+ foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();


    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();


    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);



        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName()+ " " + userToCredit.getOtherName())
                        .accountNumber(request.getAccountNumber())
                        .build())

                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //checks if the account exists
        //checks if the amoung you interd to withdraw is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName()+ " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();

        }
    }
    @Override
    public BankResponse transfer(TransferRequest request) {
        //Get the account to debit (check if it exists)
        //check if the amount I am debiting is not greater than the current balance
        //debit the account
        //get account to credit,
        // credit the account
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User  sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance())>0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() +  " " + sourceAccountUser.getOtherName();
        userRepository.save(sourceAccountUser);

//        EmailDetails debitAlert = EmailDetails.builder()
//                .subject("DEBIT ALERT")
//                .recipient(sourceAccountUser.getEmail())
//                .messageBody("The sum of "+ request.getAmount() + " has been deducted from your account. Your current balance is " + sourceAccountUser.getAccountBalance())
//                .build();
//
//        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        //String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() +  " " + destinationAccountUser.getOtherName();
        userRepository.save(destinationAccountUser);

//        EmailDetails creditAlert = EmailDetails.builder()
//                .subject("Credit ALERT")
//                .recipient(sourceAccountUser.getEmail())
//                .messageBody("The sum of "+ request.getAmount() + " has been sent to your account from: " + sourceUsername + " Your current balance is " + destinationAccountUser.getAccountBalance())
//                .build();
//
//        emailService.sendEmailAlert(debitAlert);

        System.out.println("Debiting from: " + request.getSourceAccountNumber());
        System.out.println("Crediting to: " + request.getDestinationAccountNumber());


        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();


    }


    //Balance Enquiry, name Enquiry, credit, debit, transfer



}

//Why? User Repository w JPA w el kesa deh w ezay meno bn get el account number w el username w el last name w keda w da interface mafhoosh attributes
