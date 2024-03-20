package se.kth.integral.mecenat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class SSLConfiguration {

  @Value("${mecenat.integration.crt}")
  private String crtString;

  @Value("${mecenat.integration.key}")
  private String kyString;

  @Value("${keystore.password}")
  private String keystorePassword;

  @Bean
  public void setupSSLContext() throws Exception {
    String certString = crtString;
    String keyString = kyString;

    byte[] certBytes = Base64.getDecoder().decode(certString);
    byte[] keyBytes = Base64.getDecoder().decode(keyString);

    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));

    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    PrivateKey key = kf.generatePrivate(keySpec);

    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, null);
    keyStore.setKeyEntry("key-alias", key, keystorePassword.toCharArray(), new X509Certificate[]{cert});

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagerFactory.getKeyManagers(), null, null); // Uses Java's default truststore

    SSLContext.setDefault(sslContext);
  }
}
