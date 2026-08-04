package org.sopt.makers.clients.sms;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GabiaOkHttpClientConfig {

  static final String GABIA_OK_HTTP_CLIENT = "gabiaOkHttpClient";

  @Bean(GABIA_OK_HTTP_CLIENT)
  OkHttpClient gabiaOkHttpClient() {
    return GabiaOkHttpClientFactory.create();
  }
}
