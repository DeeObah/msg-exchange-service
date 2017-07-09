package zw.co.dobadoba.msgexchange.service.exception;

/**
 * Created by dobadoba on 7/9/17.
 */
public class MessageExchangeException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public MessageExchangeException(String message) {
        super(message);
    }

    public MessageExchangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageExchangeException(Throwable cause) {
        super(cause);
    }

}
