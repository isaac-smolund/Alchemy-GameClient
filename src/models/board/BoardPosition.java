package models.board;

import com.google.gson.annotations.Expose;
import com.jme3.scene.Node;
import models.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isaac on 6/16/17.
 */
public class BoardPosition {
    @Expose
    private int position;
    private transient Player player;

    /**
     * Variable used to keep track of the player this belongs to without creating
     * a circular dependency:
     */
    @Expose
    private int playerTurn;

    @Expose
    private BoardEntity entity;

    private Node graphicsNode;

    public BoardPosition(int position, Player player) {
        this.position = position;
        this.player = player;
        this.playerTurn = player.getTurnOrder();
    }

    public int getPosition() {
        return position;
    }

    public Player getPlayer() {
        if (player == null) {
            for (Player currentPlayer : BoardState.getInstance().getPlayers()) {
                if (currentPlayer.getTurnOrder() == this.playerTurn) {
                    this.player = currentPlayer;
                    return player;
                }
            }
        }
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

    public Map<String, Object> encode() {
        Map<String, Object> positionMap = new HashMap<>();
        positionMap.put("entityType", BoardPosition.class);
        positionMap.put("position", position);
        if (entity != null) {
            positionMap.put("entity", entity.encode());
        }

        return positionMap;
    }
}
