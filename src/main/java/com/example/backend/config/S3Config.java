package com.example.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        Dotenv dotenv = Dotenv.load();
        
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
            dotenv.get("AWS_ACCESS_KEY_ID"),
            dotenv.get("AWS_SECRET_ACCESS_KEY")
        );

        return S3Client.builder()
                .region(Region.of(dotenv.get("AWS_REGION")))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    @Bean
    public String bucketName() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("AWS_S3_BUCKET");
    }
}