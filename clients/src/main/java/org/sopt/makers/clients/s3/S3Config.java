package org.sopt.makers.clients.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

  @Bean
  public S3Client s3Client(S3Property property) {
    return S3Client.builder().region(Region.of(property.region())).build();
  }

  @Bean
  public S3Presigner s3Presigner(S3Property property) {
    return S3Presigner.builder().region(Region.of(property.region())).build();
  }
}
