package org.sopt.makers.domain.auth.port;

public interface SmsSenderPort {

  void send(String phoneNumber, String message);
}
