package gameState;

import models.board.BoardState;
import utils.GraphicsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to contain a list of objects that the main thread should render.
 */
public class RenderQueue {

    /**
     * Specify what we want the main thread to render.
     */
    public enum UpdateType {
        UPDATE_HAND,
        UPDATE_BOARD,
        UPDATE_HUD,
        UPDATE_ALL
    }

    private boolean isLocked;
    private static RenderQueue instance;
    private final List<UpdateType> queue = new ArrayList<>();

    private String hudText;

    /**
     * Return the static instance of the queue.
     * @return The instance
     */
    public static RenderQueue getInstance() {
        if (instance == null) {
            instance = new RenderQueue();
        }
        return instance;
    }

    /**
     * Add an update request onto the queue.
     * @param type The type of update to execute
     */
    public void queueUpdate(final UpdateType type) {
        if (isLocked) {
            queueUpdate(type);
        } else {
            isLocked = true;
            queue.add(type);
            isLocked = false;
        }
    }

    /**
     * Queue a change in hudText.
     * @param newText The text to change the hudNode to
     */
    public void queueTextChange(final String newText) {
        if (isLocked) {
            queueTextChange(newText);
        } else {
            isLocked = true;
            queue.add(UpdateType.UPDATE_HUD);
            hudText = newText;
            isLocked = false;
        }
    }

    /**
     * Execute a single render update from the stack.
     */
    public void executeUpdate() {
        if (queue.size() < 1) {
            return;
        }
        if (isLocked) {
            executeUpdate();
        } else {
            isLocked = true;
            final UpdateType type = queue.remove(0);

            switch (type) {
                case UPDATE_ALL:
                    GraphicsUtils.renderAll(BoardState.getInstance());
                    break;
                case UPDATE_BOARD:
                    GraphicsUtils.renderBoard();
                    break;
                case UPDATE_HAND:
                    GraphicsUtils.renderCards(Game.getPlayer());
                case UPDATE_HUD:
                    GraphicsUtils.setHudText(hudText);
            }
            isLocked = false;
        }
    }

}
