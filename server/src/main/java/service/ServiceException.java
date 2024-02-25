package service;

public class ServiceException extends Exception{

        final private int statusCode;

        public ServiceException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

    public int getStatusCode() {
        return statusCode;
    }
}
