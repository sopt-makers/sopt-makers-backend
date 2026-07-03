package org.sopt.makers.clients.eventbridge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.scheduler.SchedulerClient;

@Configuration
public class EventBridgeConfig {

  @Bean
  public SchedulerClient schedulerClient(EventBridgeProperty property) {
    return SchedulerClient.builder().region(Region.of(property.region())).build();
  }
}
