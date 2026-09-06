package org.sopt.makers.api.controller.crew.post.dto;

import org.sopt.makers.domain.playground.post.PostMeetingImage;

public record PostMeetingImageResponse(Integer id, String url) {

  public static PostMeetingImageResponse from(PostMeetingImage image) {
    return new PostMeetingImageResponse(image.id(), image.url());
  }
}
