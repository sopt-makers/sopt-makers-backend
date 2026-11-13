package org.sopt.makers.storage.support;

import org.sopt.makers.storage.config.QuerydslConfig;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "org.sopt.makers.storage.db.admin.querydsl")
@EntityScan(basePackages = "org.sopt.makers.storage.db.admin.entity")
@EnableJpaRepositories(basePackages = "org.sopt.makers.storage.db.admin")
@EnableJpaAuditing
@Import(QuerydslConfig.class)
public class AdminJpaTestConfig {}
