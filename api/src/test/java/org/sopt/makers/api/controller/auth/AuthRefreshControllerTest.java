package org.sopt.makers.api.controller.auth;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.auth.port.TokenIssuerPort.TokenPair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthRefreshControllerTest {
  private final AuthFacade facade = mock(AuthFacade.class);
  private final SecurityProperty property =
      new SecurityProperty(
          new SecurityProperty.Api(Map.of("test", "key"), List.of("/actuator")),
          new SecurityProperty.Jwt(
              new SecurityProperty.Jwt.Secret(
                  new SecurityProperty.Jwt.Secret.Expiration(3600L, 1209600L),
                  "test-secret",
                  new SecurityProperty.Jwt.Secret.Issuer("test-issuer"))));
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new AuthController(facade, new CookieFactory(property)))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  @DisplayName("웹 토큰 갱신은 새 액세스 토큰과 갱신 쿠키를 반환한다")
  void refreshesWebTokens() throws Exception {
    when(facade.refresh("Bearer expired-access", "refresh"))
        .thenReturn(new TokenPair("new-access", "new-refresh"));
    mvc.perform(
            post("/api/v1/auth/refresh/web")
                .header(HttpHeaders.AUTHORIZATION, "Bearer expired-access")
                .cookie(new Cookie("Refresh-Token", "refresh")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("new-access"))
        .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
        .andExpect(
            header()
                .string(
                    HttpHeaders.SET_COOKIE,
                    allOf(
                        startsWith("Refresh-Token=new-refresh;"),
                        containsString("Path=/"),
                        containsString("Domain=.sopt.org"),
                        containsString("Max-Age=1209600"),
                        containsString("HttpOnly"),
                        containsString("Secure"),
                        containsString("SameSite=None"))));
    verify(facade).refresh("Bearer expired-access", "refresh");
  }

  @Test
  @DisplayName("웹 갱신 쿠키가 없으면 401을 반환한다")
  void requiresRefreshCookie() throws Exception {
    mvc.perform(
            post("/api/v1/auth/refresh/web")
                .header(HttpHeaders.AUTHORIZATION, "Bearer expired-access"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("웹 갱신 요청에 Authorization 헤더가 없으면 400을 반환한다")
  void requiresAuthorizationHeader() throws Exception {
    mvc.perform(post("/api/v1/auth/refresh/web").cookie(new Cookie("Refresh-Token", "refresh")))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("앱 토큰 갱신은 본문의 토큰을 전달하고 새 토큰 쌍을 반환한다")
  void refreshesAppTokens() throws Exception {
    when(facade.refresh("Bearer expired-access", "refresh"))
        .thenReturn(new TokenPair("new-access", "new-refresh"));
    mvc.perform(
            post("/api/v1/auth/refresh/app")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"accessToken":"Bearer expired-access","refreshToken":"refresh"}
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("new-access"))
        .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"))
        .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    verify(facade).refresh("Bearer expired-access", "refresh");
  }

  @Test
  @DisplayName("앱 갱신 요청의 본문이 없으면 400을 반환한다")
  void requiresAppBody() throws Exception {
    mvc.perform(post("/api/v1/auth/refresh/app").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }
}
