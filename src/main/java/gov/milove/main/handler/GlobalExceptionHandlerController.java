package gov.milove.main.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import gov.milove.main.exception.*;
import gov.milove.main.repository.jpa.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Global exception handler for controllers to handle specific exceptions and return appropriate
 * HTTP responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalExceptionHandlerController {

  private static final String NOT_FOUND_MESSAGE = "Response code: 404 (not found). Message: '{}'. FROM: {} {}";

  @ExceptionHandler(value = {DocumentGroupNotFoundException.class,
          FileNotFoundException.class, AppUserNotFoundException.class, ImageNotFoundException.class,
      LinkBannerNotFoundException.class, DocumentNotFoundException.class, NewsNotFoundException.class})
  public ResponseEntity<ErrorMessage> handleCustomNotFoundException(RuntimeException ex,
      HttpServletRequest request) {
    log.error(NOT_FOUND_MESSAGE, ex.getMessage(), request.getMethod(), request.getRequestURI());

    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {EntityNotFoundException.class})
  public ResponseEntity<ErrorMessage> handleNotFoundException(RuntimeException ex,
                                                              HttpServletRequest request) {
    log.error(NOT_FOUND_MESSAGE, ex.getMessage(), request.getMethod(), request.getRequestURI(), ex);

    return new ResponseEntity<>(new ErrorMessage("Entity not found"), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<String> handleServiceException(ServiceException ex) {
    log.error("Service error", ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler({MultipartException.class, MissingServletRequestParameterException.class})
  public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
    log.error("Bad request error", ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex,
                                                                             HttpServletRequest request) {
    String message = "Request with path '%s' does not supported with method '%s'".formatted(request.getRequestURI(),
            ex.getMethod());
    return badRequest(message);
  }

  @ExceptionHandler(value = {ValidationException.class, IllegalParameterException.class})
  public ResponseEntity<String> handleObjectNotValidException(RuntimeException ex,
      HttpServletRequest request) {
    log.error("Response code: 422 (validation). Validation exception '{}'. FROM {}, {}",
        ex.getMessage(), request.getMethod(), request.getRequestURI());

    return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionDetails> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<FieldError> errors = ex.getBindingResult().getFieldErrors();
    Map<String, String> errorsMap = errors.stream()
        .collect(Collectors.toMap(FieldError::getField,
            FieldError::getDefaultMessage));

    log.error("Response code: 400 (bad request). Validation exception '{}'. Method: {}, URI: {}",
        errorsMap, request.getMethod(), request.getRequestURI());

    ExceptionDetails exceptionBody = new ExceptionDetails("Validation failed", errorsMap);

    return new ResponseEntity<>(exceptionBody, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionDetails> handleConstraintViolationException(
      ConstraintViolationException ex, HttpServletRequest request) {
    Map<String, String> errorsMap = ex.getConstraintViolations().stream().
        collect(Collectors.toMap(violation ->
                violation.getPropertyPath().toString(),
            ConstraintViolation::getMessage));

    log.error("Response code: 400 (bad request). Validation exception '{}'. FROM {}, {}",
        errorsMap, request.getMethod(), request.getRequestURI());

    ExceptionDetails exceptionBody = new ExceptionDetails("Validation failed", errorsMap);

    return new ResponseEntity<>(exceptionBody, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleRuntimeException(Exception ex,
                                                             HttpServletRequest request) {
    log.error("Response code: 500 (internal). Message: '{}'. FROM: {} {}",
            ex.getMessage(), request.getMethod(), request.getRequestURI(), ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorMessage("Internal Server Error. Unexpected Error."));
  }

  private ResponseEntity<ErrorMessage> badRequest(String message) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorMessage(message));
  }
}
