package models.cards;

import models.Player;
import models.board.BoardEntity;
import models.energyUtils.Cost;
import models.exceptions.ActionCancelledException;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;
import utils.IdService;
import utils.LogUtils;

public abstract class Card {
    private final String name;
    private String text;
    private final String description;
    private final String image;
    private final int id;

    private final Cost cost;

    public String getNameNoColor() {
        return name;
    }

    public String getName() {
        return LogUtils.colorBlue(getNameNoColor());
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        text = newText;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image == null ? "" : image;
    }

    public int getId() {
        return id;
    }

    public Cost getCost() {
        return cost;
    }

    public Card(String name, String text, String description, Cost cost, String image) {
        this.name = name;
        this.text = text;
        this.description = description;
        this.cost = cost;
        this.image = image;
        id = IdService.getInstance().generateId();
    }

    public Card(String name, String text, String description, Cost cost) {
        this(name, text, description, cost, null);
    }

    public Card() {
        this(
                "No name",
                "No text",
                "No description",
                new Cost(0, 0, 0, 0)
        );
    }
    public String toString() {
        return toString(getText());
    }

    public String toString(String text) {
        return getName() + "\n" +
                "COST: " + cost.toString() + "\n" +
                text + "\n" +
                "\"" + getDescription() + "\"\n";
    }

//    public abstract BoardEntity play(Player player) throws
//            ActionCancelledException, IllegalMoveException, PositionOccupiedException, CardNotFoundException;

    public abstract Card generateCard();

//    public abstract BoardEntity play(Player player, int position) throws ActionCancelledException;

}