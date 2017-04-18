
package com.andado.jobextra.model.job;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEpochDate;
import com.andado.jobextra.configuration.DynamoDBConfiguration;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * The JobOffer object. This bean also contains the annotations required for the DynamoDB object mapper
 * to work
 */
@Data
@DynamoDBTable(tableName = DynamoDBConfiguration.JOBOFFER_TABLE_NAME)
public class JobOffer {

  @DynamoDBHashKey(attributeName = "postedBy")
  private String postedBy;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @DynamoDBTypeConvertedEpochDate
  @DynamoDBRangeKey(attributeName="creationDateTime")
  private Date creationDateTime;
  @DynamoDBAttribute(attributeName = "title")
  private String title;
  @DynamoDBAttribute(attributeName = "description")
  private String description;

}
