package org.sopt.makers.domain.official.soptstory.port;

import org.sopt.makers.domain.official.soptstory.ScrapedArticle;

public interface SoptStoryScraperPort {

  ScrapedArticle scrap(String articleUrl);
}
