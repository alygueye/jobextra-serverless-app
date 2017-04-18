package com.andado.jobextra.function.pets;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.andado.jobextra.LocalDynamoDBCreationRule;
import com.andado.jobextra.function.LambdaPetsHandler;
import com.andado.jobextra.function.TestBase;
import com.andado.jobextra.model.pets.Pet;
import com.andado.jobextra.pojo.ListResponse;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(JUnit4.class)
public class LambdaPetsHandlerTest extends TestBase {

  // Initialize the Log4j logger.
  static final Logger LOGGER = Logger.getLogger(LambdaPetsHandlerTest.class);

  private static AwsProxyRequest input;

  @ClassRule
  public static final LocalDynamoDBCreationRule dynamoDB = new LocalDynamoDBCreationRule(Pet.class);

  @BeforeClass
  public static void createInput() throws IOException {
    // TODO: set up your sample input object here.
    input = getGson().fromJson(readResource("GetPets.json"), AwsProxyRequest.class);

  }

  private Context createContext() {
    TestContext ctx = new TestContext();

    // TODO: customize your context here if needed.
    ctx.setFunctionName("Your Function Name");

    return ctx;
  }

  @Test
  public void testHello() {
    LambdaPetsHandler handler = new LambdaPetsHandler();
    Context ctx = createContext();

    AwsProxyResponse output = handler.handleRequest(input, ctx);

    // TODO: validate output here if needed.
    if (output != null) {
      LOGGER.info(output.getBody().toString());
    }

    ListResponse result=getGson().fromJson(output.getBody(),ListResponse.class);
    Assert.assertEquals(result.getCount(),0);

  }
}
