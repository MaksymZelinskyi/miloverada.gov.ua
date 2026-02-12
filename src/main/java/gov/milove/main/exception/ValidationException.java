package gov.milove.main.exception;

/**
 * @author Liashenko Andrii
 * @since 2/26/2025
 */
public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}
