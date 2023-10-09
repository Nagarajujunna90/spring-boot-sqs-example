package com.example.springbootsqsexample;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    String messageId;
    String message;
    String receiptHandle;
    String md5OfBody;
    String body;
}
