package webSocketMessages.serverMessages;

import java.util.Objects;

public class Error extends ServerMessage {
    private final String errorMessage;

    public Error(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Error error = (Error) o;
        return Objects.equals(errorMessage, error.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), errorMessage);
    }
}
