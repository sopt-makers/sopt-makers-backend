package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.auth.AdminAccount;
import org.sopt.makers.domain.admin.auth.AdminAccountType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "admin")
public class AdminAccountEntity extends BaseEntity {

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type")
  private AdminAccountType accountType;

  @Builder
  private AdminAccountEntity(
      String email, String password, String name, AdminAccountType accountType) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.accountType = accountType;
  }

  public void update(AdminAccount adminAccount) {
    this.email = adminAccount.email();
    this.password = adminAccount.encodedPassword();
    this.name = adminAccount.name();
    this.accountType = adminAccount.accountType();
  }

  public AdminAccount toDomain() {
    return new AdminAccount(getId(), email, password, name, accountType);
  }

  public static AdminAccountEntity from(AdminAccount adminAccount) {
    return AdminAccountEntity.builder()
        .email(adminAccount.email())
        .password(adminAccount.encodedPassword())
        .name(adminAccount.name())
        .accountType(adminAccount.accountType())
        .build();
  }
}
