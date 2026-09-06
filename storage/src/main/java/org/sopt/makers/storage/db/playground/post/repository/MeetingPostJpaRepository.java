package org.sopt.makers.storage.db.playground.post.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.storage.db.playground.post.entity.MeetingPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingPostJpaRepository extends JpaRepository<MeetingPostEntity, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT post FROM MeetingPostEntity post WHERE post.id = :postId")
  Optional<MeetingPostEntity> findByIdForUpdate(@Param("postId") Long postId);

  Page<MeetingPostEntity> findAllByMeetingId(Long meetingId, Pageable pageable);

  Page<MeetingPostEntity> findAllByMeetingIdIn(List<Long> meetingIds, Pageable pageable);

  List<MeetingPostEntity>
      findAllByMeetingIdInAndContentTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThanAndWriterIdNotOrderByCreatedAtDesc(
          List<Long> meetingIds,
          PostContentType contentType,
          LocalDateTime startAt,
          LocalDateTime endAt,
          Long writerId);

  long countByMeetingId(Long meetingId);
}
