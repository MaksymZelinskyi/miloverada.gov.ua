package gov.milove.main.exception;

/**
 * @author Liashenko Andrii
 * @since 2/24/2025
 */
public class AppUserNotFoundException extends RuntimeException {

  public AppUserNotFoundException(String message) {
    super(message);
  }
}
