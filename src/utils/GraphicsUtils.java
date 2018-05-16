package utils;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import gameState.Game;
import models.Player;
import models.board.*;
import models.cards.Card;
import models.cards.HeroCard;
import models.energyUtils.EnergyState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Isaac on 7/23/17.
 */
public class GraphicsUtils {

    public static final float BOARD_WIDTH = 15;
    public static final float BOARD_HEIGHT = 8;
    public static final float QUARTER_ROTATION = 1.5708f;

    public static final float CARD_WIDTH = 1;
    public static final float CARD_DEPTH = 0.1f;
    public static final float CARD_HEIGHT = 2;

    public static final float CARD_PREVIEW_WIDTH = 204;
    public static final float CARD_PREVIEW_HEIGHT = 304;
    public static final float CARD_PREVIEW_IMAGE_OFFSET = 2;

    private static final float HELP_TEXT_HEIGHT = 600;

    private static Node guiNode;

    private static Node boardNode;
    private static Node energyNode;
    private static Node slotNode;
    private static Node enemySlotNode;
    private static Node handNode;
    private static Node enemyHandNode;
    private static Node playerNode;
    private static Node enemyPlayerNode;

    public static Node highlightNode;

    private static AssetManager assetManager;

    private static BitmapText hudText;

    private static AppSettings settings;

    private static ArrayList<Node> selectables;
    private static ArrayList<Node> clickables;

    public static void init(AssetManager newAssetManager, Node newGuiNode) {
        assetManager = newAssetManager;
        guiNode = newGuiNode;
    }

    public static AssetManager assetManager() {
        return assetManager;
    }

    public static Node guiNode() {
        return guiNode;
    }

    public static void initBoardNode() {
        boardNode = createSquareNode(
                0, -3, -15,
                BOARD_WIDTH, 0.4f, BOARD_HEIGHT,
                "Board",
                ColorRGBA.Gray
        );
    }

    public static BitmapFont defaultFont() {
        return assetManager().loadFont("Interface/Fonts/Default.fnt");
    }

    public static Node getBoardNode() {
        if (boardNode == null) {
            boardNode = new Node();
            boardNode.setName("Board");
        }
        return boardNode;
    }

    public static Node getEnergyNode() {
        if (energyNode == null) {
            energyNode = new Node();
            energyNode.setName("energy_buttons");
        }
        return energyNode;
    }

    public static Node getSlotNode() {
        if (slotNode == null) {
            slotNode = new Node();
            slotNode.setName("slot_buttons");
        }
        return slotNode;
    }

    public static Node getEnemySlotNode() {
        if (enemySlotNode == null) {
            enemySlotNode = new Node();
            enemySlotNode.setName("enemy_slot_buttons");
        }
        return enemySlotNode;
    }

    public static List<Node> getClickables() {
        if (clickables == null) {
            clickables = new ArrayList<>();
        }
        return clickables;
    }

    public static List<Node> getSelectables() {
        if (selectables == null) {
            selectables = new ArrayList<>();
        }
        return selectables;
    }

    public static Node getPlayerNode() {
        return playerNode;
    }

    public static Node getEnemyPlayerNode() {
        return enemyPlayerNode;
    }

    public static void initHudText() {
        if (hudText != null) {
            hudText.removeFromParent();
        }
        settings = new AppSettings(true);

        BitmapFont guiFont = GraphicsUtils.assetManager().loadFont("Interface/Fonts/Default.fnt");

        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText("test");             // the text
        hudText.setLocalTranslation(settings.getWidth(), HELP_TEXT_HEIGHT, 0); // position
        GraphicsUtils.guiNode().attachChild(hudText);
    }

    public static void setHudText(String text) {
        hudText.setText(text);
        hudText.setLocalTranslation(settings.getWidth() - hudText.getLineWidth() / 2, HELP_TEXT_HEIGHT, 0);
    }

    public static void setSelectables(Node newNode) {
        getSelectables().clear();
        getSelectables().add(newNode);
    }

    private static Node getHandNode() {
        if (handNode == null) {
            handNode = new Node();
            handNode.setName("player_hand");
        }
        return handNode;
    }

