package com.andado.jobextra;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.Stream;

import org.junit.rules.ExternalResource;

import java.util.Arrays;

/**
 * Creates a local DynamoDB instance for testing.
 */
public class LocalDynamoDBCreationRule extends ExternalResource{

  private final Class<?>[] modelClasses;

  private DynamoDBProxyServer server;
  private AmazonDynamoDB amazonDynamoDB;
  private DynamoDBMapper dynamoDBMapper;

  public LocalDynamoDBCreationRule() {
    this.modelClasses = new Class[0];
    // This one should be copied during test-compile time. If project's basedir does not contains a folder
    // named 'native-libs' please try '$ mvn clean install' from command line first
    //System.setProperty("sqlite4java.library.path", "target/dependenciess");
  }

  public LocalDynamoDBCreationRule(final Class<?>... modelClasses) {
    this.modelClasses = modelClasses;
    // This one should be copied during test-compile time. If project's basedir does not contains a folder
    // named 'native-libs' please try '$ mvn clean install' from command line first
    //System.setProperty("sqlite4java.library.path", "target/dependenciess");
  }

  @Override
  protected void before() throws Throwable {

    try {
      final String port = getAvailablePort();
      this.server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", port});
      server.start();
      amazonDynamoDB = new AmazonDynamoDBClient();
      amazonDynamoDB.setEndpoint("http://localhost:" + port);

      dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

      Arrays.stream(modelClasses)
        .forEach(model-> {
          final CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(model);
          createTableRequest.setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
          amazonDynamoDB.createTable(createTableRequest);
        });

      listTables(amazonDynamoDB.listTables(), "DynamoDB Local over HTTP");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void after() {

    if (server == null) {
      return;
    }

    try {
      server.stop();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public AmazonDynamoDB getAmazonDynamoDB() {
    return amazonDynamoDB;
  }

  public DynamoDBMapper getDynamoDBMapper() {
    return dynamoDBMapper;
  }

  private String getAvailablePort() {
    return "8000";
  }

  public static void listTables(ListTablesResult result, String method) {
    System.out.println("found " + Integer.toString(result.getTableNames().size()) + " tables with " + method);
    for (String table : result.getTableNames()) {
      System.out.println(table);
    }
  }
}
