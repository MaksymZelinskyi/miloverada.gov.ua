package gov.milove.main.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException() {
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
