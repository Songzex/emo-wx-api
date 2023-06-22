package com.example.emo.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
/*@ComponentScan(basePackages = "com.example.emo.wx*")*/
public class EmoWxApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmoWxApiApplication.class, args);
    }

}
