package org.sopt.makers.clients.playground.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sopt.makers.domain.app.playground.PlaygroundRecentPost;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlaygroundRecentPostDto(
    Long id,
    Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    String category,
    String title,
    String content,
    String webLink,
    String createdAt) {

  public PlaygroundRecentPost toDomain() {
    return new PlaygroundRecentPost(
        id,
        userId,
        profileImage,
        name,
        generationAndPart,
        category,
        title,
        content,
        webLink,
        createdAt,
        false);
  }
}
