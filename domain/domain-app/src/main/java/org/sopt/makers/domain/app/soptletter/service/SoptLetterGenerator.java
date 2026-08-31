package org.sopt.makers.domain.app.soptletter.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterColor;
import org.sopt.makers.domain.app.soptletter.SoptLetterShapeType;
import org.springframework.stereotype.Component;

@Component
public class SoptLetterGenerator {

  private static final List<Double> ROTATION_DEGREES = List.of(-10.0, 0.0, 10.0);
  private static final SoptLetterShapeType[] SHAPE_TYPES = SoptLetterShapeType.values();

  public SoptLetter generate(
      Long authorProfileId, Long topicId, String message, SoptLetterColor previousColor) {
    return new SoptLetter(
        null,
        authorProfileId,
        topicId,
        randomDegree(),
        message,
        previousColor == null ? SoptLetterColor.first() : previousColor.next(),
        randomShapeType(),
        0,
        null,
        null);
  }

  private Double randomDegree() {
    return ROTATION_DEGREES.get(ThreadLocalRandom.current().nextInt(ROTATION_DEGREES.size()));
  }

  private SoptLetterShapeType randomShapeType() {
    return SHAPE_TYPES[ThreadLocalRandom.current().nextInt(SHAPE_TYPES.length)];
  }
}
