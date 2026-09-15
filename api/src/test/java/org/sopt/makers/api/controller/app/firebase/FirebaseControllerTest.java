package org.sopt.makers.api.controller.app.firebase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

class FirebaseControllerTest {

  private final MockMvc mockMvc = AppChannelMockMvc.ofAnonymous(new FirebaseController());

  @Test
  void firebase_응답은_스네이크_키를_쓴다() throws Exception {
    mockMvc
        .perform(get("/api/v2/firebase"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "success": true,
                      "message": "firebase 정보 조회에 성공했습니다.",
                      "data": {
                        "iOS_force_update_version": "3.0.3",
                        "iOS_app_version": "2.2.0",
                        "android_force_update_version": "1.0.0",
                        "android_app_version": "2.0.0",
                        "notice": "안녕하세요, Makers 입니다. SOPT APP이 더 편리한 서비스 경험을 위해 개선 되었어요 ‘◡’\\n지금 바로 업데이트를 통해 더 편리하고, 안정적인 SOPT APP을 경험해보세요!\\n",
                        "img_url": null
                      }
                    }
                    """,
                    JsonCompareMode.STRICT));
  }
}
