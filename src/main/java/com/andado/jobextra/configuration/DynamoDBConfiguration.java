
package com.andado.jobextra.configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.util.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration parameters for the DynamoDB DAO objects
 */
@Configuration
public class DynamoDBConfiguration {
  // TODO: Specify the name of the Users table in DynamoDB
  public static final String USERS_TABLE_NAME = "users";
  // TODO: Specify the name of the Pet table in DynamoDB
  public static final String PET_TABLE_NAME = "pets";

  public static final int SCAN_LIMIT = 50;

  @Value("${amazonDynamodbEndpoint}")
  private String amazonDynamoDBEndpoint;
  @Value("${amazonDynamodbTableNamePrefix}")
  private String amazonDynamodbTableNamePrefix;
  @Value("${aws.region}")
  private String region;

  @Bean
  public AmazonDynamoDB amazonDynamoDBClient() {

    // credentials for the client come from the environment variables pre-configured by Lambda. These are tied to the
    // Lambda function execution role.
    final AmazonDynamoDBClient client = new AmazonDynamoDBClient();
    if (!StringUtils.isNullOrEmpty(region)) {
      client.setSignerRegionOverride(Regions.fromName(region).getName());
    }
    if (!StringUtils.isNullOrEmpty(amazonDynamoDBEndpoint)) {
      client.setEndpoint(amazonDynamoDBEndpoint);
    }
    return client;
  }

  @Bean
  public DynamoDBMapper mazonDynamoDBMapper() {

    final DynamoDBMapperConfig.TableNameOverride tableNameOverride = DynamoDBMapperConfig.TableNameOverride
      .withTableNamePrefix(amazonDynamodbTableNamePrefix);
    DynamoDBMapperConfig ddbMapperConfig = DynamoDBMapperConfig.builder()
      .withTableNameOverride(tableNameOverride)
      .build();
    DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient(), ddbMapperConfig);
    return mapper;
  }
}
