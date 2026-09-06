package org.sopt.makers.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.sopt.makers")
public class SoptMakersApplication {

  public static void main(String[] args) {
    SpringApplication.run(SoptMakersApplication.class, args);
  }
}
