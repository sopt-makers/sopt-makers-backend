package org.sopt.makers.storage.db.playground.community.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "category")
// 레거시 DB 스키마와의 호환을 위해 BaseEntity를 상속하지 않음
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "code", nullable = false)
  private CommunityCategoryCode code;

  @Enumerated(EnumType.STRING)
  @Column(name = "category_group", nullable = false)
  private CommunityCategoryGroup categoryGroup;

  @Column(name = "name")
  private String name;

  @Column(name = "content")
  private String content;

  @Column(name = "has_all")
  private Boolean hasAll;

  @Column(name = "has_blind")
  private Boolean hasBlind;

  @Column(name = "has_question")
  private Boolean hasQuestion;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent")
  private CategoryEntity parent;

  @Column(name = "display_order")
  private Integer displayOrder;

  public Category toDomain() {
    return new Category(
        id,
        code,
        categoryGroup,
        name,
        content,
        hasAll,
        hasBlind,
        hasQuestion,
        isActive,
        parent == null ? null : parent.getId(),
        displayOrder);
  }
}
