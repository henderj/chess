package service;

public class NotAuthorizedException extends ServiceException{
    public NotAuthorizedException(String message) {
        super(message);
    }
}
