package org.sopt.makers.storage.db.crew.adapter;

import static org.sopt.makers.domain.crew.dining.query.GetDiningsQuery.*;
import static org.sopt.makers.storage.db.crew.entity.QDiningAttendanceEntity.*;
import static org.sopt.makers.storage.db.crew.entity.QDiningRoundEntity.*;
import static org.sopt.makers.storage.db.crew.entity.QMeetingEntity.*;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.dining.query.GetDiningsQuery;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/** 회식 메인 목록 조회 (meeting 중 category = 회식) 정렬: 진행 중 우선 → 회식 일자 최신순 → 등록 최신순 */
@Repository
@RequiredArgsConstructor
public class DiningSearchRepositoryAdapter
    implements org.sopt.makers.domain.crew.dining.port.DiningSearchPort {

  private final JPAQueryFactory queryFactory;
  private final DiningMeetingRepositoryAdapter mapper;

  public List<DiningMeeting> findDinings(GetDiningsQuery queryCommand, Long userId) {
    return mapper.toDomains(
        queryFactory
            .selectFrom(meetingEntity)
            .where(conditions(queryCommand, userId))
            .orderBy(
                inProgressFirst(), meetingEntity.activityStartDate.desc(), meetingEntity.id.desc())
            .offset((long) (queryCommand.getPage() - 1) * queryCommand.getTake())
            .limit(queryCommand.getTake())
            .fetch());
  }

  public long countDinings(GetDiningsQuery queryCommand, Long userId) {
    Long count =
        queryFactory
            .select(meetingEntity.count())
            .from(meetingEntity)
            .where(conditions(queryCommand, userId))
            .fetchOne();
    return count == null ? 0 : count;
  }

  private BooleanExpression[] conditions(GetDiningsQuery queryCommand, Long userId) {
    return new BooleanExpression[] {
      meetingEntity.category.eq(MeetingCategory.DINING),
      eqStatus(queryCommand.getStatus()),
      eqPart(queryCommand.getPart()),
      containsTitle(queryCommand.getQuery()),
      queryCommand.isMyTab() ? isLeaderOrAttendee(userId) : null
    };
  }

  private BooleanExpression eqStatus(String status) {
    if (STATUS_IN_PROGRESS.equals(status)) {
      return meetingEntity.diningEndedAt.isNull();
    }
    if (STATUS_ENDED.equals(status)) {
      return meetingEntity.diningEndedAt.isNotNull();
    }
    return null;
  }

  /** 기존 모임 목록의 파트 필터와 같은 방식. 전체 파트 회식은 6개 파트를 모두 저장하므로 어떤 파트로 필터해도 포함된다. */
  private BooleanExpression eqPart(MeetingJoinablePart part) {
    if (part == null) {
      return null;
    }
    return Expressions.stringTemplate("cast({0} as string)", meetingEntity.joinableParts)
        .contains("\"" + part.name() + "\"");
  }

  private BooleanExpression containsTitle(String query) {
    if (!StringUtils.hasText(query)) {
      return null;
    }
    return meetingEntity.title.contains(query.trim());
  }

  /** 내 회식: 내가 개설한 회식 + 현재 참석 중인 회차가 1개 이상인 회식 */
  private BooleanExpression isLeaderOrAttendee(Long userId) {
    return JPAExpressions.selectOne()
        .from(org.sopt.makers.storage.db.crew.entity.QMeetingMemberEntity.meetingMemberEntity)
        .where(
            org.sopt.makers.storage.db.crew.entity.QMeetingMemberEntity.meetingMemberEntity
                .meetingId.eq(meetingEntity.id),
            org.sopt.makers.storage.db.crew.entity.QMeetingMemberEntity.meetingMemberEntity.userId
                .eq(userId),
            org.sopt.makers.storage.db.crew.entity.QMeetingMemberEntity.meetingMemberEntity.role.eq(
                org.sopt.makers.domain.crew.meeting.MemberRole.LEADER))
        .exists()
        .or(
            JPAExpressions.selectOne()
                .from(diningAttendanceEntity, diningRoundEntity)
                .where(
                    diningAttendanceEntity.roundId.eq(diningRoundEntity.id),
                    diningRoundEntity.meetingId.eq(meetingEntity.id),
                    diningAttendanceEntity.userId.eq(userId))
                .exists());
  }

  private OrderSpecifier<Integer> inProgressFirst() {
    return new CaseBuilder().when(meetingEntity.diningEndedAt.isNull()).then(0).otherwise(1).asc();
  }
}
