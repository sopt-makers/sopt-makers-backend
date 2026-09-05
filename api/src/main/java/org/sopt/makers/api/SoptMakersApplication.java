package org.sopt.makers.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(scanBasePackages = "org.sopt.makers")
public class SoptMakersApplication {

  public static void main(String[] args) {
    SpringApplication.run(SoptMakersApplication.class, args);
  }
}
