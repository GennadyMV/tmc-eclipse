package fi.helsinki.cs.plugin.tmc.getJson;

public class UserVisibleException extends Exception {
    public UserVisibleException(String msg) {
        super(msg);
    }

    public UserVisibleException(String msg, Throwable cause) {
        super(msg, cause);
    }
}