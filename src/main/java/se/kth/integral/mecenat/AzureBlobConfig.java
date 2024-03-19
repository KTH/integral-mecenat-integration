package se.kth.integral.mecenat;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {

  @Bean
  public BlobServiceClient blobServiceClient() {
    // Replace with your connection string
    return new BlobServiceClientBuilder().connectionString("{{storage.account.connection.string}}").buildClient();
  }
}