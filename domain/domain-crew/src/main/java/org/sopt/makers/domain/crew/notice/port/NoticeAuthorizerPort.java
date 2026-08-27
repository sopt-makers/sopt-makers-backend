package org.sopt.makers.domain.crew.notice.port;

public interface NoticeAuthorizerPort {

  boolean isAuthorized(String secretKey);
}