    public static void setSelectablesHandAndEnergy() {
        setSelectables(getHandNode());
        for (EnergyState.ENERGY_TYPE type : EnergyState.ENERGY_TYPE.values()) {
            if (Game.getCurrentPlayer().getStoredEnergy().getEnergy(type) > 0) {
                selectables.add(
                    getEnergyNode().getChild("energy_" + type.displayName()).getParent()
                );
            }
        }
    }

    private static Node createSquareNode(Vector3f position, float width, float height, float depth, String name, ColorRGBA color) {
        return createSquareNode(position.x, position.y, position.z, width, height, depth, name, color);
    }

    private static Node createSquareNode(float x, float y, float z, float width, float height, float depth, String name, ColorRGBA color) {
        return createSquareNode(x, y, z, width, height, depth, name, color, null);
    }

    private static Node createSquareNode(float x, float y, float z,
                                  float width, float height, float depth,
                                  String name, ColorRGBA color, Texture texture) {

        Box box = new Box(width, height, depth);
        Geometry geom = new Geometry(name, box);
        geom.setLocalTranslation(new Vector3f(x,y,z));
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        if (texture != null) {
            mat.setTexture("ColorMap", texture);
        }
        geom.setMaterial(mat);
        Node node = new Node(name);
        node.attachChild(geom);

        return node;
    }

    private static String removeColors(String str) {
        return str.replaceAll("\u001B.{1,3}m", "");
    }

    public static void renderCards(Player player) {

        if (player.getHand() != null) {
            List<Card> cards = player.getHand().getCards();
            getHandNode().detachAllChildren();
            int handSize = player.getHand().getCards().size();
            final float SPACING = handSize <= 5 ? 3 : 3 - (handSize * 0.1f);
            float xpos = ((cards.size() / 2) * -1 * (SPACING + CARD_WIDTH)) - (CARD_WIDTH/2);
            for (Card card : cards) {
                Node cardNode = generateCardNode(card, xpos += SPACING, 1, -2);
                getHandNode().attachChild(cardNode);
            }
            getBoardNode().attachChild(getHandNode());
        }
    }

    public static Node generateCardPreview(Hero entity) {
        Node previewNode = generateCardPreview(entity.getCard(), 100, 300,
                entity.getName() + "\n" + removeColors(entity.healthString()),
                removeColors(entity.getText() + "\n\n\n" + entity.getStoredEnergy().toString()));

        List<Equipment> equipList = entity.getEquipment();
        if (equipList.size() > 0) {
            float currentX = 300;
            float currentY = 350;
            for (Equipment current : equipList) {
                previewNode.attachChild(generateCardPreview(current.getCard(), currentX, currentY));
                currentX += CARD_PREVIEW_WIDTH + 50;
            }
        }

        return previewNode;
    }

    private static Node generateCardPreview(Card card, float x, float y) {
        return generateCardPreview(card, x, y, card.getNameNoColor(), removeColors(card.getText()));
    }

    private static Node generateCardPreview(Card card, float x, float y, String name, String body) {
        Node imageNode = new Node("card_preview");

        Picture picture = new Picture("card_base");

        picture.setImage(assetManager, "blank_card.png", true);

        picture.setWidth(CARD_PREVIEW_WIDTH);
        picture.setHeight(CARD_PREVIEW_HEIGHT);
        picture.setPosition(x - CARD_PREVIEW_IMAGE_OFFSET, y + CARD_PREVIEW_IMAGE_OFFSET);


        Picture cardImage = new Picture("card_image");
        try {
            cardImage.setImage(assetManager, card.getImage(), false);
        } catch (AssetLoadException e) {
            cardImage.setImage(assetManager, "no_image.png", false);
        }

        final float IMAGE_SIZE = CARD_PREVIEW_WIDTH - (CARD_PREVIEW_IMAGE_OFFSET * 2);
        cardImage.setWidth(IMAGE_SIZE);
        cardImage.setHeight(IMAGE_SIZE - 50);
        cardImage.setPosition(x, y + CARD_PREVIEW_HEIGHT - IMAGE_SIZE + 50);

        BitmapText cardNameText = new BitmapText(defaultFont(), false);
        cardNameText.setSize(16f);
        cardNameText.setText(name);
        cardNameText.setLocalTranslation(x, y + IMAGE_SIZE, 0);
        cardNameText.setColor(ColorRGBA.White);
        cardNameText.setBox(new Rectangle(0, 0, IMAGE_SIZE, 150));
        cardNameText.setAlignment(BitmapFont.Align.Center);

        BitmapText cardDescriptionText = new BitmapText(defaultFont(), false);
        cardDescriptionText.setSize(12f);
        cardDescriptionText.setText(body);
        cardDescriptionText.setLocalTranslation(x, y + IMAGE_SIZE - 75, 0);
        cardDescriptionText.setColor(ColorRGBA.Black);
        cardDescriptionText.setBox(new Rectangle(0, 0, IMAGE_SIZE, 125));
        cardDescriptionText.setAlignment(BitmapFont.Align.Center);

        imageNode.attachChild(picture);
        imageNode.attachChild(cardImage);
        imageNode.attachChild(cardNameText);
        imageNode.attachChild(cardDescriptionText);

        return imageNode;
    }

