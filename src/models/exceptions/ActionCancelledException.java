package models.exceptions;

import utils.LogUtils;

/**
 * Created by Isaac on 6/25/17.
 */
public class ActionCancelledException extends Exception {
    public ActionCancelledException(String message, Throwable t) {
        super(message, t);
    }
}
