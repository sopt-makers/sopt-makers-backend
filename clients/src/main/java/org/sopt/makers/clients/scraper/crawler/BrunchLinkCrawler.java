package org.sopt.makers.clients.scraper.crawler;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
class BrunchLinkCrawler extends BasicLinkCrawler {

  @Override
  public LinkSource supportSource() {
    return LinkSource.BRUNCH;
  }

  @Override
  public ScraperResult crawl(String link) throws IOException {
    Document doc = fetch(link);
    return new ScraperResult(
        "https:" + fetchOg(doc, "image"),
        fetchOg(doc, "title"),
        fetchOg(doc, "description"),
        fetchOg(doc, "url"));
  }
}
