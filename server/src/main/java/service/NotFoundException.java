package service;

public class NotFoundException extends ServiceException{
    public NotFoundException(String message) {
        super(406, message);
    }

    public NotFoundException() {
        this("not found");
    }
}
