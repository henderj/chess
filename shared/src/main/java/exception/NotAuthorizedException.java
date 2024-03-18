package exception;

public class NotAuthorizedException extends ResponseException {
    public NotAuthorizedException(String message) {
        super(401, message);
    }

    public NotAuthorizedException() {
        this("not authorized");
    }
}
