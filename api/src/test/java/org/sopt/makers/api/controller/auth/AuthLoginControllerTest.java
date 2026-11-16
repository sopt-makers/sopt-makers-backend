package org.sopt.makers.api.controller.auth;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.auth.port.TokenIssuerPort.TokenPair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthLoginControllerTest {
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
          .build();

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName("웹 로그인은 액세스 토큰과 최초 로그인 여부 및 갱신 쿠키를 반환한다")
  void logsInFromWeb(boolean firstLogin) throws Exception {
    when(facade.login("social-token", OAuthPlatform.GOOGLE))
        .thenReturn(new AuthFacade.LoginResult("access", "refresh", firstLogin));
    mvc.perform(
            post("/api/v1/auth/login/web")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"social-token\",\"authPlatform\":\"GOOGLE\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("access"))
        .andExpect(jsonPath("$.data.isFirstLogin").value(firstLogin))
        .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
        .andExpect(
            header()
                .string(
                    HttpHeaders.SET_COOKIE,
                    allOf(
                        startsWith("Refresh-Token=refresh;"),
                        containsString("Path=/"),
                        containsString("Domain=.sopt.org"),
                        containsString("Max-Age=1209600"),
                        containsString("HttpOnly"),
                        containsString("Secure"),
                        containsString("SameSite=None"))));
    verify(facade).login("social-token", OAuthPlatform.GOOGLE);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName("앱 로그인은 액세스 토큰과 갱신 토큰 및 최초 로그인 여부를 반환한다")
  void logsInFromApp(boolean firstLogin) throws Exception {
    when(facade.login("social-token", OAuthPlatform.APPLE))
        .thenReturn(new AuthFacade.LoginResult("access", "refresh", firstLogin));
    mvc.perform(
            post("/api/v1/auth/login/app")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"social-token\",\"authPlatform\":\"APPLE\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("access"))
        .andExpect(jsonPath("$.data.refreshToken").value("refresh"))
        .andExpect(jsonPath("$.data.isFirstLogin").value(firstLogin))
        .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    verify(facade).login("social-token", OAuthPlatform.APPLE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"GOOGLE", "APPLE"})
  @DisplayName("이름과 전화번호 및 소셜 인증 정보로 회원가입을 요청한다")
  void signsUp(String platform) throws Exception {
    mvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"name":"홍길동","phone":"01012345678","token":"social-token","authPlatform":"%s"}
            """
                        .formatted(platform)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data").isEmpty());
    verify(facade).signUp("social-token", "01012345678", OAuthPlatform.find(platform));
  }

  @Test
  @DisplayName("웹 갱신은 로그인에서 발급한 이름의 쿠키를 받아 다시 발급한다")
  void refreshesWithLoginCookie() throws Exception {
    when(facade.refresh("Bearer access", "refresh"))
        .thenReturn(new TokenPair("new-access", "new-refresh"));
    mvc.perform(
            post("/api/v1/auth/refresh/web")
                .header(HttpHeaders.AUTHORIZATION, "Bearer access")
                .cookie(new Cookie("Refresh-Token", "refresh")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("new-access"))
        .andExpect(
            header().string(HttpHeaders.SET_COOKIE, startsWith("Refresh-Token=new-refresh;")));
    verify(facade).refresh("Bearer access", "refresh");
  }
}
