package ru.springio.orders.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString();

        if (file.getOriginalFilename() != null) {
            fileName = fileName + file.getOriginalFilename();

        }

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build()
        );

        return fileName;
    }

    @SneakyThrows
    public String fileUrl(String fileName) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .bucket(bucket)
            .method(Method.GET)
            .object(fileName)
            .build()
        );
    }
}