    private static Node generateCardNode(Card card, float x, float y, float z) {
        Node cardNode = createSquareNode(x, y, z,
                CARD_WIDTH, CARD_HEIGHT, CARD_DEPTH,
                card.getNameNoColor(), ColorRGBA.Blue);

        BitmapFont font = defaultFont();

        BitmapText titleText = new BitmapText(font, false);
        BitmapText bodyText = new BitmapText(font, false);

        titleText.setSize(0.2f);
        bodyText.setSize(0.15f);

        titleText.setText(card.getNameNoColor());
        bodyText.setText(removeColors(card.getText()));

        Geometry cardGeom = (Geometry)cardNode.getChild(card.getNameNoColor());

        titleText.setLocalTranslation(cardGeom.getLocalTranslation());
        bodyText.setLocalTranslation(cardGeom.getLocalTranslation());
        titleText.getLocalTranslation().setZ(cardGeom.getLocalTranslation().getZ() + CARD_DEPTH + 0.2f);
        bodyText.getLocalTranslation().setZ(cardGeom.getLocalTranslation().getZ() + CARD_DEPTH + 0.15f);

        final float TEXT_WIDTH = CARD_WIDTH * 2f;
        titleText.setBox(new Rectangle(TEXT_WIDTH * -1 / 2, 0, TEXT_WIDTH, 1));
        titleText.setAlignment(BitmapFont.Align.Center);

        bodyText.setBox(new Rectangle(TEXT_WIDTH * -1 / 2, -0.5f, TEXT_WIDTH, 1));

        if (card instanceof HeroCard) {
            titleText.setText(titleText.getText() + "\n" + ((HeroCard) card).getHealth());
        }

        cardNode.setUserData("type", Game.OBJECT_TYPE.CARD.toString());
        cardNode.setUserData("id", card.getId());

        titleText.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());
        bodyText.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());

        assetManager.registerLocator("resources", FileLocator.class);

        Node image = createSquareNode(cardGeom.getLocalTranslation().getX(),
                cardGeom.getLocalTranslation().getY() + CARD_HEIGHT / 2,
                cardGeom.getLocalTranslation().getZ() + CARD_DEPTH + 0.001f,
                CARD_WIDTH,
                CARD_WIDTH,
                0f,
                card.getNameNoColor() + "_img",
                ColorRGBA.White
                );

        Texture cardTexture;
        try {
            cardTexture = assetManager.loadTexture(card.getImage());
        } catch(AssetLoadException e) {
            cardTexture = assetManager.loadTexture("no_image.png");
        }
        ((Geometry)image.getChild(card.getNameNoColor() + "_img")).getMaterial().setTexture("ColorMap", cardTexture);

        image.setUserData("type", Game.OBJECT_TYPE.IMAGE.toString());

        cardNode.attachChild(titleText);
        cardNode.attachChild(bodyText);
        cardNode.attachChild(image);

        return cardNode;
    }

    public static void renderAll(BoardState boardState) {
        renderBoard(boardState);
        for (Player player : Game.players) {
            renderCards(player);
        }
    }

    /**
     * Render the default board state.
     */
    public static void renderBoard() {
        renderBoard(BoardState.getInstance());
    }

    /**
     * Render a specified board state instance.
     *
     * @param boardState the board state to render
     */
    public static void renderBoard(BoardState boardState) {
        detachChildrenFromBoard(getSlotNode(), getEnemySlotNode(), getEnergyNode(), getPlayerNode(), getEnemyPlayerNode());
        energyNode = null;
        slotNode = null;
        enemySlotNode = null;

        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

        Player player = boardState.getPlayers().get(0);
        Player enemy = boardState.getPlayers().get(1);

        float x = -10;
        for (EnergyState.ENERGY_TYPE type : EnergyState.ENERGY_TYPE.values()) {
            Node energyButtonNode =   createSquareNode(x, -2.5f, -15, 1.5f, 0.01f, 1.5f, "energy_" + type.displayName(), type.color());
            energyButtonNode.setUserData("energyType", type.shortName());

            BitmapText text = new BitmapText(font, false);

            text.setSize(0.5f);
            text.setText(player.getStoredEnergy().getCurrentEnergy().getEnergy(type) + "/" + player.getStoredEnergy().getEnergy(type));
            text.setLocalTranslation(energyButtonNode.getChildren().get(0).getLocalTranslation());
            text.getLocalTranslation().setY(text.getLocalTranslation().getY() + 0.05f);
            text.getLocalTranslation().setX(text.getLocalTranslation().getX() - (text.getLineWidth() / 2));
            text.getLocalTranslation().setZ(text.getLocalTranslation().getZ() + 0.25f);
            text.rotate(QUARTER_ROTATION * -1, 0, 0);

            BitmapText enemyText = new BitmapText(font, false);

            enemyText.setSize(0.5f);
            enemyText.setText(enemy.getStoredEnergy().getCurrentEnergy().getEnergy(type) + "/" + enemy.getStoredEnergy().getEnergy(type));
            enemyText.setLocalTranslation(energyButtonNode.getChildren().get(0).getLocalTranslation());
            enemyText.getLocalTranslation().setY(enemyText.getLocalTranslation().getY() + 0.05f);
            enemyText.getLocalTranslation().setX(enemyText.getLocalTranslation().getX() + (enemyText.getLineWidth() / 2));
            enemyText.getLocalTranslation().setZ(enemyText.getLocalTranslation().getZ() - 0.25f);
            enemyText.rotate(QUARTER_ROTATION * -1, QUARTER_ROTATION * 2, 0);

            text.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());
            enemyText.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());

            energyButtonNode.attachChild(text);
            energyButtonNode.attachChild(enemyText);

            energyButtonNode.setUserData("type", Game.OBJECT_TYPE.ENERGY.toString());
            getEnergyNode().attachChild(energyButtonNode);

            x += 6.5f;
        }

        getBoardNode().attachChild(getEnergyNode());

        for (BoardPosition pos : player.getBoard()) {
            float slotX = getBoardNode().getChild("Board").getLocalTranslation().getX() - BOARD_WIDTH + ((pos.getPosition() + 1) * 5);

            Texture cardTexture = null;
            GraphicsUtils.assetManager().registerLocator("resources", FileLocator.class);
            if (pos.getEntity() instanceof Hero) {
                HeroCard card = ((Hero)pos.getEntity()).getCard();
                try {
                    cardTexture = GraphicsUtils.assetManager().loadTexture(card.getImage());
                } catch (AssetLoadException e) {
                    cardTexture = GraphicsUtils.assetManager().loadTexture("no_image.png");
                }
            }

            Node slotButtonNode = createSquareNode(slotX, -2.5f, -10, 1, 0.01f, 1, "board_position_" + pos.getPosition(), ColorRGBA.White, cardTexture);
            slotButtonNode.setUserData("position", pos.getPosition());

            BitmapText text = new BitmapText(font, false);

            text.setSize(0.5f);
            text.setText(removeColors(pos.toString()));
            text.setLocalTranslation(slotButtonNode.getChildren().get(0).getLocalTranslation());
            text.getLocalTranslation().setY(text.getLocalTranslation().getY() + 0.5f);
            text.getLocalTranslation().setX(text.getLocalTranslation().getX() - (text.getLineWidth() / 2));
            text.rotate(QUARTER_ROTATION * -1, 0, 0);

            text.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());
            slotButtonNode.attachChild(text);
            slotButtonNode.setUserData("type", Game.OBJECT_TYPE.POSITION.toString());

            pos.setNode(slotButtonNode);

            if (pos.getEntity() instanceof PlayerEntity) {
                playerNode = slotButtonNode;
                getBoardNode().attachChild(slotButtonNode);
            } else {
                getSlotNode().attachChild(slotButtonNode);
            }
        }

        for (BoardPosition pos : enemy.getBoard()) {
            float slotX = getBoardNode().getChild("Board").getLocalTranslation().getX() - BOARD_WIDTH + (((pos.getPosition() - 5) + 1) * 5);
            Node slotButtonNode = createSquareNode(slotX, -2.5f, -20, 1, 0.01f, 1, "board_position_" + pos.getPosition(), ColorRGBA.White);
            slotButtonNode.setUserData("position", pos.getPosition());

            BitmapText text =  new BitmapText(font, false);

            text.setSize(0.5f);
            text.setText(pos.toString());
            text.setLocalTranslation(slotButtonNode.getChildren().get(0).getLocalTranslation());
            text.getLocalTranslation().setY(text.getLocalTranslation().getY() + 0.5f);
            text.getLocalTranslation().setX(text.getLocalTranslation().getX() + (text.getLineWidth() / 2));
            text.getLocalTranslation().setZ(text.getLocalTranslation().getZ() + 1f);
            text.rotate(QUARTER_ROTATION * -1, QUARTER_ROTATION * 2, 0);

            text.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());
            slotButtonNode.attachChild(text);
            slotButtonNode.setUserData("type", Game.OBJECT_TYPE.POSITION.toString());

            pos.setNode(slotButtonNode);

            if (pos.getEntity() instanceof PlayerEntity) {
                enemyPlayerNode = slotButtonNode;
                getBoardNode().attachChild(slotButtonNode);
            } else {
                getEnemySlotNode().attachChild(slotButtonNode);
            }
        }

        getBoardNode().attachChild(getSlotNode());
        getBoardNode().attachChild(getEnemySlotNode());

        Node endTurnButton = createSquareNode(BOARD_WIDTH - 1, -2.5f, -15, 1.5f, 0.1f, 1, "end_turn_button", ColorRGBA.White);
        endTurnButton.setUserData("type", Game.OBJECT_TYPE.BUTTON.toString());
        BitmapText text = new BitmapText(font, false);
        text.setSize(0.5f);
        text.setText("End Turn");
        text.setLocalTranslation(endTurnButton.getChildren().get(0).getLocalTranslation());
        text.getLocalTranslation().setY(text.getLocalTranslation().getY() + 0.5f);
        text.getLocalTranslation().setX(text.getLocalTranslation().getX() - (text.getLineWidth() / 2));
        text.rotate(QUARTER_ROTATION * -1, 0, 0);
        text.setUserData("type", Game.OBJECT_TYPE.TEXT.toString());
        endTurnButton.attachChild(text);
        getBoardNode().attachChild(endTurnButton);
    }

    private static void detachChildrenFromBoard(Node ... nodesToDetach) {
        for (Node node : nodesToDetach) {
            if (node != null) {
                getBoardNode().detachChild(node);
            }
        }
    }

    public static void removeHighlight() {
        if (highlightNode != null) {
            highlightNode.removeFromParent();
        }
    }

    public static void highlightNode(Node node) {
        if (isSelectable(node)) {
            if (highlightNode != null && highlightNode.getParent() != null) {
                highlightNode.removeFromParent();
            }
            highlightNode = createSquareNode(node.getChild(node.getName()).getLocalTranslation(), 1.1f, 2.1f, 0.01f, "highlight", ColorRGBA.White);
            highlightNode.setUserData("type", Game.OBJECT_TYPE.HIGHLIGHT_NODE.toString());
            highlightNode.rotate(node.getWorldRotation());
            node.attachChild(highlightNode);
        }
    }

    private static boolean isSelectable(Node target) {
        for (Node node : getSelectables()) {
            if (isSelectable(target, node)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSelectable(Node target, Node parent) {
        if (parent == null) {
            return false;
        }
        if (parent.equals(target)) {
            return true;
        }
        for (Spatial current : parent.getChildren()) {
            if (current instanceof Node) {
                Node currentNode = (Node)current;
                if (currentNode.getChildren().contains(target)) {
                    return true;
                }
                else if (!currentNode.getChildren().isEmpty()) {
                    if (isSelectable(target, currentNode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
