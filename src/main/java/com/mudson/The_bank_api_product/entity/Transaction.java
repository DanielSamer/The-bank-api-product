package com.mudson.The_bank_api_product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Getter //Automatically generates getter methods for all fields
@Setter //Automatically generates setters method for all fields, mainfash t exclude attributes, hat7tag t overwride b method fadia masalan 
@NoArgsConstructor //Generates constructor with no parameters 
@AllArgsConstructor //Generates constructor with all attributes 
@Builder//Makes you implement the builder pattern (bta3 builder().build() dah )
@Entity//Tells spring boot that thiss class should be mapped to a data base 
@Table(name = "transaction")//Specifies the data base name 

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;


}
