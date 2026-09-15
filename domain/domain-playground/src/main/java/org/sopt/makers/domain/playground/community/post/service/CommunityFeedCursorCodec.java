package org.sopt.makers.domain.playground.community.post.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_CURSOR;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.post.CommunityDbCursor;
import org.sopt.makers.domain.playground.community.post.CommunityFeedCursor;
import org.springframework.stereotype.Component;

/**
 * 피드 무한스크롤 cursor를 opaque 문자열로 인코딩/디코딩한다. cursor의 내부 포맷은 공개 계약이 아니며, 클라이언트는 응답의 nextCursor 값을 그대로
 * 되돌려주기만 하면 된다.
 */
@Component
public class CommunityFeedCursorCodec {

  private static final String EMPTY_TOKEN = "-";
  private static final String DELIMITER = "\\|";

  public CommunityFeedCursor decodeOrInitial(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return CommunityFeedCursor.initial(LocalDateTime.now());
    }

    try {
      String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
      String[] parts = raw.split(DELIMITER, -1);

      if (parts.length != 4) {
        throw new IllegalArgumentException("invalid cursor part count");
      }

      LocalDateTime snapshotTime = LocalDateTime.parse(parts[0]);
      LocalDateTime communityCreatedAt =
          EMPTY_TOKEN.equals(parts[1]) ? null : LocalDateTime.parse(parts[1]);
      Long communityPostId = EMPTY_TOKEN.equals(parts[2]) ? null : Long.valueOf(parts[2]);
      Integer meetingConsumedCount = Integer.valueOf(parts[3]);

      CommunityDbCursor communityCursor =
          communityCreatedAt == null || communityPostId == null
              ? null
              : new CommunityDbCursor(communityCreatedAt, communityPostId);

      return new CommunityFeedCursor(snapshotTime, communityCursor, meetingConsumedCount);
    } catch (Exception e) {
      throw new CommunityException(INVALID_CURSOR);
    }
  }

  public String encode(CommunityFeedCursor cursor) {
    String communityCreatedAt =
        cursor.community() == null ? EMPTY_TOKEN : cursor.community().createdAt().toString();
    String communityPostId =
        cursor.community() == null ? EMPTY_TOKEN : String.valueOf(cursor.community().postId());

    String raw =
        String.join(
            "|",
            cursor.snapshotTime().toString(),
            communityCreatedAt,
            communityPostId,
            String.valueOf(cursor.safeMeetingConsumedCount()));

    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }
}
