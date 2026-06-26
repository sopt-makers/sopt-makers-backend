package org.sopt.makers.clients.scrap.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.sopt.makers.clients.scrap.LinkCrawler;
import org.sopt.makers.clients.scrap.LinkSource;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NaverLinkCrawler implements LinkCrawler {

  private static final int TIMEOUT_MILLISECONDS = 10000;
  private static final String EMPTY_STRING = "";
  private static final String PREFIX_OF_IFRAME = "https://blog.naver.com";

  @Override
  public LinkSource supportSource() {
    return LinkSource.NAVER;
  }

  @Override
  public ScrapedArticle crawl(String link) throws IOException {
    Document target = scrap(link);
    Document iframe = scrapIframe(target);
    String title = fetchMeta(iframe, "title");
    String url = fetchMeta(iframe, "url");
    String image = fetchMeta(iframe, "image");
    String description = fetchMeta(iframe, "description");
    return new ScrapedArticle(image, title, description, url, "네이버 블로그");
  }

  private Document scrap(String link) throws IOException {
    URLConnection connection = URI.create(link).toURL().openConnection();
    connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
    connection.setReadTimeout(TIMEOUT_MILLISECONDS);

    try (InputStream inputStream = connection.getInputStream()) {
      return Jsoup.parse(inputStream, "UTF-8", link);
    }
  }

  private Document scrapIframe(Document target) throws IOException {
    Element iframe = target.selectFirst("iframe#mainFrame");
    if (iframe == null) {
      log.error("네이버 블로그 iframe이 존재하지 않습니다.");
      throw new IOException();
    }
    String iframeUrl = iframe.attr("src");
    return scrap(parseIframeUrl(iframeUrl));
  }

  private String parseIframeUrl(String iframeUrl) {
    if (iframeUrl.startsWith(PREFIX_OF_IFRAME)) {
      return iframeUrl;
    }
    return PREFIX_OF_IFRAME + iframeUrl;
  }

  private String fetchMeta(Document target, String tag) {
    Element element = target.selectFirst("meta[property=og:%s]".formatted(tag));
    if (element == null) {
      return EMPTY_STRING;
    }
    return element.attr("content");
  }
}
