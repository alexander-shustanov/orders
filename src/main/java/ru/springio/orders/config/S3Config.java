package ru.springio.orders.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

@Configuration
public class S3Config {

    @Value("${storage.url}")
    private String s3Url;

    @Nullable
    @Value("${storage.region:null}")
    private String region;

    @Value("${storage.access-key}")
    private String accessKey;

    @Value("${storage.secret-key}")
    private String secretKey;

    @Bean
    public AmazonS3 minioClient() {
        return AmazonS3Client.builder()
            .withCredentials(
                new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))
            )
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(
                    s3Url, region
                )
            )
            .build();
    }
}

