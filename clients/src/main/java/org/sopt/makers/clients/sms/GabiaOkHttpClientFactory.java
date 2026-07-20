package org.sopt.makers.clients.sms;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.TlsVersion;

final class GabiaOkHttpClientFactory {

  private static final String DISABLED_ALGORITHMS_PROPERTY = "jdk.tls.disabledAlgorithms";
  private static final String TLS_RSA_DISABLED_ALGORITHM = "TLS_RSA_*";
  private static final String TLS_PROTOCOL = "TLSv1.2";
  private static final List<String> GABIA_CIPHER_SUITE_NAMES =
      List.of("TLS_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA");
  private static final List<CipherSuite> GABIA_CIPHER_SUITES =
      List.of(CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA);

  private GabiaOkHttpClientFactory() {}

  static OkHttpClient create() {
    try {
      removeTlsRsaDisabledAlgorithm();

      X509TrustManager trustManager = defaultX509TrustManager();
      SSLContext sslContext = SSLContext.getInstance(TLS_PROTOCOL);
      sslContext.init(null, new TrustManager[] {trustManager}, null);

      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      validateSupportedCipherSuites(sslSocketFactory);

      ConnectionSpec gabiaConnectionSpec =
          new ConnectionSpec.Builder(true)
              .tlsVersions(TlsVersion.TLS_1_2)
              .cipherSuites(GABIA_CIPHER_SUITES.toArray(CipherSuite[]::new))
              .build();

      return new OkHttpClient.Builder()
          .sslSocketFactory(
              new GabiaSslSocketFactory(sslSocketFactory, GABIA_CIPHER_SUITE_NAMES), trustManager)
          .connectionSpecs(List.of(gabiaConnectionSpec))
          .protocols(List.of(Protocol.HTTP_1_1))
          .connectTimeout(Duration.ofSeconds(30))
          .readTimeout(Duration.ofSeconds(30))
          .writeTimeout(Duration.ofSeconds(30))
          .retryOnConnectionFailure(true)
          .build();
    } catch (Exception e) {
      throw new IllegalStateException("Gabia TLS HTTP client 초기화에 실패했습니다.", e);
    }
  }

  private static void removeTlsRsaDisabledAlgorithm() {
    String disabledAlgorithms = Security.getProperty(DISABLED_ALGORITHMS_PROPERTY);

    if (disabledAlgorithms == null || disabledAlgorithms.isBlank()) {
      return;
    }

    String updatedAlgorithms =
        Arrays.stream(disabledAlgorithms.split(","))
            .map(String::trim)
            .filter(algorithm -> !TLS_RSA_DISABLED_ALGORITHM.equals(algorithm))
            .reduce((left, right) -> left + ", " + right)
            .orElse("");

    Security.setProperty(DISABLED_ALGORITHMS_PROPERTY, updatedAlgorithms);
  }

  private static X509TrustManager defaultX509TrustManager() throws NoSuchAlgorithmException {
    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

    try {
      trustManagerFactory.init((java.security.KeyStore) null);
    } catch (java.security.KeyStoreException e) {
      throw new IllegalStateException("기본 TrustManager 초기화에 실패했습니다.", e);
    }

    TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

    if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager trustManager)) {
      throw new IllegalStateException("기본 X509TrustManager를 찾을 수 없습니다.");
    }

    return trustManager;
  }

  private static void validateSupportedCipherSuites(final SSLSocketFactory sslSocketFactory) {
    List<String> supportedCipherSuites = Arrays.asList(sslSocketFactory.getSupportedCipherSuites());

    List<String> unsupportedCipherSuites =
        GABIA_CIPHER_SUITE_NAMES.stream()
            .filter(cipherSuite -> !supportedCipherSuites.contains(cipherSuite))
            .toList();

    if (!unsupportedCipherSuites.isEmpty()) {
      throw new IllegalStateException(
          "Gabia TLS Cipher Suite를 현재 JDK가 지원하지 않습니다: " + unsupportedCipherSuites);
    }
  }
}
