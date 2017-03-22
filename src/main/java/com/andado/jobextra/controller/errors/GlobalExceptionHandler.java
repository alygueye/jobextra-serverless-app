package com.andado.jobextra.controller.errors;

import com.andado.jobextra.exception.BadRequestException;
import com.andado.jobextra.exception.InternalErrorException;

import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StringUtils.capitalize;

/**
 * les exceptions traitées par défaut par le framework DefaultHandlerExceptionResolver
 */
@ControllerAdvice
class GlobalExceptionHandler {

  static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

  @Autowired
  private Environment env;

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorInfo> genericHandler(Exception e) throws Exception {

    if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
      throw e;
    }

    ErrorInfo errorInfo = new ErrorInfo();
    errorInfo.message = e.getMessage();
    errorInfo.debugMessage = getDebugMessage(e);
    errorInfo.status = BAD_REQUEST.value();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    LOG.error("Exception has occured", e);

    return new ResponseEntity<ErrorInfo>(errorInfo, headers, BAD_REQUEST);
  }

  @ExceptionHandler(value = BadRequestException.class)
  public ResponseEntity<ErrorInfo> badRequestException(BadRequestException e) throws Exception {
    return createResponse(e, e.getMessage(), BAD_REQUEST);
  }

  @ExceptionHandler(value = InternalErrorException.class)
  public ResponseEntity<ErrorInfo> internalException(InternalErrorException e) throws Exception {
    return createResponse(e, e.getMessage(), INTERNAL_SERVER_ERROR);
  }


  @ExceptionHandler(value = TypeMismatchException.class)
  public ResponseEntity<ErrorInfo> handleTypeMismatchException(TypeMismatchException e) {

    String message = "Failed to convert to required type : " + e.getRequiredType().getSimpleName();

    return createResponse(e, message, BAD_REQUEST);
  }


  /**
   * Les exceptions remontés par le client RestTemplate
   */
  @ExceptionHandler(HttpStatusCodeException.class)
  public ResponseEntity<ErrorInfo> handleHttpStatusCodeException(HttpStatusCodeException e) {
    if (e.getStatusCode().value() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      LOG.error("[RestTemplate Call] Erreur : " + e.getResponseBodyAsString(), e);
    }

    return createResponse(e, e.getStatusText(), e.getStatusCode());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorInfo> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

    return createResponse(e, "Validation Error", BAD_REQUEST, handleValidationFailure(e));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorInfo> handleConstraintViolationException(ConstraintViolationException e) {

    return createResponse(e, "Validation Error", BAD_REQUEST, handleConstraintViolation(e));
  }

  private ResponseEntity<ErrorInfo> createResponse(Exception e, String message, HttpStatus status) {
    return createResponse(e, message, status, null);
  }

  private ResponseEntity<ErrorInfo> createResponse(Exception e, String message, HttpStatus status,
                                                   ArrayList<ErrorInfo.Property> properties) {

    ErrorInfo errorInfo = new ErrorInfo();
    errorInfo.message = message;
    errorInfo.debugMessage = getDebugMessage(e);
    errorInfo.status = status.value();
    errorInfo.properties = properties;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);

    return new ResponseEntity<ErrorInfo>(errorInfo, headers, status);
  }

  private ArrayList<ErrorInfo.Property> handleConstraintViolation(ConstraintViolationException e) {

    ArrayList<ErrorInfo.Property> properties = new ArrayList<ErrorInfo.Property>();

    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

    for (ConstraintViolation<?> violation : constraintViolations) {

      ErrorInfo.Property property = new ErrorInfo.Property();
      property.message = violation.getMessage();
      property.object = violation.getRootBean().getClass().getSimpleName();
      property.field = getPath(violation);
      properties.add(property);

    }

    return properties;
  }

  /**
   * En environnement hors prod, on affiche les messages de debuggage. Dans tous les environnement
   * on affiche le message de debuggage au rôles techniques.
   */
  private String getDebugMessage(Throwable throwable) {
    if (!Boolean.valueOf(env.getProperty("api.debugmessage.enable", "false"))) {
      return null;
    }
    final StringWriter stackStrace = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stackStrace));
    String debugMessage = stackStrace.toString();

    return debugMessage;
  }

  private ArrayList<ErrorInfo.Property> handleValidationFailure(MethodArgumentNotValidException exception) {

    List<ObjectError> bindingErrors = exception.getBindingResult().getAllErrors();

    ArrayList<ErrorInfo.Property> properties = new ArrayList<ErrorInfo.Property>();

    for (ObjectError error : bindingErrors) {

      ErrorInfo.Property property = new ErrorInfo.Property();
      property.message = error.getDefaultMessage();
      property.object = capitalize(error.getObjectName());

      if (error instanceof FieldError) {
        property.field = ((FieldError) error).getField();
      }

      properties.add(property);

    }

    return properties;
  }

  private String getPath(ConstraintViolation<?> violation) {
    String pathStr = "";
    Path path = violation.getPropertyPath();
    for (Path.Node node : path) {
      pathStr += node.getName() + ".";
    }
    // removing the trailing dot "."
    pathStr = pathStr.substring(0, pathStr.length() - 1);
    return pathStr;
  }


}
