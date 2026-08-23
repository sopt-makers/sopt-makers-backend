package org.sopt.makers.storage.db.playground.project.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.storage.db.playground.project.entity.ProjectEntity;
import org.sopt.makers.storage.db.playground.project.entity.QProjectEntity;
import org.sopt.makers.storage.db.playground.project.entity.QProjectMemberEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<Project> findProjects(
      Integer limit,
      Long cursor,
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation) {
    QProjectEntity project = QProjectEntity.projectEntity;

    JPAQuery<ProjectEntity> query =
        queryFactory
            .selectFrom(project)
            .where(
                ltProjectId(cursor),
                checkProjectContainsSearchWord(searchWord),
                checkProjectIsFounding(isFounding),
                checkProjectCategory(category),
                checkProjectIsAvailable(isAvailable),
                checkProjectGeneration(generation))
            .orderBy(project.id.desc());

    if (limit != null) {
      query.limit(limit);
    }

    return query.fetch().stream().map(e -> e.toDomain()).toList();
  }

  public int countAllProjects(
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation) {
    QProjectEntity project = QProjectEntity.projectEntity;

    Long count =
        queryFactory
            .select(project.id.count())
            .from(project)
            .where(
                checkProjectContainsSearchWord(searchWord),
                checkProjectIsFounding(isFounding),
                checkProjectCategory(category),
                checkProjectIsAvailable(isAvailable),
                checkProjectGeneration(generation))
            .fetchOne();

    return count == null ? 0 : count.intValue();
  }

  public int countProjectsExcludeSopkathon(Long memberId) {
    QProjectEntity project = QProjectEntity.projectEntity;
    QProjectMemberEntity projectMember = QProjectMemberEntity.projectMemberEntity;

    Long count =
        queryFactory
            .select(project.id.countDistinct())
            .from(project)
            .innerJoin(projectMember)
            .on(projectMember.projectId.eq(project.id))
            .where(projectMember.userId.eq(memberId), project.category.ne("SOPKATHON"))
            .fetchOne();

    return count == null ? 0 : count.intValue();
  }

  public List<Project> findAllProjects() {
    QProjectEntity project = QProjectEntity.projectEntity;
    return queryFactory.selectFrom(project).orderBy(project.id.desc()).fetch().stream()
        .map(ProjectEntity::toDomain)
        .toList();
  }

  public List<Project> findRandomProjects(int limit) {
    QProjectEntity project = QProjectEntity.projectEntity;

    return queryFactory
        .selectFrom(project)
        .orderBy(Expressions.numberTemplate(Double.class, "random()").asc())
        .limit(limit)
        .fetch()
        .stream()
        .map(e -> e.toDomain())
        .toList();
  }

  private BooleanExpression checkProjectContainsSearchWord(String searchWord) {
    if (searchWord == null || searchWord.trim().isEmpty()) {
      return null;
    }

    QProjectEntity project = QProjectEntity.projectEntity;
    String normalizedSearchWord = searchWord.trim().toLowerCase(Locale.ROOT);

    if (containsLikeMetaCharacter(normalizedSearchWord)) {
      return containsByLocate(project.name, normalizedSearchWord)
          .or(containsByLocate(project.summary, normalizedSearchWord))
          .or(containsByLocate(project.detail, normalizedSearchWord));
    }

    String likeSearchWord = "%" + normalizedSearchWord + "%";
    return project
        .name
        .lower()
        .like(likeSearchWord)
        .or(project.summary.lower().like(likeSearchWord))
        .or(project.detail.lower().like(likeSearchWord));
  }

  private boolean containsLikeMetaCharacter(String value) {
    return value.contains("%") || value.contains("_") || value.contains("\\");
  }

  private BooleanExpression containsByLocate(StringExpression target, String searchWord) {
    return target.lower().locate(searchWord).gt(0);
  }

  private BooleanExpression checkProjectCategory(String category) {
    if (Objects.isNull(category)) {
      return null;
    }
    return QProjectEntity.projectEntity.category.eq(category);
  }

  private BooleanExpression checkProjectIsFounding(Boolean isFounding) {
    if (Objects.isNull(isFounding)) {
      return null;
    }
    return QProjectEntity.projectEntity.isFounding.eq(isFounding);
  }

  private BooleanExpression checkProjectIsAvailable(Boolean isAvailable) {
    if (Objects.isNull(isAvailable)) {
      return null;
    }
    return QProjectEntity.projectEntity.isAvailable.eq(isAvailable);
  }

  private BooleanExpression checkProjectGeneration(Integer generation) {
    if (Objects.isNull(generation)) {
      return null;
    }
    return QProjectEntity.projectEntity.generation.eq(generation);
  }

  private BooleanExpression ltProjectId(Long projectId) {
    if (projectId == null || projectId == 0) {
      return null;
    }
    return QProjectEntity.projectEntity.id.lt(projectId);
  }
}
