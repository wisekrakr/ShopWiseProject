package com.shopwise.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.shopwise.common.entity","com.shopwise.admin.entity.user"})
public class ShopWiseBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopWiseBackEndApplication.class, args);
	}

}
