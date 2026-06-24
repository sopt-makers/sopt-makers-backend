package org.sopt.makers.api.common.security.filter;

import static org.sopt.makers.api.common.security.SecurityConstant.ADMIN;
import static org.sopt.makers.domain.admin.auth.exception.AdminAuthFailure.NOT_APPROVED_ACCOUNT;
import static org.sopt.makers.domain.admin.auth.exception.AdminAuthFailure.NOT_FOUND_ADMIN;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.domain.admin.auth.AdminAccount;
import org.sopt.makers.domain.admin.auth.exception.AdminAuthException;
import org.sopt.makers.domain.admin.auth.port.AdminAccountRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@NullMarked
@Component
@RequiredArgsConstructor
public class AdminStatusVerificationFilter extends OncePerRequestFilter {

  private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

  private final AdminAccountRepositoryPort adminAccountRepositoryPort;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof CustomAuthentication customAuth) {
      boolean isAdminToken =
          customAuth.getAuthorities().stream().anyMatch(a -> ADMIN.equals(a.getAuthority()));
      if (isAdminToken) {
        Long adminId = Long.parseLong(customAuth.getPrincipal());
        AdminAccount adminAccount =
            adminAccountRepositoryPort
                .findById(adminId)
                .orElseThrow(() -> new AdminAuthException(NOT_FOUND_ADMIN));
        if (adminAccount.isNotAllowed()) {
          throw new AdminAuthException(NOT_APPROVED_ACCOUNT);
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    return !PATH_MATCHER.match("/api/v1/admin/**", request.getRequestURI());
  }
}
