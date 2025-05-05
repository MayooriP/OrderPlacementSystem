package com.restaurant.ordersystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DynamoDBConfig {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBConfig.class);

    @Value("${aws.dynamodb.endpoint}")
    private String dynamoDbEndpoint;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.credentials.use-default-chain:true}")
    private boolean useDefaultCredentialsChain;

    @Value("${aws.accessKey:#{null}}")
    private String accessKey;

    @Value("${aws.secretKey:#{null}}")
    private String secretKey;

    @Value("${aws.dynamodb.table-name}")
    private String tableName;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder clientBuilder = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .overrideConfiguration(c -> c.retryPolicy(RetryPolicy.defaultRetryPolicy()));

        // Set endpoint if provided (for local development)
        if (dynamoDbEndpoint != null && !dynamoDbEndpoint.isEmpty()) {
            clientBuilder.endpointOverride(URI.create(dynamoDbEndpoint));
        }

        // Set credentials provider
        if (useDefaultCredentialsChain) {
            clientBuilder.credentialsProvider(DefaultCredentialsProvider.create());
            logger.info("Using default AWS credentials chain");
        } else if (accessKey != null && secretKey != null) {
            clientBuilder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)));
            logger.info("Using provided AWS credentials");
        } else {
            logger.warn("No valid AWS credentials configuration found. Falling back to default credentials chain.");
            clientBuilder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return clientBuilder.build();
    }

    @PostConstruct
    public void createTableIfNotExists() {
        DynamoDbClient dynamoDbClient = dynamoDbClient();

        try {
            // Check if table exists
            boolean tableExists = false;
            try {
                DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build();
                dynamoDbClient.describeTable(describeTableRequest);
                tableExists = true;
                logger.info("DynamoDB table '{}' already exists", tableName);
            } catch (ResourceNotFoundException e) {
                logger.info("DynamoDB table '{}' does not exist, creating it", tableName);
            }

            if (!tableExists) {
                // Create table with orderId as partition key
                List<KeySchemaElement> keySchema = new ArrayList<>();
                keySchema.add(KeySchemaElement.builder()
                        .attributeName("orderId")
                        .keyType(KeyType.HASH) // Partition key
                        .build());

                List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
                attributeDefinitions.add(AttributeDefinition.builder()
                        .attributeName("orderId")
                        .attributeType(ScalarAttributeType.S) // String type
                        .build());

                CreateTableRequest createTableRequest = CreateTableRequest.builder()
                        .tableName(tableName)
                        .keySchema(keySchema)
                        .attributeDefinitions(attributeDefinitions)
                        .billingMode(BillingMode.PAY_PER_REQUEST) // On-demand capacity
                        .build();

                dynamoDbClient.createTable(createTableRequest);
                logger.info("DynamoDB table '{}' created successfully", tableName);
            }
        } catch (Exception e) {
            logger.error("Error creating DynamoDB table: {}", e.getMessage(), e);
        }
    }
}
