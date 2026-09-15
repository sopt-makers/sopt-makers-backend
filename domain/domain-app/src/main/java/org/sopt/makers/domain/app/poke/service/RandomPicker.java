package org.sopt.makers.domain.app.poke.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomPicker {

  public static <T> List<T> pickRandom(List<T> data, int pickLimit) {
    int n = data.size();
    if (pickLimit <= 0 || n == 0) {
      return List.of();
    }
    if (pickLimit >= n) {
      return new ArrayList<>(data);
    }

    int[] indices = IntStream.range(0, n).toArray();
    List<T> randomList = new ArrayList<>(pickLimit);
    for (int i = 0; i < pickLimit; ++i) {
      int randomIndex = ThreadLocalRandom.current().nextInt(n - i);
      randomList.add(data.get(indices[randomIndex]));
      // Fisher-Yates 선택: 사용한 슬롯을 뒤쪽 미사용 구간의 마지막으로 대체
      indices[randomIndex] = indices[n - 1 - i];
    }
    return randomList;
  }
}
