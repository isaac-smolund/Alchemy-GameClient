import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.input.event.*;
import com.jme3.math.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import gameState.Game;
import gameState.RenderQueue;
import libraries.Cards;
import models.threads.MainThread;
import models.Player;
import models.board.BoardEntity;
import models.board.BoardState;
import models.board.Hero;
import models.cards.Card;
import models.cards.Deck;
import models.energyUtils.EnergyState;
import models.exceptions.*;

import com.jme3.app.SimpleApplication;
import org.lwjgl.input.Mouse;
import utils.GraphicsUtils;
import utils.InputUtils;
import utils.LogUtils;
import utils.ServerUtils;

public class Main extends SimpleApplication {

    private static Main app;

    private static ChaseCamera chaseCam;

    private Node selectedCard;
    private EnergyState.ENERGY_TYPE selectedEnergy;

    private static int var = 1;

    private static final float BOARD_WIDTH = 15;
    private static final float BOARD_HEIGHT = 8;
    private static final float DEFAULT_CAMERA_DISTANCE = 25;
    private static final float CAMERA_SPEED = 0.05f;
    private static final float HAND_VIEW_ANGLE = 0.30f;
    private static final float BOARD_VIEW_ANGLE = 0.90f;

    public static void main(String [] args) throws IllegalMoveException {

        app = new Main();
        ServerUtils.setApp(app);
        MainThread mainThread = new MainThread("main_thread", app);
        MainThread.setInstance(mainThread);
        mainThread.start();
        ServerUtils.connectToHost("localhost", 7000);
    }

    private void selectCard(Node card) {
        if (selectedCard != null) {
            deselectCard();
        }
        selectedCard = card;
        System.out.println("selected id " + selectedCard.getUserData("id"));
        Transform scale = new Transform();
        scale.setScale(1.25f);
        scale.setTranslation(card.getChild(0).getWorldTranslation().getX() * -1, 0, 3);
        card.setLocalTransform(scale);
        GraphicsUtils.highlightNode(card);
    }

    private void selectEnergy(Node energy) {
        selectedEnergy = EnergyState.getEnumValueFromString(energy.getUserData("energyType"));
        GraphicsUtils.highlightNode(energy);
    }

    private void deselectCard() {
        if (selectedCard != null) {
            Transform scale = new Transform();
            scale.setScale(1);
            selectedCard.setLocalTransform(scale);
            selectedCard = null;
            Game.setStatusMain();
        }
    }

    private void initKeys() {
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);

