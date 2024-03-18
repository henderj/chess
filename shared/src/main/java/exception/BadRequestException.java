package exception;

public class BadRequestException extends ResponseException {
    public BadRequestException(String message) {
        super(400, message);
    }

    public BadRequestException() {
        this("bad request");
    }
}
