package service;

public class AlreadyTakenException extends ServiceException{
    public AlreadyTakenException(String message) {
        super(403, message);
    }

    public AlreadyTakenException() {
        this("already taken");
    }
}
