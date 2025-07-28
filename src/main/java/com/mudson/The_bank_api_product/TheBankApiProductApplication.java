package com.mudson.The_bank_api_product;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "The Bank API Project",
				description = "Backend Rest API for a Bank project",
				version = "v1.0",
				contact = @Contact(
						name = "Daniel Samer",
						email = "daniel.samer.work@gmail.com",
						url = "https://github.com/DanielSamer/The-bank-api-product"
				),
				license = @License(
						name = "Daniel Samer - LinkedIn",
						url = "https://www.linkedin.com/in/daniel-samer-82682121a/"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "The Bank API Product documentation",
				url = "https://github.com/DanielSamer/The-bank-api-product"

		)
)
public class TheBankApiProductApplication {



	public static void main(String[] args) {
		SpringApplication.run(TheBankApiProductApplication.class, args);
	}

}
