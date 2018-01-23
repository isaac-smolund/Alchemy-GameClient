package models.exceptions;

import utils.LogUtils;

/**
 * Created by Isaac on 5/30/17.
 */
public class IllegalMoveException extends Exception {
    public IllegalMoveException(String message, Throwable t) {
        super(message, t);
        LogUtils.logWarning("[illegal move] " + message);
    }
}
