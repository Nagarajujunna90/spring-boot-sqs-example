package com.example.springbootsqsexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@RestController
public class SqsController {
    @Value("${credentials.access-key}")
    private String accessKey;
    @Value("${credentials.secret-key}")
    private String secretKey;
    AwsBasicCredentials awsCreds;

    SqsClient sqsClient;

    ObjectMapper objectMapper = new ObjectMapper();

    public void sqsClient() {
        sqsClient = SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials
                                .create(accessKey, secretKey)))
                .region(Region.of(Region.US_EAST_1.id()))
                .build();
    }

    @GetMapping("/")
    public void extracted() {
        sqsClient();
        ListQueuesResponse queues = this.sqsClient.listQueues();
        System.out.println(queues);
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("test2")
                .build();
        sqsClient.createQueue(createQueueRequest);
    }

    @GetMapping("/message")
    public void sendMessage() {
        sqsClient();
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName("test")
                .build();
        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("hello world Nagaraju")
                .delaySeconds(5)
                .build();

        sqsClient.sendMessage(sendMsgRequest);
    }

    @GetMapping("/receive")
    public ResponseEntity<List<software.amazon.awssdk.services.sqs.model.Message>> recieveMessage() {
        try {
            sqsClient();
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName("test")
                    .build();
            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(20)
                    .build();
            List<software.amazon.awssdk.services.sqs.model.Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            System.out.println(messages);
            return new ResponseEntity<>(messages, HttpStatus.OK);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;


    }


}
