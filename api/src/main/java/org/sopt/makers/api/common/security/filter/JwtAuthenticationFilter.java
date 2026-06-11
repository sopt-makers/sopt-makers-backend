package org.sopt.makers.api.common.security.filter;

import static org.sopt.makers.api.common.security.SecurityConstant.JWT_WHITELIST;
import static org.sopt.makers.domain.auth.exception.AuthFailure.MISSING_AUTHORIZATION_HEADER;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.sopt.makers.api.common.security.authentication.ApiKeyAuthentication;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@NullMarked
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

  private final JwtAccessTokenService jwtAccessTokenService;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationToken = getAuthorizationToken(request);
    CustomAuthentication authentication = jwtAccessTokenService.parse(authorizationToken);
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  @Override
  public boolean shouldNotFilter(final HttpServletRequest request) {
    return isWhiteRequest(request) || isApiKeyAuthenticationExists();
  }

  private String getAuthorizationToken(final HttpServletRequest request) {
    String authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeaderValue == null) {
      throw new AuthException(MISSING_AUTHORIZATION_HEADER);
    }
    String authorizationToken = authorizationHeaderValue.trim();
    jwtAccessTokenService.validatePrefix(authorizationToken);
    return authorizationToken;
  }

  private boolean isApiKeyAuthenticationExists() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication instanceof ApiKeyAuthentication;
  }

  private boolean isWhiteRequest(final HttpServletRequest request) {
    String uri = request.getRequestURI();
    return JWT_WHITELIST.stream().anyMatch(whitePath -> isWhitePath(whitePath, uri));
  }

  private boolean isWhitePath(final String whitePath, final String uri) {
    return PATH_MATCHER.match(whitePath, uri) || PATH_MATCHER.match(whitePath + "/**", uri);
  }
}
