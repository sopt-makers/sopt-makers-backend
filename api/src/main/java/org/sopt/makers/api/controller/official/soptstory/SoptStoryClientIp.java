package org.sopt.makers.api.controller.official.soptstory;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.util.StringUtils;

final class SoptStoryClientIp {
  private SoptStoryClientIp() {}

  private static final String[] IP_HEADER_CANDIDATES = {
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
  };

  public static String getClientIpAddress(HttpServletRequest request) {
    for (String header : IP_HEADER_CANDIDATES) {
      String ip = request.getHeader(header);
      if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {

        if (ip.contains(",")) {
          ip = ip.split(",")[0].trim();
        }
        return normalizeIpAddress(ip);
      }
    }

    return normalizeIpAddress(request.getRemoteAddr());
  }

  private static String normalizeIpAddress(String ip) {
    if (ip == null) {
      return "127.0.0.1";
    }

    if ("0:0:0:0:0:0:0:1".equals(ip)) {
      return "::1";
    }

    return ip;
  }

  public static boolean isValidIpAddress(String ip) {
    if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
      return false;
    }

    String ipv4Pattern =
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    return ip.matches(ipv4Pattern)
        || ip.matches(ipv6Pattern)
        || isIpv6Literal(ip)
        || "localhost".equals(ip)
        || "127.0.0.1".equals(ip)
        || "::1".equals(ip);
  }

  private static boolean isIpv6Literal(String ip) {
    if (!ip.contains(":") || !ip.matches("[0-9a-fA-F:.]+")) {
      return false;
    }
    try {
      InetAddress.getByName(ip);
      return true;
    } catch (UnknownHostException e) {
      return false;
    }
  }
}
