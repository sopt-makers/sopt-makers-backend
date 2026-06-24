package org.sopt.makers.clients.scraper.crawler;

import java.io.IOException;

public interface LinkCrawler {
  LinkSource supportSource();

  ScraperResult crawl(String link) throws IOException;

  record ScraperResult(String thumbnailUrl, String title, String description, String articleUrl) {}
}
