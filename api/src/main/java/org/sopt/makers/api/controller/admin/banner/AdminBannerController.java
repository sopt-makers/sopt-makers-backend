package org.sopt.makers.api.controller.admin.banner;

import static org.sopt.makers.api.controller.admin.banner.AdminBannerSuccessCode.SUCCESS_CREATE_BANNER;
import static org.sopt.makers.api.controller.admin.banner.AdminBannerSuccessCode.SUCCESS_DELETE_BANNER;
import static org.sopt.makers.api.controller.admin.banner.AdminBannerSuccessCode.SUCCESS_GET_BANNER_DETAIL;
import static org.sopt.makers.api.controller.admin.banner.AdminBannerSuccessCode.SUCCESS_GET_BANNER_LIST;
import static org.sopt.makers.api.controller.admin.banner.AdminBannerSuccessCode.SUCCESS_GET_EXTERNAL_BANNERS;
import static org.sopt.makers.api.controller.admin.banner.AdminBannerSuccessCode.SUCCESS_UPDATE_BANNER;
import static org.sopt.makers.domain.auth.exception.AuthFailure.INVALID_API_KEY;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.banner.dto.BannerRequest;
import org.sopt.makers.api.controller.admin.banner.dto.BannerResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.admin.banner.service.BannerService;
import org.sopt.makers.domain.admin.banner.service.BannerService.FilterCriteria;
import org.sopt.makers.domain.admin.banner.service.BannerService.SortCriteria;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/banners")
public class AdminBannerController implements AdminBannerApi {

  private static final int DEFAULT_PAGE = 1;
  private static final int FALLBACK_LIMIT = 20;

  private final BannerService bannerService;

  @Value("${official.apikey:}")
  private String officialApiKey;

  @Override
  @GetMapping("/{bannerId}")
  public ResponseEntity<BaseResponse<?>> getBannerDetail(@PathVariable Long bannerId) {
    return ResponseFactory.success(
        SUCCESS_GET_BANNER_DETAIL,
        BannerResponse.BannerDetail.from(bannerService.getBannerDetail(bannerId)));
  }

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<?>> createBanner(
      @RequestPart("location") String location,
      @RequestPart("content_type") String contentType,
      @RequestPart("publisher") String publisher,
      @RequestPart("start_date") String startDate,
      @RequestPart("end_date") String endDate,
      @RequestPart(name = "link", required = false) String link,
      @RequestPart("image_pc") MultipartFile imagePc,
      @RequestPart("image_mobile") MultipartFile imageMobile) {
    BannerRequest.BannerCreateOrModify request =
        new BannerRequest.BannerCreateOrModify(
            location, contentType, publisher, startDate, endDate, link, imagePc, imageMobile);
    return ResponseFactory.success(
        SUCCESS_CREATE_BANNER,
        BannerResponse.BannerDetail.from(bannerService.createBanner(request.toCommand())));
  }

  @Override
  @PutMapping(value = "/{bannerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<?>> updateBanner(
      @PathVariable Long bannerId,
      @RequestPart("location") String location,
      @RequestPart("content_type") String contentType,
      @RequestPart("publisher") String publisher,
      @RequestPart("start_date") String startDate,
      @RequestPart("end_date") String endDate,
      @RequestPart(name = "link", required = false) String link,
      @RequestPart("image_pc") MultipartFile imagePc,
      @RequestPart("image_mobile") MultipartFile imageMobile) {
    BannerRequest.BannerCreateOrModify request =
        new BannerRequest.BannerCreateOrModify(
            location, contentType, publisher, startDate, endDate, link, imagePc, imageMobile);
    return ResponseFactory.success(
        SUCCESS_UPDATE_BANNER,
        BannerResponse.BannerDetail.from(
            bannerService.updateBanner(bannerId, request.toCommand())));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getBanners(
      @RequestParam(value = "status", required = false, defaultValue = "all") String status,
      @RequestParam(value = "sort", required = false, defaultValue = "status") String sort,
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
    int pageValue = page != null && page > 0 ? page : DEFAULT_PAGE;
    int limitValue = limit != null && limit > 0 ? limit : FALLBACK_LIMIT;
    return ResponseFactory.success(
        SUCCESS_GET_BANNER_LIST,
        BannerResponse.BannerPage.from(
            bannerService.getBannersWithPagination(
                FilterCriteria.fromParameter(status),
                SortCriteria.fromParameter(sort),
                pageValue,
                limitValue)));
  }

  @Override
  @DeleteMapping("/{bannerId}")
  public ResponseEntity<BaseResponse<?>> deleteBanner(@PathVariable Long bannerId) {
    bannerService.deleteBanner(bannerId);
    return ResponseFactory.success(SUCCESS_DELETE_BANNER);
  }

  @Override
  @GetMapping("/images")
  public ResponseEntity<BaseResponse<?>> getExternalBanners(
      @RequestParam("location") String location,
      @RequestHeader(value = "api-key", required = false) String apiKey) {
    validateOfficialApiKey(apiKey);
    return ResponseFactory.success(
        SUCCESS_GET_EXTERNAL_BANNERS,
        bannerService.getExternalBanners(location).stream()
            .map(BannerResponse.BannerImageWithBothPlatforms::from)
            .toList());
  }

  private void validateOfficialApiKey(String apiKey) {
    if (apiKey == null || apiKey.isBlank() || !officialApiKey.equals(apiKey)) {
      throw new AuthException(INVALID_API_KEY);
    }
  }
}
