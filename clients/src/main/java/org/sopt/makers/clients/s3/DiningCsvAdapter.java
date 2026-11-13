package org.sopt.makers.clients.s3;

import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.DiningUser;
import org.sopt.makers.domain.crew.dining.port.DiningCsvPort;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class DiningCsvAdapter implements DiningCsvPort {
  private final S3Client client;
  private final S3Property property;

  @Override
  public String upload(List<DiningUser> attendees) {
    String key = property.baseDir() + "meeting/dining-attendees-" + UUID.randomUUID() + ".csv";
    try {
      client.putObject(
          PutObjectRequest.builder()
              .bucket(property.bucket())
              .key(key)
              .contentType("text/csv; charset=UTF-8")
              .build(),
          RequestBody.fromBytes(toCsv(attendees)));
      return String.format(
          "https://s3.%s.amazonaws.com/%s/%s", property.region(), property.bucket(), key);
    } catch (RuntimeException e) {
      throw new DiningException(DiningFailure.CSV_ERROR);
    }
  }

  static byte[] toCsv(List<DiningUser> attendees) {
    StringBuilder csv = new StringBuilder("\uFEFF");
    row(csv, "번호", "이름", "최근 기수", "연락처");
    for (int i = 0; i < attendees.size(); i++) {
      var user = attendees.get(i);
      row(
          csv,
          String.valueOf(i + 1),
          text(user.getName(), false),
          user.getRecentGeneration() == null ? "" : user.getRecentGeneration().toString(),
          text(user.getPhone(), true));
    }
    return csv.toString().getBytes(StandardCharsets.UTF_8);
  }

  private static void row(StringBuilder csv, String... cells) {
    csv.append(
            Arrays.stream(cells)
                .map(cell -> "\"" + cell.replace("\"", "\"\"") + "\"")
                .collect(java.util.stream.Collectors.joining(",")))
        .append("\n");
  }

  private static String text(String value, boolean forceText) {
    if (value == null || value.isEmpty()) return "";
    char first = value.charAt(0);
    boolean unsafe =
        "=+-@＝＋－＠".indexOf(first) >= 0
            || first <= '\u0020'
            || Character.isWhitespace(first)
            || Character.isSpaceChar(first)
            || first == '\uFEFF';
    return forceText || unsafe ? "\t" + value : value;
  }
}
