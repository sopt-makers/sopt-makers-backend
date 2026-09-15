package org.sopt.makers.clients.playground.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sopt.makers.domain.app.playground.PlaygroundPopularPost;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlaygroundPopularPostDto(
    Long id,
    Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    int rank,
    String category,
    String title,
    String content,
    String webLink) {

  public PlaygroundPopularPost toDomain() {
    return new PlaygroundPopularPost(
        id, userId, profileImage, name, generationAndPart, rank, category, title, content, webLink);
  }
}
