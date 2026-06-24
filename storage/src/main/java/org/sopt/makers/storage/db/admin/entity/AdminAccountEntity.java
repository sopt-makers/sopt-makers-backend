package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.auth.AdminAccount;
import org.sopt.makers.domain.admin.auth.AdminAccountStatus;
import org.sopt.makers.domain.admin.auth.AdminRole;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "admin")
public class AdminAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "admin_id")
  private Long id;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private AdminRole adminRole;

  @Enumerated(EnumType.STRING)
  private AdminAccountStatus status;

  @Builder
  private AdminAccountEntity(
      String email, String password, String name, AdminRole adminRole, AdminAccountStatus status) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.adminRole = adminRole;
    this.status = status;
  }

  public void updatePassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public AdminAccount toDomain() {
    return new AdminAccount(id, email, password, name, adminRole, status);
  }

  public static AdminAccountEntity from(AdminAccount adminAccount) {
    return AdminAccountEntity.builder()
        .email(adminAccount.email())
        .password(adminAccount.encodedPassword())
        .name(adminAccount.name())
        .adminRole(adminAccount.adminRole())
        .status(adminAccount.status())
        .build();
  }
}
