package utils;

/***
 * @author alberto delgado
 * @author anchitbhatia
 *
 * Custom exception class to raise exception in connection failure
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }
}
