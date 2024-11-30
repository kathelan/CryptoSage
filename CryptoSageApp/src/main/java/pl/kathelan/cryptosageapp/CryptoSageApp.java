package pl.kathelan.cryptosageapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
        scanBasePackages = {
                "pl.kathelan.cryptosageapp",
                "pl.kathelan.notificationmodule",
                "pl.kathelan.commons"
        }
)
@Slf4j
public class CryptoSageApp {

    public static void main(String[] args) {
        SpringApplication.run(CryptoSageApp.class, args);
        log.info("CryptoSage App started");
    }

}
