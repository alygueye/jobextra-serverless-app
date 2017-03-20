package com.andado.jobextra.controller.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ErrorInfo {

  /**
   * Http status code.
   */
  public int status;

  /**
   * Code erreur spécifique . Non utilisé pour l'instant.
   */
  @JsonIgnore
  public String code;

  public String message;

  public String debugMessage;

  public ArrayList<Property> properties;

  /**
   * Empty constructor used for JSON Serialization.
   */
  public ErrorInfo() {
  }

  public ErrorInfo(int status, String code, String message, String debugMessage, ArrayList<Property> properties) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.debugMessage = debugMessage;
    this.properties = properties;
  }

  public ErrorInfo(int status, String code, String message, String debugMessage) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.debugMessage = debugMessage;
  }


  public static class Property {

    public String object;

    public String message;

    public String field;
  }

}
