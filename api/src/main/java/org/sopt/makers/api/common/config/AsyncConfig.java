package org.sopt.makers.api.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

  private static final int DEV_CORE_POOL_SIZE = 2;
  private static final int PROD_CORE_POOL_SIZE = 3;
  private static final String THREAD_NAME_PREFIX = "executor-";

  private final Environment environment;

  @Bean(name = "taskExecutor")
  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(getCorePoolSize());
    executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return new DelegatingSecurityContextExecutor(executor.getThreadPoolExecutor());
  }

  private int getCorePoolSize() {
    return environment.matchesProfiles("prod") ? PROD_CORE_POOL_SIZE : DEV_CORE_POOL_SIZE;
  }
}
