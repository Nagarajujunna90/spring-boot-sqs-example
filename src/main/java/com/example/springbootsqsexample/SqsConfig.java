package com.example.springbootsqsexample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsConfig {
    @Value("${credentials.access-key}")
    private String accessKey;
    @Value("${credentials.secret-key}")
    private String secretKey;
    AwsBasicCredentials awsCreds;

    @Bean
    public SqsClient sqsClient(SqsConfig sqsConfig) {
        SqsClient sqs = SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials
                                .create(accessKey, secretKey)))
                .region(Region.of(Region.US_EAST_1.id()))
                .build();
        return sqs;
    }

}
