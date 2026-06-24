package org.sopt.makers.clients.scraper.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
class VelogMainLinkCrawler implements LinkCrawler {

  private static final int TIMEOUT_MS = 10_000;

  @Override
  public LinkSource supportSource() {
    return LinkSource.VELOG_MAIN;
  }

  @Override
  public ScraperResult crawl(String link) throws IOException {
    URLConnection conn = new URL(link).openConnection();
    conn.setConnectTimeout(TIMEOUT_MS);
    conn.setReadTimeout(TIMEOUT_MS);
    Document doc;
    try (InputStream in = conn.getInputStream()) {
      doc = Jsoup.parse(in, "UTF-8", link);
    }
    String title = "";
    Element titleEl = doc.selectFirst("head > title");
    if (titleEl != null) {
      title = titleEl.text();
    }
    String image = "";
    Element imageEl = doc.selectFirst("a[href*=/posts/] img");
    if (imageEl != null) {
      image = imageEl.attr("src");
    }
    String description = "";
    Element descEl = doc.selectFirst("a[href*=/posts/] p");
    if (descEl != null) {
      description = descEl.text();
    }
    return new ScraperResult(image, title, description, link);
  }
}
