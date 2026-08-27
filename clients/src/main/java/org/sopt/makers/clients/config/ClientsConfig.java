package org.sopt.makers.clients.config;

import org.sopt.makers.clients.alarm.AlarmProperty;
import org.sopt.makers.clients.crew.CrewProperty;
import org.sopt.makers.clients.dictionary.DictionaryProperty;
import org.sopt.makers.clients.eventbridge.EventBridgeProperty;
import org.sopt.makers.clients.s3.S3Property;
import org.sopt.makers.clients.sms.GabiaSmsProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({
  OAuthProperty.class,
  GabiaSmsProperty.class,
  S3Property.class,
  AlarmProperty.class,
  EventBridgeProperty.class,
  CrewProperty.class,
  DictionaryProperty.class
})
public class ClientsConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
