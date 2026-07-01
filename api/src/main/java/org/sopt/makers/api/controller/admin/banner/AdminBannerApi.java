package org.sopt.makers.api.controller.admin.banner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.banner.dto.BannerRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "앱 배너", description = "앱 배너 API")
public interface AdminBannerApi {

  @Operation(summary = "배너 상세 조회")
  ResponseEntity<BaseResponse<?>> getBannerDetail(Long bannerId);

  @Operation(summary = "배너 목록 조회")
  ResponseEntity<BaseResponse<?>> getBanners(
      String status, String sort, Integer page, Integer limit);

  @Operation(summary = "배너 삭제", description = "게시 종료(DONE) 상태의 배너는 삭제할 수 없습니다.")
  ResponseEntity<BaseResponse<?>> deleteBanner(Long bannerId);

  @Operation(
      summary = "게시 중인 외부 배너 리스트 조회",
      responses = {@ApiResponse(responseCode = "200", description = "게시 중인 외부 배너 리스트 조회 성공")},
      parameters = {
        @Parameter(
            name = "location",
            description = "배너 게시 위치",
            required = true,
            schema =
                @Schema(
                    type = "string",
                    allowableValues = {"pg_community", "cr_main", "cr_feed"})),
        @Parameter(name = "api-key", description = "API 인증 키", in = ParameterIn.HEADER)
      })
  ResponseEntity<BaseResponse<?>> getExternalBanners(String location, String apiKey);

  @Operation(
      summary = "배너 생성",
      requestBody =
          @RequestBody(
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = BannerRequest.BannerCreateOrModify.class))))
  ResponseEntity<BaseResponse<?>> createBanner(
      String location,
      String contentType,
      String publisher,
      String startDate,
      String endDate,
      String link,
      MultipartFile imagePc,
      MultipartFile imageMobile);

  @Operation(
      summary = "배너 수정",
      description = "게시 종료(DONE) 상태의 배너는 수정할 수 없습니다.",
      requestBody =
          @RequestBody(
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = BannerRequest.BannerCreateOrModify.class))))
  ResponseEntity<BaseResponse<?>> updateBanner(
      Long bannerId,
      String location,
      String contentType,
      String publisher,
      String startDate,
      String endDate,
      String link,
      MultipartFile imagePc,
      MultipartFile imageMobile);
}
