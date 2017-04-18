package com.andado.jobextra.utils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;

/**
 * Created by alygueye on 16/04/2017.
 */
 public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

  @Override
  public String convert( final LocalDateTime time ) {

    return time.toString();
  }

  @Override
  public LocalDateTime unconvert( final String stringValue ) {

    return LocalDateTime.parse(stringValue);
  }
}
