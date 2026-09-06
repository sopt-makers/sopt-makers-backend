package org.sopt.makers.api.controller.app.firebase;

import static org.sopt.makers.api.controller.app.firebase.FirebaseSuccessCode.GET_FIREBASE_INFO;

import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.firebase.dto.FirebaseResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/firebase")
public class FirebaseController implements FirebaseApi {

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getFirebaseInfo() {
    return ResponseFactory.success(GET_FIREBASE_INFO, FirebaseResponse.current());
  }
}
