package models.exceptions;

import utils.LogUtils;

/**
 * Created by Isaac on 6/3/17.
 */
public class CardNotFoundException extends Exception {
    public CardNotFoundException(String message, Throwable t) {
        super(message, t);
        LogUtils.logWarning("[card not found] " + message);
    }
}
