package ru.springio.orders.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {

    private final AmazonS3 amazonS3;

    @Value("${storage.bucket}")
    private String bucket;

    private final FileUrlBuilder fileUrlBuilder;

    @SneakyThrows
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString();

        if (file.getOriginalFilename() != null) {
            fileName = fileName + file.getOriginalFilename();

        }

        try {
            amazonS3.putObject(
                new PutObjectRequest(
                    bucket,
                    fileName,
                    file.getInputStream(),
                    new ObjectMetadata()
                )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    @SneakyThrows
    public String fileUrl(String fileName) {
        return fileUrlBuilder.fileUrl(fileName);
    }
}
