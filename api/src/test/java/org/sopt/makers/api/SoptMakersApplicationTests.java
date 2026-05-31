package org.sopt.makers.api;

import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.auth.port.OAuthAuthenticatorPort;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class SoptMakersApplicationTests {

  @MockitoBean OAuthAuthenticatorPort oAuthAuthenticatorPort;
  @MockitoBean TokenIssuerPort tokenIssuerPort;
  @MockitoBean SmsSenderPort smsSenderPort;

  @Test
  void contextLoads() {}
}
