package org.sopt.makers.clients.scrap.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.sopt.makers.clients.scrap.LinkCrawler;
import org.sopt.makers.clients.scrap.LinkSource;
import org.sopt.makers.domain.official.soptstory.ScrapedArticle;
import org.springframework.stereotype.Component;

@Component
public class BrunchLinkCrawler implements LinkCrawler {

  private static final int TIMEOUT_MILLISECONDS = 10000;
  private static final String EMPTY_STRING = "";

  @Override
  public LinkSource supportSource() {
    return LinkSource.BRUNCH;
  }

  @Override
  public ScrapedArticle crawl(String link) throws IOException {
    Document target = scrap(link);
    String title = fetchMeta(target, "title");
    String url = fetchMeta(target, "url");
    String rawImage = fetchMeta(target, "image");
    String image = (rawImage != null && rawImage.startsWith("//")) ? "https:" + rawImage : rawImage;
    String description = fetchMeta(target, "description");
    return new ScrapedArticle(image, title, description, url, "브런치");
  }

  private Document scrap(String link) throws IOException {
    URLConnection connection = URI.create(link).toURL().openConnection();
    connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
    connection.setReadTimeout(TIMEOUT_MILLISECONDS);

    try (InputStream inputStream = connection.getInputStream()) {
      return Jsoup.parse(inputStream, "UTF-8", link);
    }
  }

  private String fetchMeta(Document target, String tag) {
    Element element = target.selectFirst("meta[property=og:%s]".formatted(tag));
    if (element == null) {
      return EMPTY_STRING;
    }
    return element.attr("content");
  }
}
