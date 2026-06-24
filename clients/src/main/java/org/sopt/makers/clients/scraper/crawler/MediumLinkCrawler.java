package org.sopt.makers.clients.scraper.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Slf4j
@Component
class MediumLinkCrawler implements LinkCrawler {

  private static final String FEED_URL = "https://medium.com/feed/sopt-makers";
  private static final int TIMEOUT_MS = 10_000;

  @Override
  public LinkSource supportSource() {
    return LinkSource.MEDIUM;
  }

  @Override
  public ScraperResult crawl(String link) throws IOException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      URLConnection conn = new URL(FEED_URL).openConnection();
      conn.setConnectTimeout(TIMEOUT_MS);
      conn.setReadTimeout(TIMEOUT_MS);
      Document doc;
      try (InputStream in = conn.getInputStream()) {
        doc = builder.parse(in);
      }
      NodeList items = doc.getElementsByTagName("item");
      for (int i = 0; i < items.getLength(); i++) {
        Element item = (Element) items.item(i);
        String articleLink = text(item, "link");
        if (link.equals(articleLink)) {
          return new ScraperResult(
              "",
              text(item, "title"),
              "",
              articleLink);
        }
      }
      log.error("Medium RSS에서 해당 링크를 찾을 수 없습니다: {}", link);
      return new ScraperResult("", "", "", link);
    } catch (ParserConfigurationException | SAXException e) {
      throw new IOException("Medium RSS 파싱에 실패했습니다.", e);
    }
  }

  private String text(Element el, String tag) {
    NodeList nodes = el.getElementsByTagName(tag);
    return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
  }
}
