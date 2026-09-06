package org.sopt.makers.domain.app.soptamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SoptampMode {

  private final boolean appjam;

  public SoptampMode(@Value("${sopt.soptamp.appjam-mode:false}") boolean appjam) {
    this.appjam = appjam;
  }

  public boolean isAppjam() {
    return appjam;
  }
}
