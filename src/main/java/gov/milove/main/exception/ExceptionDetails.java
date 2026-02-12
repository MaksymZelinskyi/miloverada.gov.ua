package gov.milove.main.exception;

import java.util.Map;

/**
 * Represents exception details in HTTP response
 */
public record ExceptionDetails(
    String message,
    Map<String, String> errors
) {
}
