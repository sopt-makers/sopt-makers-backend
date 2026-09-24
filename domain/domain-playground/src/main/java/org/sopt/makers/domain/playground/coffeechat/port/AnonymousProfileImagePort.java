package org.sopt.makers.domain.playground.coffeechat.port;

public interface AnonymousProfileImagePort {

  AnonymousImage getRandomImage();

  record AnonymousImage(Long id, String imageUrl) {}
}
