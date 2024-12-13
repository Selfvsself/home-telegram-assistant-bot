package ru.selfvsself.home_telegram_assistant_bot.client;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import ru.selfvsself.home_telegram_assistant_bot.client.api.FileRepositoryApi;

import java.io.InputStream;

@Slf4j
@Service
public class MinioFileRepositoryClient implements FileRepositoryApi {

    private final MinioClient minioClient;

    public MinioFileRepositoryClient(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean putFile(String bucketName, String fileName, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public InputStream getFile(String bucketName, String fileName) {
        throw new NotImplementedException("getFile");
    }
}
