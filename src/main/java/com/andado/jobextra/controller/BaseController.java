package com.andado.jobextra.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.amazonaws.serverless.proxy.internal.RequestReader;
import com.amazonaws.serverless.proxy.internal.model.ApiGatewayRequestContext;
import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequest;
import com.amazonaws.services.lambda.runtime.Context;

import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Map;

/**
 * Created by alygueye on 18/03/2017.
 */

public abstract class BaseController {

  static final Logger LOGGER = Logger.getLogger(BaseController.class);
  private static final String SEPARATOR_LINE = System.getProperty("line.separator")
    //+ " ************************************************************** "
    ;

  public static Gson getGson() {
    return new GsonBuilder()
      //.enableComplexMapKeySerialization()
      //.serializeNulls()
      //.setDateFormat(DateFormat.LONG)
      //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
      .setPrettyPrinting()
      .create();
  }

  protected Context getLambdaContext(AwsProxyHttpServletRequest request) {
    return (Context) request.getAttribute(RequestReader.LAMBDA_CONTEXT_PROPERTY);
  }

  protected ApiGatewayRequestContext getApiGatewayRequestContext(AwsProxyHttpServletRequest request) {
    return (ApiGatewayRequestContext) request.getAttribute(RequestReader.API_GATEWAY_CONTEXT_PROPERTY);
  }

  public String logRequest(AwsProxyHttpServletRequest request) {

    Map<String, String[]> params = (Map<String, String[]>) request.getParameterMap();
    StringBuilder stringBuilder = new StringBuilder("");

    stringBuilder = stringBuilder.append(request.getMethod() + " : " + request.getRequestURI() + " --- " + SEPARATOR_LINE);


    for (String paramName : params.keySet()) {
      stringBuilder = stringBuilder.append(" --- PARAM " + paramName + " : " + SEPARATOR_LINE);
      for (String paramValue : params.get(paramName)) {
        stringBuilder = stringBuilder.append(paramValue).append(SEPARATOR_LINE);
      }
    }
//    if (LOGGER.isDebugEnabled()) {
    stringBuilder = stringBuilder.append("\"headers\"" + " { " + SEPARATOR_LINE);
    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      stringBuilder = stringBuilder.append("  \"" + key + "\"" + " : " + "\"" + value + "\""
        + (headerNames.hasMoreElements() ? "," : "") + SEPARATOR_LINE);
    }

    stringBuilder = stringBuilder.append("}" + SEPARATOR_LINE);
//    }

    return stringBuilder.toString();
  }

}
