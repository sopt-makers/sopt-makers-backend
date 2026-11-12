package org.sopt.makers.api.controller.official.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.sopt.makers.domain.official.project.type.ProjectSortType;
import org.springframework.http.ResponseEntity;

@Tag(name = "프로젝트", description = "공식 홈페이지 프로젝트 API")
public interface ProjectApi {

  @Operation(summary = "프로젝트 목록 조회")
  ResponseEntity<BaseResponse<?>> getProjects(
      @Parameter(description = "프로젝트 카테고리") ProjectCategory filter,
      @Parameter(description = "서비스 형태") ProjectServiceType platform,
      @Parameter(description = "기수", example = "36") Integer generation,
      @Parameter(description = "서비스 이용 가능 여부. 값이 없는 프로젝트는 false로 취급") Boolean isAvailable,
      @Parameter(description = "창업 여부. 값이 없는 프로젝트는 false로 취급") Boolean isFounding,
      @Parameter(description = "정렬 기준. 기본값 GENERATION") ProjectSortType sortBy,
      @Parameter(description = "정렬 방향. 기본값 DESC. 정렬 값이 없는 프로젝트는 방향과 무관하게 마지막")
          ProjectSortDirection sortDirection,
      @Parameter(description = "1부터 시작하는 페이지 번호", example = "1") @Min(1) int pageNo,
      @Parameter(description = "한 페이지 크기. 1에서 100 사이", example = "10") @Min(1) @Max(100) int limit);

  @Operation(summary = "프로젝트 상세 조회")
  ResponseEntity<BaseResponse<?>> getProjectDetail(Long projectId);
}