        inputManager.addMapping("exit",
                new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("camera_up",
                new KeyTrigger(KeyInput.KEY_P),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("camera_down",
                new KeyTrigger(KeyInput.KEY_L),
                new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("select",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("var_up",
                new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("var_down",
                new KeyTrigger(KeyInput.KEY_W));

        inputManager.addListener(analogListener,"camera_up", "camera_down");
        inputManager.addListener(actionListener, "exit", "select", "var_up", "var_down");
        inputManager.addRawInputListener(rawInputListener);
    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("exit") && !keyPressed) {
                ServerUtils.exitGame();
            }

            if (name.equals("select") && !keyPressed) {
                /* Fix for weird environment-specific bug:
                For some reason when clicking, your mouse position is immediately reflected across the Y axis of the window.
                My hacky fix: reflect the Y axis _again_, getting you back to where you started:
                */
                int middle = getContext().getSettings().getHeight() / 2;
                int yPos = (int)inputManager.getCursorPosition().getY();
                int diff = yPos - middle;
                int newY = yPos - (diff * 2);
                Mouse.setCursorPosition((int)inputManager.getCursorPosition().getX(), newY);

                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getWorldCoordinates(inputManager.getCursorPosition(), 0),
                        cam.getWorldCoordinates(inputManager.getCursorPosition(), 1));

                GraphicsUtils.getClickables().get(0).collideWith(ray, results);
                System.out.println(Game.getStatus().toString());
                for (CollisionResult result : results) {
                    Node target = result.getGeometry().getParent();
                    Game.OBJECT_TYPE type = Game.stringToObjectType(target.getUserData("type"));

                    if (type == Game.OBJECT_TYPE.HIGHLIGHT_NODE || type == Game.OBJECT_TYPE.TEXT || type == Game.OBJECT_TYPE.IMAGE) {
                        continue;
                    }

                    System.out.println("Clicked on " + result.getGeometry().getName());

                    if (Game.getStatus() == Game.STATUS.ENERGY_PHASE &&
                            target.getUserData("type") == Game.OBJECT_TYPE.ENERGY.toString()) {

                        Game.getPlayer().getStoredEnergy().addEnergy(
                                EnergyState.getEnumValueFromString(target.getUserData("energyType")), 1);
                        RenderQueue.getInstance().queueUpdate(RenderQueue.UpdateType.UPDATE_BOARD);
                        Game.setStatusMain();

                    } else if (Game.getStatus() == Game.STATUS.MAIN_PHASE) {
                        if (type == Game.OBJECT_TYPE.CARD) {
                            selectCard(target);
                            Game.setStatusTargeting();
                        } else if (type == Game.OBJECT_TYPE.ENERGY) {
                            selectEnergy(target);
                            Game.setStatusImbuing();
                        } else if (type == Game.OBJECT_TYPE.BUTTON) {
                            Game.endTurn(Game.getPlayer());
                        }
                    } else if (Game.getStatus() == Game.STATUS.IMBUING) {
                        if (type == Game.OBJECT_TYPE.POSITION) {
                            Hero toImbue = (Hero) BoardState.getInstance().getEntityForPosition(target.getUserData("position"));
                            InputUtils.imbueEnergy(Game.getCurrentPlayer(), toImbue, selectedEnergy, 1);
                            GraphicsUtils.renderBoard();
                            Game.setStatusMain();
                        } else {
                            Game.setStatusMain();
                        }
                    } else if (Game.getStatus() == Game.STATUS.SELECTING_CARD_TARGET) {
                        if (type == Game.OBJECT_TYPE.CARD) {
                            if (target.equals(selectedCard)) {
                                deselectCard();
                            } else {
                                selectCard(target);
                            }
                            Game.setStatusTargeting();
                        } else if (type == Game.OBJECT_TYPE.ENERGY) {
                            selectEnergy(target);
                            Game.setStatusImbuing();
                        } else if (type == Game.OBJECT_TYPE.POSITION) {
                            try {
                                Card card = Game.getPlayer().getHand().findCardById(selectedCard.getUserData("id"));
                                Game.getPlayer().playCard(card, target.getUserData("position"));
                                Game.setStatusMain();
                                GraphicsUtils.renderBoard();
                                GraphicsUtils.renderCards(Game.getPlayer());
                            } catch (NullPointerException | IllegalMoveException e) {
                                return;
                            } catch (PositionOccupiedException e) {
                                LogUtils.logWarning("There is already something there!");
                                LogUtils.printBoardPositionDetails(
                                        BoardState.getInstance().allPositions().get(target.getUserData("position")));
                            }
                        } else {
                            deselectCard();
                        }
                    } else if (Game.getStatus() == Game.STATUS.SELECTING_ABILITY_TARGET) {
                        if (type == Game.OBJECT_TYPE.POSITION) {
                            BoardEntity targetEntity = BoardState.getInstance().getEntityForPosition(target.getUserData("position"));
                            if (targetEntity != null) {
                                Game.getSelector().execute(targetEntity);
                                GraphicsUtils.renderBoard();
                            }
                        }
                    } else if (type == Game.OBJECT_TYPE.POSITION) {
                        LogUtils.printBoardPositionDetails(BoardState.getInstance().allPositions().get(target.getUserData("position")));
                    } else {
                        deselectCard();
                    }
                    GraphicsUtils.renderBoard();
                    return;
                }
            }

            int delta = 0;
            if (name.equals("var_up")) {
                delta++;
            } else if (name.equals("var_down")) {
                delta--;
            }

            if (delta != 0) {
                var += delta;
                System.out.println(var);
            }
        }
    };

    private RawInputListener rawInputListener = new RawInputListener() {

        @Override
        public void beginInput() {

        }

        @Override
        public void endInput() {

        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent joyAxisEvent) {

        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent joyButtonEvent) {

        }

        @Override
        public void onMouseMotionEvent(MouseMotionEvent mouseMotionEvent) {
            Spatial cardPreview = getGuiNode().getChild("card_preview");
            if (cardPreview != null) {
                getGuiNode().detachChild(cardPreview);
            }
            CollisionResults results = new CollisionResults();
            Vector2f mousePos = new Vector2f(mouseMotionEvent.getX(), mouseMotionEvent.getY());

            Ray ray = new Ray(cam.getWorldCoordinates(mousePos, 0),
                    cam.getWorldCoordinates(mousePos, 1));

            GraphicsUtils.getClickables().get(0).collideWith(ray, results);

            for (CollisionResult result : results) {
                if (!result.getGeometry().getName().equals("BitmapFont")) {
                    Node target = result.getGeometry().getParent();
                    if (target == null || target.getUserData("type") == null) {
                        return;
                    }
                    switch(Game.stringToObjectType(target.getUserData("type"))) {
                        case POSITION:
                            BoardEntity entity = BoardState.getInstance().getEntityForPosition(target.getUserData("position"));
                            if (entity instanceof Hero) {
                                float x = target.getLocalTranslation().getX();
                                float y = target.getLocalTranslation().getY() + 3;
                                float z = target.getLocalTranslation().getZ();

                                GraphicsUtils.guiNode().attachChild(GraphicsUtils.generateCardPreview((Hero)entity));
                            }
                        case CARD:
                        case ENERGY:
                            GraphicsUtils.highlightNode(target);
                            return;
                        default:
                            GraphicsUtils.removeHighlight();
                            break;
                    }
                }
            }
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent mouseButtonEvent) {

        }

        @Override
        public void onKeyEvent(KeyInputEvent keyInputEvent) {

        }

        @Override
        public void onTouchEvent(TouchEvent touchEvent) {

        }
    };

    private void showCardView(Node card) {

    }

    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            float mod = 0;
            float delta = CAMERA_SPEED;
            float ZOOM_SPEED = 0.5f;
            float currentAngle = chaseCam.getVerticalRotation();
            if (name.equals("camera_up") && currentAngle < BOARD_VIEW_ANGLE) {
                if (chaseCam.getDistanceToTarget() == DEFAULT_CAMERA_DISTANCE) {
                    chaseCam.setDefaultVerticalRotation(currentAngle + delta);
                } else {
                    chaseCam.setDefaultDistance(chaseCam.getDistanceToTarget() - ZOOM_SPEED);
                }
            } else if (name.equals("camera_down")) {
                if (currentAngle > HAND_VIEW_ANGLE && chaseCam.getDistanceToTarget() == DEFAULT_CAMERA_DISTANCE) {
                    chaseCam.setDefaultVerticalRotation(currentAngle - delta);
                } else if (chaseCam.getDistanceToTarget() <= 28){
                    chaseCam.setDefaultDistance(chaseCam.getDistanceToTarget() + (ZOOM_SPEED));
                }
            }
        }
    };

    @Override
    public void simpleInitApp() {

        LogUtils.initGson();

        GraphicsUtils.init(assetManager, guiNode);

        System.out.println("Beginning...");

        Deck yourDeck = new Deck();
        for (int i = 0; i < 3; i++) {
            yourDeck.addCards(Cards.allCards());
        }
        yourDeck.shuffle();

        Player you = new Player(0, yourDeck, true);

        you.fillStockpile();

        Player opponent = new Player(1, yourDeck, false);
        opponent.fillStockpile();

        InputUtils.initInput();

        Game game = new Game(this, you, opponent);

        GraphicsUtils.initBoardNode();
        GraphicsUtils.getBoardNode().setUserData("type", Game.OBJECT_TYPE.BOARD.toString());

        GraphicsUtils.getClickables().add(GraphicsUtils.getBoardNode());
        GraphicsUtils.getClickables().add(GraphicsUtils.getBoardNode());
        rootNode.attachChild(GraphicsUtils.getBoardNode());

        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, GraphicsUtils.getBoardNode().getChild("Board"), inputManager);
        chaseCam.setDefaultHorizontalRotation(GraphicsUtils.QUARTER_ROTATION);
        chaseCam.setDefaultDistance(DEFAULT_CAMERA_DISTANCE);
        chaseCam.setDefaultVerticalRotation(HAND_VIEW_ANGLE);
        chaseCam.setMinVerticalRotation(HAND_VIEW_ANGLE);
        chaseCam.setMaxVerticalRotation(BOARD_VIEW_ANGLE);

        GraphicsUtils.renderCards(you);
        GraphicsUtils.renderBoard();

        initKeys();
        inputManager.deleteMapping(CameraInput.CHASECAM_UP);
        inputManager.deleteMapping(CameraInput.CHASECAM_DOWN);
        inputManager.deleteMapping(CameraInput.CHASECAM_MOVELEFT);
        inputManager.deleteMapping(CameraInput.CHASECAM_MOVERIGHT);
        inputManager.deleteMapping(CameraInput.CHASECAM_ZOOMIN);
        inputManager.deleteMapping(CameraInput.CHASECAM_ZOOMOUT);

        GraphicsUtils.initHudText();

        game.start();
    }

    private String removeColors(String str) {
        return str.replaceAll("\u001B.{1,3}m", "");
    }

    @Override
    public void simpleUpdate(float tpf) {
        RenderQueue.getInstance().executeUpdate();
    }
}