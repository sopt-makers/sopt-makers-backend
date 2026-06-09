package org.sopt.makers.clients.config;

import org.sopt.makers.clients.sms.GabiaSmsProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OAuthProperty.class, GabiaSmsProperty.class})
public class ClientsConfig {}
