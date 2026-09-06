package org.sopt.makers.api.controller.admin.crew.advertisement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementImageStoragePort;
import org.sopt.makers.domain.crew.advertisement.service.AdvertisementService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminCrewAdvertisementControllerTest {

  private final AdvertisementService advertisementService = mock(AdvertisementService.class);
  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(new AdminCrewAdvertisementController(advertisementService))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  void 광고_이미지_업로드는_멀티파트를_스토리지_요청으로_변환한다() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "banner.png", "image/png", "image".getBytes());
    when(advertisementService.uploadMeetingTopImage(any()))
        .thenReturn("https://example.com/banner.png");

    mockMvc
        .perform(multipart("/api/v1/admin/crew/advertisements/meeting-top/image").file(file))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.publicUrl").value("https://example.com/banner.png"));

    verify(advertisementService)
        .uploadMeetingTopImage(any(AdvertisementImageStoragePort.UploadImage.class));
  }
}
