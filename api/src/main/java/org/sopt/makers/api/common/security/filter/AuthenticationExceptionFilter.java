package org.sopt.makers.api.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.sopt.makers.core.exception.BaseException;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@NullMarked
@Component
@RequiredArgsConstructor
public class AuthenticationExceptionFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (AuthException | TokenException e) {
      writeErrorResponse(response, e);
    }
  }

  private void writeErrorResponse(final HttpServletResponse response, final BaseException e)
      throws IOException {
    String body = objectMapper.writeValueAsString(BaseResponse.ofFailure(e.getError()));
    response.setStatus(e.getError().getStatusCode());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write(body);
  }
}
