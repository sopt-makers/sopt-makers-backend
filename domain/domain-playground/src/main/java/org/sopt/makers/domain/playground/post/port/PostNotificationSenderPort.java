package org.sopt.makers.domain.playground.post.port;

import org.sopt.makers.domain.playground.post.PostNotification;

public interface PostNotificationSenderPort {

  void send(PostNotification notification);
}
