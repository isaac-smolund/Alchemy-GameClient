package models.board;

import com.jme3.scene.Node;
import models.Player;

/**
 * Created by Isaac on 6/16/17.
 */
public class BoardPosition {
    private int position;
    private Player player;
    private BoardEntity entity;

    private Node graphicsNode;

    public BoardPosition(int position, Player player) {
        this.position = position;
        this.player = player;
    }

    public int getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    public BoardEntity getEntity() {
        return entity;
    }

    public Node getNode() {
        return graphicsNode;
    }

    public void setNode(Node graphicsNode) {
        this.graphicsNode = graphicsNode;
    }

    public void setEntity(BoardEntity entity) {
        this.entity = entity;
    }

    public boolean isEmpty() {
        return entity == null;
    }

    public String toString() {
        return isEmpty() ? "Empty space." : entity.toString();
    }
}
