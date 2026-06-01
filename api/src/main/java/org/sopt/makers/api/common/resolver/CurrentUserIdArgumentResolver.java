package org.sopt.makers.api.common.resolver;

import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@NullMarked
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(CurrentUserId.class)
        && parameter.getParameterType().equals(Long.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) {
    Authentication rawAuth = SecurityContextHolder.getContext().getAuthentication();
    boolean isNotAuthenticated =
        !(SecurityContextHolder.getContext().getAuthentication() instanceof CustomAuthentication);

    if (isNotAuthenticated) {
      throw new IllegalStateException("No authenticated user");
    }

    CustomAuthentication authentication = (CustomAuthentication) Objects.requireNonNull(rawAuth);
    return Long.parseLong(authentication.getPrincipal());
  }
}
