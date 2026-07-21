package org.sopt.makers.storage.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "org.sopt.makers.storage.db")
@EnableJpaRepositories(basePackages = "org.sopt.makers.storage.db")
@EnableJpaAuditing
public class StorageJpaConfig {}
