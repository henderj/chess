package exception;

public class NotFoundException extends ResponseException {
    public NotFoundException(String message) {
        super(401, message);
    }

    public NotFoundException() {
        this("not found");
    }
}
