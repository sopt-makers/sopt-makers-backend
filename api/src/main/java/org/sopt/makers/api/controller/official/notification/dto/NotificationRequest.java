package org.sopt.makers.api.controller.official.notification.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.notification.service.NotificationService;

@NoArgsConstructor(access = PRIVATE)
public final class NotificationRequest {

  public record Register(
      @NotNull(message = "이메일은 필수입니다") @Email(message = "올바른 이메일 형식이 아닙니다") String email,
      @NotNull(message = "기수는 필수입니다") @Positive(message = "기수는 양수여야 합니다") Integer generation) {

    public NotificationService.RegisterNotificationCommand toCommand() {
      return new NotificationService.RegisterNotificationCommand(email, generation);
    }
  }
}
