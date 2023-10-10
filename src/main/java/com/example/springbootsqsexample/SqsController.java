package com.example.springbootsqsexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/createQueue")
    public void extracted() {
        sqsClient();
        ListQueuesResponse queues = this.sqsClient.listQueues();
        System.out.println(queues);

        //one way
        Map<QueueAttributeName, String> attribute = new HashMap<>();
        attribute.put(QueueAttributeName.DELAY_SECONDS, "10");
        //2nd way
        Map<String, String> attributeWithString = new HashMap<>();
        attributeWithString.put(QueueAttributeName.DELAY_SECONDS.toString(), "900");

        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("test3")
                .attributes(attribute)
                .attributesWithStrings(attributeWithString)
                .build();
        sqsClient.createQueue(createQueueRequest);
    }
    @GetMapping("/deleteQueue")
    public void deleteQueue() {
        sqsClient();
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().maxResults(12).queueNamePrefix("aa").build();
        ListQueuesResponse listQueuesResponse = this.sqsClient.listQueues(listQueuesRequest);
        System.out.println(listQueuesResponse);
    }

    @GetMapping("/sendMessage")
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

    @GetMapping("/sendMessageBatch")
    public void sendMessageBatch() {
        sqsClient();
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName("test")
                .build();
        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
        SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
                        SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
                .build();
        sqsClient.sendMessageBatch(sendMessageBatchRequest);
    }

    @GetMapping("/receiveMessage")
    public ResponseEntity<List<software.amazon.awssdk.services.sqs.model.Message>> recieveMessage() {
        List<Message> messages = null;
        try {
            sqsClient();
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder().queueName("test").build();
            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest
                    .builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(20).build();
            messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
            System.out.println(messages);
            for (software.amazon.awssdk.services.sqs.model.Message message : messages) {
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest
                        .builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteMessageRequest);
            }
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);

    }
}
