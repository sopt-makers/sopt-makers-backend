package org.sopt.makers.clients.config;

import org.sopt.makers.clients.playground.PlaygroundProperty;
import org.sopt.makers.clients.s3.S3Property;
import org.sopt.makers.clients.sms.GabiaSmsProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({OAuthProperty.class, GabiaSmsProperty.class, S3Property.class, PlaygroundProperty.class})
public class ClientsConfig {}
