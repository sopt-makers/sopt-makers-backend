package org.sopt.makers.domain.admin.auth.port;

public interface PasswordHashPort {

  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}
