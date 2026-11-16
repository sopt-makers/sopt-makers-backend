package org.sopt.makers.api.controller.auth;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SocialAccountControllerTest {
  private final AuthFacade facade = mock(AuthFacade.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new SocialAccountController(facade)).build();
  private static final String BASE = "/api/v1/social/accounts";

  @Test
  @DisplayName("전화번호로 가입한 소셜 플랫폼을 조회한다")
  void getsPlatform() throws Exception {
    when(facade.getSocialPlatform("01012345678")).thenReturn(OAuthPlatform.GOOGLE);
    mvc.perform(get(BASE + "/platform").param("phone", "01012345678"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.platform").value("GOOGLE"));
    verify(facade).getSocialPlatform("01012345678");
  }

  @Test
  @DisplayName("이름을 함께 전달해도 소셜 플랫폼을 조회한다")
  void getsPlatformWithName() throws Exception {
    when(facade.getSocialPlatform("01012345678")).thenReturn(OAuthPlatform.APPLE);
    mvc.perform(get(BASE + "/platform").param("name", "홍길동").param("phone", "01012345678"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.platform").value("APPLE"));
    verify(facade).getSocialPlatform("01012345678");
  }

  @Test
  @DisplayName("전화번호가 없으면 조회 요청을 거절한다")
  void requiresPhone() throws Exception {
    mvc.perform(get(BASE + "/platform")).andExpect(status().isBadRequest());
    verifyNoInteractions(facade);
  }

  @Test
  @DisplayName("전화번호와 토큰 및 플랫폼으로 소셜 계정을 변경한다")
  void updatesAccount() throws Exception {
    mvc.perform(
            patch(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"phone":"01012345678","token":"social-token","authPlatform":"APPLE"}
            """))
        .andExpect(status().isOk());
    verify(facade).updateSocialAccount("01012345678", "social-token", OAuthPlatform.APPLE);
  }
}
