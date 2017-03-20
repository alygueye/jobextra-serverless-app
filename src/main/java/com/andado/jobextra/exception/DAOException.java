
package com.andado.jobextra.exception;

/**
 * This exception should bot be exposed to Lambda or the client application. It's used internally to
 * identify a DAO issue
 */
public class DAOException extends Exception {
  public DAOException(String s, Exception e) {
    super(s, e);
  }

  public DAOException(String s) {
    super(s);
  }
}
