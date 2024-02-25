package exception;

public class NotFoundException extends ServiceException{
    public NotFoundException(String message) {
        super(401, message);
    }

    public NotFoundException() {
        this("not found");
    }
}
