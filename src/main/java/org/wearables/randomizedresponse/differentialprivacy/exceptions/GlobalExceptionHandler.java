/**
 * This project was part of the summer semester Term course for computer security and privacy.
 *
 * @author Lennart Hahner
 */
package org.wearables.randomizedresponse.differentialprivacy.exceptions;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class serves as the Exception handler for all REST Endpoints that exists within this
 * project.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * This is a general exception handler, whenever in a REST endpoint an exception happens, this
   * method takes over and gives and response to the client.
   *
   * @param exception The type of exception to handle
   * @return The ResponseEntity passed to the client.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    ErrorResponse errorResponse =
        new ErrorResponse(exception.getMessage(), LocalDateTime.now().toString());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
