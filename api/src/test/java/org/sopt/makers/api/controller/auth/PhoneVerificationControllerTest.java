package org.sopt.makers.api.controller.auth;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PhoneVerificationControllerTest {
  private final AuthFacade facade = mock(AuthFacade.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new AuthController(facade, mock(CookieFactory.class)))
          .build();

  @ParameterizedTest
  @EnumSource(
      value = PhoneVerificationType.class,
      names = "CHANGE_PHONE_NUMBER",
      mode = EnumSource.Mode.EXCLUDE)
  @DisplayName("사용자 ID 없이 목적별 전화번호 인증번호 발송을 요청한다")
  void createsVerification(PhoneVerificationType type) throws Exception {
    mvc.perform(
            post("/api/v1/auth/phone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"name":null,"phone":"01012345678","type":"%s"}
            """
                        .formatted(type.name())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data").isEmpty());
    verify(facade).createPhoneVerification(null, "01012345678", type);
  }

  @ParameterizedTest
  @EnumSource(value = PhoneVerificationType.class, names = "CHANGE_PHONE_NUMBER")
  @DisplayName("사용자 ID를 전달해 전화번호 변경 인증번호 발송을 요청한다")
  void createsPhoneChangeVerification(PhoneVerificationType type) throws Exception {
    mvc.perform(
            post("/api/v1/auth/phone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"userId":1,"phone":"01012345678","type":"%s"}
            """
                        .formatted(type.name())))
        .andExpect(status().isCreated());
    verify(facade).createPhoneVerification(1L, "01012345678", type);
  }

  @ParameterizedTest
  @EnumSource(PhoneVerificationType.class)
  @DisplayName("목적별 인증번호를 검증하고 이름과 전화번호를 반환한다")
  void verifiesCode(PhoneVerificationType type) throws Exception {
    when(facade.verifyPhoneCode("01012345678", "012345", type))
        .thenReturn(new AuthFacade.VerifyResult("홍길동", "01012345678"));
    mvc.perform(
            post("/api/v1/auth/verify/phone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"phone":"01012345678","code":"012345","type":"%s"}
            """
                        .formatted(type.name())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("홍길동"))
        .andExpect(jsonPath("$.data.phone").value("01012345678"));
    verify(facade).verifyPhoneCode("01012345678", "012345", type);
  }

  @ParameterizedTest
  @EnumSource(PhoneVerificationType.class)
  @DisplayName("이름 필드가 포함된 요청도 인증번호를 검증한다")
  void verifiesCodeWithName(PhoneVerificationType type) throws Exception {
    when(facade.verifyPhoneCode("01012345678", "012345", type))
        .thenReturn(new AuthFacade.VerifyResult("홍길동", "01012345678"));
    mvc.perform(
            post("/api/v1/auth/verify/phone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"name":"홍길동","phone":"01012345678","code":"012345","type":"%s"}
            """
                        .formatted(type.name())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("홍길동"));
    verify(facade).verifyPhoneCode("01012345678", "012345", type);
  }
}
