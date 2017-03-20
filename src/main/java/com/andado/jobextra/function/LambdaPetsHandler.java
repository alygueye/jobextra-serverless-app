
package com.andado.jobextra.function;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.andado.jobextra.configuration.PetStoreSpringAppConfig;

import org.apache.log4j.Logger;

/**
 * Created by bulianis on 12/13/16.
 */
public class LambdaPetsHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
  SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
  boolean isinitialized = false;

  // Initialize the Log4j logger.
  static final Logger LOG = Logger.getLogger(LambdaPetsHandler.class);

  public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
    if (!isinitialized) {
      isinitialized = true;
      try {
        handler = SpringLambdaContainerHandler.getAwsProxyHandler(PetStoreSpringAppConfig.class);
      } catch (ContainerInitializationException e) {
        e.printStackTrace();
        return null;
      }
    }
    return handler.proxy(awsProxyRequest, context);
  }
}
