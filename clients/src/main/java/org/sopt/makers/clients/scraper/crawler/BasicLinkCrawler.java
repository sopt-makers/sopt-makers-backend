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
class BasicLinkCrawler implements LinkCrawler {

  private static final int TIMEOUT_MS = 10_000;

  @Override
  public LinkSource supportSource() {
    return LinkSource.BASIC;
  }

  @Override
  public ScraperResult crawl(String link) throws IOException {
    Document doc = fetch(link);
    return new ScraperResult(
        fetchOg(doc, "image"),
        fetchOg(doc, "title"),
        fetchOg(doc, "description"),
        fetchOg(doc, "url"));
  }

  Document fetch(String link) throws IOException {
    URLConnection conn = new URL(link).openConnection();
    conn.setConnectTimeout(TIMEOUT_MS);
    conn.setReadTimeout(TIMEOUT_MS);
    try (InputStream in = conn.getInputStream()) {
      return Jsoup.parse(in, "UTF-8", link);
    }
  }

  String fetchOg(Document doc, String property) {
    Element el = doc.selectFirst("meta[property=og:%s]".formatted(property));
    return el != null ? el.attr("content") : "";
  }
}
