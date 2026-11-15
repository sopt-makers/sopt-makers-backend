package org.sopt.makers.api.controller.admin.member;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.member.service.MemberService;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MemberControllerTest {
  private final MemberService service = mock(MemberService.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new MemberController(service))
          .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  @DisplayName("기수별 회원 정보와 출석 집계를 반환한다")
  void getsMembers() throws Exception {
    when(service.countMembers(39, null)).thenReturn(1);
    when(service.getMembers(39, null, 0, 20))
        .thenReturn(List.of(new Member(10L, "홍길동", 39, Part.SERVER, 1.5f, 3, 1, 1, 2)));
    mvc.perform(get("/api/v1/members/list").param("generation", "39"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalCount").value(1))
        .andExpect(jsonPath("$.data.members[0].id").value(10))
        .andExpect(jsonPath("$.data.members[0].score").value(1.5))
        .andExpect(jsonPath("$.data.members[0].total.attendance").value(3))
        .andExpect(jsonPath("$.data.members[0].total.absent").value(1))
        .andExpect(jsonPath("$.data.members[0].total.tardy").value(1))
        .andExpect(jsonPath("$.data.members[0].total.participate").value(2));
    verify(service).getMembers(39, null, 0, 20);
  }

  @Test
  @DisplayName("기수와 파트 및 페이지 조건을 전달한다")
  void filtersMembers() throws Exception {
    when(service.getMembers(38, Part.WEB, 1, 5)).thenReturn(List.of());
    mvc.perform(
            get("/api/v1/members/list")
                .param("generation", "38")
                .param("part", "WEB")
                .param("page", "1")
                .param("size", "5"))
        .andExpect(status().isOk());
    verify(service).getMembers(38, Part.WEB, 1, 5);
    verify(service).countMembers(38, Part.WEB);
  }

  @Test
  @DisplayName("회원 목록에 기수가 없으면 400을 반환한다")
  void requiresGeneration() throws Exception {
    mvc.perform(get("/api/v1/members/list")).andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }
}
