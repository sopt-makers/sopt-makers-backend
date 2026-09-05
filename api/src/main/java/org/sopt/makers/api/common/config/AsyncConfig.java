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

  public static final String CACHE_SYNC_EXECUTOR = "cacheSyncTaskExecutor";

  private static final int CACHE_SYNC_CORE_POOL_SIZE = 5;
  private static final int CACHE_SYNC_MAX_POOL_SIZE = 10;
  private static final int CACHE_SYNC_QUEUE_CAPACITY = 100;
  private static final String CACHE_SYNC_THREAD_NAME_PREFIX = "CacheSync-";

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

  @Bean(name = CACHE_SYNC_EXECUTOR)
  public Executor cacheSyncTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(CACHE_SYNC_CORE_POOL_SIZE);
    executor.setMaxPoolSize(CACHE_SYNC_MAX_POOL_SIZE);
    executor.setQueueCapacity(CACHE_SYNC_QUEUE_CAPACITY);
    executor.setThreadNamePrefix(CACHE_SYNC_THREAD_NAME_PREFIX);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  private int getCorePoolSize() {
    return environment.matchesProfiles("prod") ? PROD_CORE_POOL_SIZE : DEV_CORE_POOL_SIZE;
  }
}
