package gov.milove.main.exception;

public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(Throwable cause) {
        super(cause);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
