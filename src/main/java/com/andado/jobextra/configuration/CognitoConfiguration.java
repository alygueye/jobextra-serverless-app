
package com.andado.jobextra.configuration;

/**
 * Configuration parameters for the Cognito credentials provider
 */
public class CognitoConfiguration {
  // TODO: Specify the identity pool id
  public static final String IDENTITY_POOL_ID = "us-east-1:xxxxx-xxx-xxx-xxx-xxxxxxxxx";
  // TODO: Specify the custom provider name used by the identity pool
  public static final String CUSTOM_PROVIDER_NAME = "com.customprovider";

  // This should not be changed, it is a default value for Cognito.
  public static final String COGNITO_PROVIDER_NAME = "cognito-identity.amazonaws.com";
}
