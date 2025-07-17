package com.mudson.The_bank_api_product.utils;

import java.time.Year;

public class AccountUtils {


    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already is registered" ;
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";



    public static String generateAccountNumber() {
        /**
         * 2025 + randomSixDigits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        //Generate a random number between min and max
        int randNumber = (int) Math.floor((Math.random() * (max - min + 1)+ min));
        /*
        Math.floor -> rounds down the answer
        we typeCast it to int as Math.random can generate decimals also
        */

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        return year + randomNumber;

    }

}
