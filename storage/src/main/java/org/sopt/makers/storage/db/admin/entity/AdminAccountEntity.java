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
import org.sopt.makers.domain.admin.auth.AdminAccountType;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "admin_accounts")
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

  public void updatePassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public AdminAccount toDomain() {
    return new AdminAccount(id, email, password, name, accountType);
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
