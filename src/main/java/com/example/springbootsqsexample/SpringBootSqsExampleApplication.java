package com.example.springbootsqsexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;

@SpringBootApplication
public class SpringBootSqsExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSqsExampleApplication.class, args);
        Region region = Region.US_EAST_1;

        S3Client s3Client = S3Client.builder()
                .build();
        s3Client.createBucket(CreateBucketRequest
                .builder()
                .bucket("demobucketfun")
                .build());

        SqsClient sqsClient = SqsClient.builder().build();
        ListQueuesResponse queues = sqsClient.listQueues();
        System.out.println(queues);
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("test")
                .build();
        sqsClient.createQueue(createQueueRequest);
    }

}
