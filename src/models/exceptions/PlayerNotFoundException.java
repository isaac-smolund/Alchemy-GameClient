package models.exceptions;

import utils.LogUtils;

/**
 * Created by Isaac on 6/16/17.
 */
public class PlayerNotFoundException extends Exception{
    public PlayerNotFoundException(String message, Throwable t) {
        super(message, t);
        LogUtils.logWarning("Player not found!");
    }
}
