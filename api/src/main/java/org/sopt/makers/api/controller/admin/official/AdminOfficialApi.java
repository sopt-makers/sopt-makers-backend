package org.sopt.makers.api.controller.admin.official;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.official.dto.AdminOfficialRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "공홈 어드민", description = "공식홈페이지 관리 API")
public interface AdminOfficialApi {

  @Operation(summary = "공홈 전체 조회")
  ResponseEntity<BaseResponse<?>> getAdminMain(Integer generation);

  @Operation(summary = "공통 탭 데이터 전달", description = "공통 정보(기수명, 브랜딩컬러, 모집일정)를 캐시에 저장합니다.")
  ResponseEntity<BaseResponse<?>> addCommonData(AdminOfficialRequest.AddCommonRequest request);

  @Operation(summary = "공통 탭 배포 확인", description = "캐시된 공통 정보를 DB에 반영합니다.")
  ResponseEntity<BaseResponse<?>> confirmCommonData(Integer generation);

  @Operation(summary = "홈 탭 데이터 전달", description = "홈 헤더 이미지, 뉴스, 후기 정보를 캐시에 저장하고 presigned URL을 반환합니다.")
  ResponseEntity<BaseResponse<?>> addHomeData(AdminOfficialRequest.AddHomeRequest request);

  @Operation(summary = "홈 탭 배포 확인", description = "캐시된 홈 탭 정보를 DB에 반영합니다.")
  ResponseEntity<BaseResponse<?>> confirmHomeData(Integer generation);

  @Operation(summary = "소개 탭 데이터 전달", description = "소개 헤더 이미지, 핵심가치, 멤버, 활동일정 정보를 캐시에 저장하고 presigned URL을 반환합니다.")
  ResponseEntity<BaseResponse<?>> addAboutData(AdminOfficialRequest.AddAboutRequest request);

  @Operation(summary = "소개 탭 배포 확인", description = "캐시된 소개 탭 정보를 DB에 반영합니다.")
  ResponseEntity<BaseResponse<?>> confirmAboutData(Integer generation);

  @Operation(summary = "모집안내 탭 데이터 전달", description = "모집 헤더 이미지, 파트 소개, 커리큘럼, FAQ 정보를 캐시에 저장하고 presigned URL을 반환합니다.")
  ResponseEntity<BaseResponse<?>> addRecruitData(AdminOfficialRequest.AddRecruitRequest request);

  @Operation(summary = "모집안내 탭 배포 확인", description = "캐시된 모집안내 탭 정보를 DB에 반영합니다.")
  ResponseEntity<BaseResponse<?>> confirmRecruitData(Integer generation);
}
