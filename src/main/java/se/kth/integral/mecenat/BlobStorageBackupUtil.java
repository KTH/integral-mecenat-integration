package se.kth.integral.mecenat;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
public class BlobStorageBackupUtil {

  @Value("${storage.account.connection.string}")
  private String connectionString;

  @Value("${test.filename}")
  private String fileName;

  public ByteArrayInputStream backupData(InputStream dataStream) throws IOException {

    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();

    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("mecenat-integration");

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[1024];
    while ((nRead = dataStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    buffer.flush();
    byte[] byteArray = buffer.toByteArray();

    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

    try {
      BlobClient blobClient = containerClient.getBlobClient(fileName);

      long length = byteArray.length;

      blobClient.upload(byteArrayInputStream, length, true);

    } catch (Exception e) {
      System.out.println("Error occurred while backing up to Azure Blob Storage: " + e.getMessage());
    }

    byteArrayInputStream.reset();

    return byteArrayInputStream;
  }
}
