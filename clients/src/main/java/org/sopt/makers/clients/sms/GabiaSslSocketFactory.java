package org.sopt.makers.clients.sms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

final class GabiaSslSocketFactory extends SSLSocketFactory {

  private static final String[] ENABLED_PROTOCOLS = {"TLSv1.2"};

  private final SSLSocketFactory delegate;
  private final String[] enabledCipherSuites;

  GabiaSslSocketFactory(
      final SSLSocketFactory delegate, final List<String> enabledCipherSuiteNames) {
    this.delegate = delegate;
    this.enabledCipherSuites = enabledCipherSuiteNames.toArray(String[]::new);
  }

  @Override
  public String[] getDefaultCipherSuites() {
    return enabledCipherSuites.clone();
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return enabledCipherSuites.clone();
  }

  @Override
  public Socket createSocket() throws IOException {
    return configure(delegate.createSocket());
  }

  @Override
  public Socket createSocket(
      final Socket socket, final String host, final int port, final boolean autoClose)
      throws IOException {
    return configure(delegate.createSocket(socket, host, port, autoClose));
  }

  @Override
  public Socket createSocket(final String host, final int port) throws IOException {
    return configure(delegate.createSocket(host, port));
  }

  @Override
  public Socket createSocket(
      final String host, final int port, final InetAddress localHost, final int localPort)
      throws IOException {
    return configure(delegate.createSocket(host, port, localHost, localPort));
  }

  @Override
  public Socket createSocket(final InetAddress host, final int port) throws IOException {
    return configure(delegate.createSocket(host, port));
  }

  @Override
  public Socket createSocket(
      final InetAddress address,
      final int port,
      final InetAddress localAddress,
      final int localPort)
      throws IOException {
    return configure(delegate.createSocket(address, port, localAddress, localPort));
  }

  private Socket configure(final Socket socket) {
    if (socket instanceof SSLSocket sslSocket) {
      sslSocket.setEnabledProtocols(ENABLED_PROTOCOLS);
      sslSocket.setEnabledCipherSuites(enabledCipherSuites);
    }

    return socket;
  }
}
