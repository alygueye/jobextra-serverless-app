
package com.andado.jobextra.exception;

/**
 * This exception should be thrown whenever requests fail validation. The exception sets a default
 * pattern in the string "BAD_REQ: .*" that can be easily matched from the API Gateway for mapping.
 */
public class BadRequestException extends Exception {
  private static final String PREFIX = "BAD_REQ: ";

  public BadRequestException(String s, Exception e) {
    super(PREFIX + s, e);
  }

  public BadRequestException(String s) {
    super(PREFIX + s);
  }
}
