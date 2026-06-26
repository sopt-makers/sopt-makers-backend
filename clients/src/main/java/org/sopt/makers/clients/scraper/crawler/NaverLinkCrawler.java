package org.sopt.makers.clients.scraper.crawler;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class NaverLinkCrawler extends BasicLinkCrawler {

  private static final String NAVER_PREFIX = "https://blog.naver.com";

  @Override
  public LinkSource supportSource() {
    return LinkSource.NAVER;
  }

  @Override
  public ScraperResult crawl(String link) throws IOException {
    Document doc = fetch(link);
    Document iframe = fetchIframe(doc);
    return new ScraperResult(
        fetchOg(iframe, "image"),
        fetchOg(iframe, "title"),
        fetchOg(iframe, "description"),
        fetchOg(iframe, "url"));
  }

  private Document fetchIframe(Document doc) throws IOException {
    Element iframe = doc.selectFirst("iframe#mainFrame");
    if (iframe == null) {
      log.error("네이버 블로그 iframe이 존재하지 않습니다.");
      throw new IOException("네이버 블로그 iframe을 찾을 수 없습니다.");
    }
    String src = iframe.attr("src");
    return fetch(src.startsWith(NAVER_PREFIX) ? src : NAVER_PREFIX + src);
  }
}
