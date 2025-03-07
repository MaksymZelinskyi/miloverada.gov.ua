package gov.milove.main.controller;

import gov.milove.main.exception.AppUserNotFoundException;
import gov.milove.main.exception.DocumentGroupNotFoundException;
import gov.milove.main.exception.ExceptionDetails;
import gov.milove.main.exception.FileNotFoundException;
import gov.milove.main.exception.IllegalParameterException;
import gov.milove.main.exception.ImageNotFoundException;
import gov.milove.main.exception.LinkBannerNotFoundException;
import gov.milove.main.exception.NewsNotFoundException;
import gov.milove.main.exception.ServiceException;
import gov.milove.main.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Global exception handler for controllers to handle specific exceptions and return appropriate
 * HTTP responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class ExceptionHandlerController {

  @ExceptionHandler(value = {DocumentGroupNotFoundException.class, FileNotFoundException.class,
      AppUserNotFoundException.class, ImageNotFoundException.class,
      LinkBannerNotFoundException.class,
      NewsNotFoundException.class})
  public ResponseEntity<String> handleNotFoundException(RuntimeException ex,
      HttpServletRequest request) {
    log.error("Response code: 404 (not found). Message: '{}'. FROM: {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURI());

    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<String> handleServiceException(ServiceException ex) {
    log.error("Service error", ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
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
  public ResponseEntity<String> handleRuntimeException(Exception ex,
      HttpServletRequest request) {
    log.error("Response code: 500 (internal). Message: '{}'. FROM: {} {}",
        ex.getMessage(), request.getMethod(), request.getRequestURI(), ex);

    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
