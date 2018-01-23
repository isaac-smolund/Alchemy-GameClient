package models.exceptions;

import utils.LogUtils;

/**
 * Created by Isaac on 5/30/17.
 */
public class DeckOutOfCardsException extends Exception {
    public DeckOutOfCardsException(String message, Throwable t) {
        super(message, t);
        LogUtils.logWarning("[empty deck] " + message);
    }
}
