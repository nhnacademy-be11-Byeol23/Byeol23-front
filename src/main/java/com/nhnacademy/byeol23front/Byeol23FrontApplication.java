package com.nhnacademy.byeol23front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Byeol23FrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(Byeol23FrontApplication.class, args);
    }

}
