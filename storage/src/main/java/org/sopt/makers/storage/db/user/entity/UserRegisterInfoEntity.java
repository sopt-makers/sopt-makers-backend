package org.sopt.makers.storage.db.user.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.UserRegisterInfo;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "user_register_infos")
public class UserRegisterInfoEntity extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private LocalDate birthday;

  private int generation;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Part part;

  public static UserRegisterInfoEntity create(
      String name, String phone, String email, LocalDate birthday, int generation, Part part) {
    UserRegisterInfoEntity entity = new UserRegisterInfoEntity();
    entity.name = name;
    entity.phone = phone;
    entity.email = email;
    entity.birthday = birthday;
    entity.generation = generation;
    entity.part = part;
    return entity;
  }

  public UserRegisterInfo toDomain() {
    return UserRegisterInfo.of(name, phone, email, birthday, generation, part);
  }
}
