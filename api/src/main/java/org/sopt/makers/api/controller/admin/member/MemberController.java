package org.sopt.makers.api.controller.admin.member;

import static org.sopt.makers.api.controller.admin.member.MemberSuccessCode.SUCCESS_GET_MEMBERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.member.dto.MembersGetResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.member.service.AdminMemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {

  private final AdminMemberService memberService;

  @Override
  @GetMapping("/list")
  public ResponseEntity<BaseResponse<?>> getMembers(
      @RequestParam(required = false) Part part, @RequestParam int generation, Pageable pageable) {
    int totalCount = memberService.countMembers(generation, part);
    List<Member> members =
        memberService.getMembers(
            generation, part, pageable.getPageNumber(), pageable.getPageSize());
    return ResponseFactory.success(
        SUCCESS_GET_MEMBERS, MembersGetResponse.from(totalCount, members));
  }
}
