package io.github.redouanebali;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

public class SSLUtils {

  public static SSLSocketFactory getUnsafeSSLSocketFactory() {
    try {
      final TrustManager[] trustAllCerts = new TrustManager[]{
          new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }
          }
      };

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static OkHttpClient getUnsafeOkHttpClient() {
    try {
      final TrustManager[] trustAllCerts = new TrustManager[]{
          new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }
          }
      };

      final SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      return new OkHttpClient.Builder()
          .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
          .hostnameVerifier((hostname, session) -> true)
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}