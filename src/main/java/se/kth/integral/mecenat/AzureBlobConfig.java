package se.kth.integral.mecenat;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class AzureBlobConfig {

  @Value("${storage.account.connection.string}")
  private String connectionString;

  @Bean
  public BlobServiceClient blobServiceClient() {
    // Replace with your connection string
    return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
  }
}