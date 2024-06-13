package pl.kathelan.cryptosageapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CryptoSageApp {

    public static void main(String[] args) {
        SpringApplication.run(CryptoSageApp.class, args);
        log.info("CryptoSage App started");
    }

}
