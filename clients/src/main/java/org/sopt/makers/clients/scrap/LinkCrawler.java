package org.sopt.makers.clients.scrap;

import java.io.IOException;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;

public interface LinkCrawler {

  LinkSource supportSource();

  ScrapedArticle crawl(String link) throws IOException;
}
