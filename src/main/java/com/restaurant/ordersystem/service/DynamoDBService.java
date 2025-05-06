package com.restaurant.ordersystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Order;
import com.restaurant.ordersystem.model.OrderItem;
import com.restaurant.ordersystem.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamoDBService {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBService.class);
    private static final String ORDER_ID_ATTR = "orderId";

    @Value("${aws.dynamodb.table-name:orders}")
    private String tableName;

    private DynamoDbClient dynamoDbClient;

    public DynamoDBService() {
        // Default constructor
    }

    @PostConstruct
    public void init() {
        try {
            Region region = Region.US_EAST_1;
            dynamoDbClient = DynamoDbClient.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            // Check if table exists, create if it doesn't
            createTableIfNotExists();

            logger.info("DynamoDB service initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing DynamoDB service: {}", e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try {
            // Check if table exists
            DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            try {
                dynamoDbClient.describeTable(describeTableRequest);
                logger.info("DynamoDB table '{}' already exists", tableName);
            } catch (ResourceNotFoundException e) {
                // Table doesn't exist, create it
                logger.info("Creating DynamoDB table '{}'", tableName);

                CreateTableRequest createTableRequest = CreateTableRequest.builder()
                        .tableName(tableName)
                        .keySchema(
                                KeySchemaElement.builder()
                                        .attributeName(ORDER_ID_ATTR)
                                        .keyType(KeyType.HASH)
                                        .build()
                        )
                        .attributeDefinitions(
                                AttributeDefinition.builder()
                                        .attributeName(ORDER_ID_ATTR)
                                        .attributeType(ScalarAttributeType.S)
                                        .build()
                        )
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .build();

                dynamoDbClient.createTable(createTableRequest);
                logger.info("DynamoDB table '{}' created successfully", tableName);
            }
        } catch (Exception e) {
            logger.error("Error checking/creating DynamoDB table: {}", e.getMessage());
        }
    }

    /**
     * Save order data to DynamoDB
     *
     * @param order Order object
     * @param customer Customer object
     * @param restaurant Restaurant object
     * @param orderItems List of order items
     */
    public void saveOrder(Order order, Customer customer, Restaurant restaurant, List<OrderItem> orderItems) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();

            // Order details
            item.put(ORDER_ID_ATTR, AttributeValue.builder().s(order.getOrderId()).build());
            item.put("orderStatus", AttributeValue.builder().s(order.getStatus().name()).build());
            item.put("orderDate", AttributeValue.builder().s(formatDateTime(order.getOrderDate())).build());
            item.put("deliveryDate", AttributeValue.builder().s(formatDateTime(order.getDeliveryDate())).build());

            // Customer details
            Map<String, AttributeValue> customerMap = new HashMap<>();
            customerMap.put("customerId", AttributeValue.builder().n(customer.getCustomerId().toString()).build());
            customerMap.put("fullName", AttributeValue.builder().s(customer.getFullName()).build());
            if (customer.getEmail() != null) {
                customerMap.put("email", AttributeValue.builder().s(customer.getEmail()).build());
            }
            item.put("customer", AttributeValue.builder().m(customerMap).build());

            // Restaurant details
            Map<String, AttributeValue> restaurantMap = new HashMap<>();
            restaurantMap.put("restaurantId", AttributeValue.builder().n(restaurant.getRestaurantId().toString()).build());
            restaurantMap.put("name", AttributeValue.builder().s(restaurant.getName()).build());
            item.put("restaurant", AttributeValue.builder().m(restaurantMap).build());

            // Payment details
            if (order.getPaymentId() != null) {
                item.put("paymentId", AttributeValue.builder().s(order.getPaymentId()).build());
            }

            // Order items
            List<AttributeValue> orderItemsList = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Map<String, AttributeValue> orderItemMap = new HashMap<>();
                orderItemMap.put("orderItemId", AttributeValue.builder().n(orderItem.getOrderItemId().toString()).build());

                if (orderItem.getMenuItem() != null) {
                    orderItemMap.put("menuItemId", AttributeValue.builder().n(orderItem.getMenuItem().getItemId().toString()).build());
                    orderItemMap.put("menuItemName", AttributeValue.builder().s(orderItem.getItemName() != null ?
                            orderItem.getItemName() : orderItem.getMenuItem().getName()).build());
                } else if (orderItem.getItemName() != null) {
                    orderItemMap.put("menuItemName", AttributeValue.builder().s(orderItem.getItemName()).build());
                }

                orderItemMap.put("quantity", AttributeValue.builder().n(orderItem.getQuantity().toString()).build());
                orderItemMap.put("price", AttributeValue.builder().n(orderItem.getPrice().toString()).build());
                orderItemMap.put("subtotal", AttributeValue.builder().n(orderItem.getSubtotal().toString()).build());

                if (orderItem.getSpecialInstructions() != null) {
                    orderItemMap.put("specialInstructions", AttributeValue.builder().s(orderItem.getSpecialInstructions()).build());
                }

                if (orderItem.getVariant() != null) {
                    Map<String, AttributeValue> variantMap = new HashMap<>();
                    variantMap.put("variantId", AttributeValue.builder().n(orderItem.getVariant().getVariantId().toString()).build());
                    variantMap.put("variantName", AttributeValue.builder().s(orderItem.getVariantName() != null ?
                            orderItem.getVariantName() : orderItem.getVariant().getVariantName()).build());
                    orderItemMap.put("variant", AttributeValue.builder().m(variantMap).build());
                } else if (orderItem.getVariantName() != null) {
                    Map<String, AttributeValue> variantMap = new HashMap<>();
                    variantMap.put("variantName", AttributeValue.builder().s(orderItem.getVariantName()).build());
                    orderItemMap.put("variant", AttributeValue.builder().m(variantMap).build());
                }

                orderItemsList.add(AttributeValue.builder().m(orderItemMap).build());
            }
            item.put("orderItems", AttributeValue.builder().l(orderItemsList).build());

            // Additional details
            if (order.getPickupInstructions() != null) {
                item.put("pickupInstructions", AttributeValue.builder().s(order.getPickupInstructions()).build());
            }

            if (order.getStatusHistory() != null) {
                item.put("statusHistory", AttributeValue.builder().s(order.getStatusHistory()).build());
            }

            // Save to DynamoDB
            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
            logger.info("Order {} saved to DynamoDB successfully", order.getOrderId());
        } catch (Exception e) {
            logger.error("Error saving order to DynamoDB: {}", e.getMessage());
        }
    }

    /**
     * Update order status in DynamoDB
     *
     * @param orderId Order ID
     * @param status New status
     */
    public void updateOrderStatus(String orderId, String status) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put(ORDER_ID_ATTR, AttributeValue.builder().s(orderId).build());

            Map<String, AttributeValueUpdate> updates = new HashMap<>();
            updates.put("orderStatus", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(status).build())
                    .action(AttributeAction.PUT)
                    .build());

            UpdateItemRequest request = UpdateItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .attributeUpdates(updates)
                    .build();

            dynamoDbClient.updateItem(request);
            logger.info("Order {} status updated to {} in DynamoDB", orderId, status);
        } catch (Exception e) {
            logger.error("Error updating order status in DynamoDB: {}", e.getMessage());
        }
    }

    /**
     * Get order details from DynamoDB
     *
     * @param orderId Order ID
     * @return Map of order attributes
     */
    public Map<String, AttributeValue> getOrderDetails(String orderId) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put(ORDER_ID_ATTR, AttributeValue.builder().s(orderId).build());

            GetItemRequest request = GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            GetItemResponse response = dynamoDbClient.getItem(request);

            if (response.hasItem()) {
                return response.item();
            }
        } catch (Exception e) {
            logger.error("Error retrieving order details from DynamoDB: {}", e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * Format LocalDateTime for DynamoDB
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
