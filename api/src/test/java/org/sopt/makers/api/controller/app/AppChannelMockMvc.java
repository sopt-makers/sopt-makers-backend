package org.sopt.makers.api.controller.app;

import org.jspecify.annotations.Nullable;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.springframework.core.MethodParameter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 앱 채널 컨트롤러 계약 테스트용 MockMvc 팩토리. 인증 필터 없이 @CurrentUserId를 고정 값으로 주입하고, 통합 레포 표준
 * GlobalExceptionHandler(BaseResponse)를 배선한다. fortune 외 도메인(poke 등)도 이 팩토리를 재사용한다.
 */
public final class AppChannelMockMvc {

  private AppChannelMockMvc() {}

  public static MockMvc of(Object controller, Long fixedUserId) {
    return MockMvcBuilders.standaloneSetup(controller)
        .setCustomArgumentResolvers(
            fixedCurrentUserId(fixedUserId), new PageableHandlerMethodArgumentResolver())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  private static HandlerMethodArgumentResolver fixedCurrentUserId(Long userId) {
    return new HandlerMethodArgumentResolver() {
      @Override
      public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class);
      }

      @Override
      public Object resolveArgument(
          MethodParameter parameter,
          @Nullable ModelAndViewContainer mavContainer,
          NativeWebRequest webRequest,
          @Nullable WebDataBinderFactory binderFactory) {
        return userId;
      }
    };
  }
}
