package org.sopt.makers.api.common.security.filter;

import static org.sopt.makers.api.common.security.SecurityConstant.API_KEY_HEADER;
import static org.sopt.makers.api.common.security.SecurityConstant.API_KEY_SECURED_PATHS;
import static org.sopt.makers.api.common.security.SecurityConstant.SERVICE_NAME_HEADER;
import static org.sopt.makers.domain.auth.exception.AuthFailure.INVALID_API_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.security.authentication.ApiKeyAuthentication;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@NullMarked
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private final SecurityProperty securityProperty;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    String uri = request.getRequestURI();
    if (isSecuredEndpoint(uri)) {
      handleApiKeyAuthentication(request);
    }
    filterChain.doFilter(request, response);
  }

  private boolean isSecuredEndpoint(final String uri) {
    return securityProperty.api().securedEndpoints().stream().anyMatch(path -> matchesPath(uri, path))
        || API_KEY_SECURED_PATHS.stream().anyMatch(path -> matchesPath(uri, path));
  }

  private boolean matchesPath(final String uri, final String path) {
    return uri.equals(path) || uri.startsWith(path + "/");
  }

  private void handleApiKeyAuthentication(final HttpServletRequest request) {
    String apiKey = request.getHeader(API_KEY_HEADER);
    String serviceName = request.getHeader(SERVICE_NAME_HEADER);
    validateApiKey(apiKey, serviceName);
    SecurityContextHolder.getContext()
        .setAuthentication(new ApiKeyAuthentication(apiKey, serviceName));
  }

  private void validateApiKey(final String apiKey, final String serviceName) {
    boolean hasInvalidInput =
        apiKey == null || apiKey.isBlank() || serviceName == null || serviceName.isBlank();
    if (hasInvalidInput) {
      throw new AuthException(INVALID_API_KEY);
    }

    String expectedKey = securityProperty.api().keys().get(serviceName);
    if (expectedKey == null || expectedKey.isBlank()) {
      throw new AuthException(INVALID_API_KEY);
    }

    boolean isNotMatched =
        !MessageDigest.isEqual(
            apiKey.getBytes(StandardCharsets.UTF_8), expectedKey.getBytes(StandardCharsets.UTF_8));
    if (isNotMatched) {
      throw new AuthException(INVALID_API_KEY);
    }
  }
}
