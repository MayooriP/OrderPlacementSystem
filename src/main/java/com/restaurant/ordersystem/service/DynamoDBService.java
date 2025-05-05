package com.restaurant.ordersystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DynamoDBService {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBService.class);

    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    public DynamoDBService(DynamoDbClient dynamoDbClient, ObjectMapper objectMapper) {
        this.dynamoDbClient = dynamoDbClient;
        this.objectMapper = objectMapper;
    }

    public void saveOrderToDynamoDB(OrderResponseDTO orderResponse) {
        if (orderResponse == null) {
            logger.warn("Attempted to save null order to DynamoDB");
            return;
        }

        try {
            logger.info("Saving order to DynamoDB");

            Map<String, AttributeValue> item = new HashMap<>();

            item.put("createdAt", AttributeValue.builder().s(LocalDateTime.now().toString()).build());

            String orderId = "order-" + System.currentTimeMillis();
            item.put("orderId", AttributeValue.builder().s(orderId).build());

            item.put("orderType", AttributeValue.builder().s("Food Order").build());
            item.put("orderSource", AttributeValue.builder().s("Restaurant App").build());

            try {
                String orderJson = objectMapper.writeValueAsString(orderResponse);
                item.put("orderData", AttributeValue.builder().s(orderJson).build());
            } catch (JsonProcessingException e) {
                logger.error("Error serializing order to JSON", e);
                item.put("orderData", AttributeValue.builder().s("{}").build());
            }

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(putItemRequest);
            logger.info("Successfully saved order to DynamoDB");
        } catch (Exception e) {
            logger.error("Error saving order to DynamoDB: {}", e.getMessage(), e);
        }
    }
}
