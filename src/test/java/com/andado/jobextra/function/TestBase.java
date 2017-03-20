package com.andado.jobextra.function;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import org.junit.Before;

import java.io.IOException;

public class TestBase {

  protected DynamoDBProxyServer server;

  public static Gson getGson() {
    return new GsonBuilder()
      //.enableComplexMapKeySerialization()
      //.serializeNulls()
      //.setDateFormat(DateFormat.LONG)
      //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
      .setPrettyPrinting()
      .create();
  }

  public static String readResource(final String fileName) throws IOException {
    return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
  }


}
