package models.exceptions;

import utils.LogUtils;

/**
 * Created by Isaac on 6/16/17.
 */
public class PositionOccupiedException extends Exception {
    public PositionOccupiedException(String message, Throwable t) {
        super(message, t);
        LogUtils.logWarning("[position occupied] " + message);
    }
}
