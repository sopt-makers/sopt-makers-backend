package org.sopt.makers.storage.db.app.home.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "app_service")
public class AppServiceEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "service_name", nullable = false)
  private String serviceName;

  @Column(name = "active_user", nullable = false)
  private Boolean activeUser;

  @Column(name = "inactive_user", nullable = false)
  private Boolean inactiveUser;

  @Column(name = "icon_url", columnDefinition = "TEXT")
  private String iconUrl;

  @Column(name = "deep_link", columnDefinition = "TEXT")
  private String deepLink;

  public AppService toDomain() {
    return new AppService(
        id, serviceName, activeUser, inactiveUser, iconUrl, deepLink, getCreatedAt());
  }
}
