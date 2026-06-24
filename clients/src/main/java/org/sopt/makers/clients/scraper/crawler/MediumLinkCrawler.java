package org.sopt.makers.clients.scraper.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class MediumLinkCrawler implements LinkCrawler {

  private static final String FEED_URL = "https://medium.com/feed/sopt-makers";
  private static final int TIMEOUT_MS = 10_000;
  private static final Pattern ARTICLE_ID_PATTERN = Pattern.compile("([0-9a-f]{12})/?(?:\\?.*)?$");

  @Override
  public LinkSource supportSource() {
    return LinkSource.MEDIUM;
  }

  @Override
  public ScraperResult crawl(String link) throws IOException {
    String articleId = extractArticleId(link);
    Document rss = fetchRss();

    Element item =
        rss.select("item").stream()
            .filter(
                i -> {
                  Element guid = i.selectFirst("guid");
                  return guid != null && guid.text().endsWith(articleId);
                })
            .findFirst()
            .orElseThrow(() -> new IOException("Medium RSS에서 글을 찾을 수 없습니다: " + articleId));

    String title = text(item, "title");
    Document contentDoc = Jsoup.parse(text(item, "content|encoded"));

    return new ScraperResult(extractThumbnail(contentDoc), title, extractDescription(contentDoc), link);
  }

  private String extractArticleId(String link) throws IOException {
    Matcher matcher = ARTICLE_ID_PATTERN.matcher(link.trim());
    if (!matcher.find()) {
      throw new IOException("Medium URL에서 article ID를 추출할 수 없습니다: " + link);
    }
    return matcher.group(1);
  }

  private Document fetchRss() throws IOException {
    URLConnection conn = new URL(FEED_URL).openConnection();
    conn.setConnectTimeout(TIMEOUT_MS);
    conn.setReadTimeout(TIMEOUT_MS);
    try (InputStream in = conn.getInputStream()) {
      return Jsoup.parse(in, "UTF-8", FEED_URL, Parser.xmlParser());
    }
  }

  private String text(Element el, String selector) {
    Element found = el.selectFirst(selector);
    return found != null ? found.text() : "";
  }

  private String extractThumbnail(Document contentDoc) {
    Element img = contentDoc.selectFirst("figure img");
    if (img == null) {
      img = contentDoc.selectFirst("img");
    }
    return img != null ? img.attr("src") : "";
  }

  private String extractDescription(Document contentDoc) {
    Element p = contentDoc.selectFirst("p");
    return p != null ? p.text() : "";
  }
}
