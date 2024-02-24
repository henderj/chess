package service;

public class AlreadyTakenException extends ServiceException{
    public AlreadyTakenException(String message) {
        super(message);
    }
}
