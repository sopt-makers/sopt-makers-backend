package org.sopt.makers.domain.playground.community.post.port;

public interface SopticleScraperPort {

  ScrapedSopticleArticle scrap(String url);

  record ScrapedSopticleArticle(String thumbnailUrl, String title, String description, String articleUrl) {}
}
