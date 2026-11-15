package org.sopt.makers.storage.support;

import org.sopt.makers.storage.config.QuerydslConfig;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(
    basePackages = {
      "org.sopt.makers.storage.db.admin.querydsl",
      "org.sopt.makers.storage.db.user.querydsl"
    })
@EntityScan(
    basePackages = {
      "org.sopt.makers.storage.db.admin.entity",
      "org.sopt.makers.storage.db.user.entity"
    })
@EnableJpaRepositories(
    basePackages = {"org.sopt.makers.storage.db.admin", "org.sopt.makers.storage.db.user"})
@EnableJpaAuditing
@Import(QuerydslConfig.class)
public class AdminJpaTestConfig {}
