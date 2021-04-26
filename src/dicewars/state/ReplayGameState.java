package dicewars.state;

import dicewars.DiceWars;
import dicewars.DiceDistribution;
import dicewars.GameSave;
import dicewars.map.Tile;
import dicewars.player.Player;
import dicewars.player.PlayerAction;
import dicewars.rendering.Hexagon;
import dicewars.rendering.RenderablePolygon;
import dicewars.rendering.RenderableText;
import dicewars.rendering.Renderer;
import dicewars.ui.DialogBox;
import dicewars.ui.GUI;
import dicewars.ui.PushButton;
import dicewars.GameEvent;

import java.awt.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReplayGameState implements GameState {
    private final Renderer renderer;
    private final GUI gui;
    private Player currentPlayer;
    private Point cursorPos = new Point(0, 0);
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);
    private String statusText = "";
    private boolean paused = false;
    private Timer timer;
    private GameSave gs;
    private int currentAction = 0;

    public ReplayGameState(Renderer r) {
        this.renderer = r;
        this.gui = new GUI(this.renderer);
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (currentAction < gs.actionHistory.size()) {
                    GameEvent event = gs.actionHistory.get(currentAction++);
                    
                    if (event instanceof PlayerAction) {
                        PlayerAction action = (PlayerAction) event;
                        if (!action.isEndTurn() && !action.isActionOf(currentPlayer)) {
                            System.err.println("Wrong replay file! Action was not performed by this player.");
                            DiceWars.endGame();
                        }
                        if (action.isEndTurn()) {
                            endTurn();
                        } else {
                            action.execute();
                            System.out.println(currentAction + ": " + event);
                        }
                    } else if (event instanceof DiceDistribution) {
                        DiceDistribution ete = (DiceDistribution) event;
                        ete.execute();
                        System.out.println(currentAction + ": " + event);
                    }
                } else {
                    paused = true;
                    gui.addDialogBox(new DialogBox("Replay file over.", new Point(0, 750),
                        new PushButton("Back to menu", new Point(0, 0)){
                            @Override
                            public void onClick() {
                                DiceWars.endGame();
                            }
                        },
                        new PushButton("Back to menu", new Point(0, 0)){
                            @Override
                            public void onClick() {
                                DiceWars.endGame();
                            }
                        })
                    );
                }
            }
        });
        gui.addButton(new PushButton("Step", new Point(0, 1000)){
            @Override
            public void onClick() {
                if (currentAction < gs.actionHistory.size()) {
                    GameEvent event = gs.actionHistory.get(currentAction++);
                    
                    if (event instanceof PlayerAction) {
                        PlayerAction action = (PlayerAction) event;
                        if (!action.isEndTurn() && !action.isActionOf(currentPlayer)) {
                            System.err.println("Wrong replay file! Action was not performed by this player.");
                            DiceWars.endGame();
                        }
                        if (action.isEndTurn()) {
                            endTurn();
                        } else {
                            action.execute();
                            System.out.println(currentAction + ": " + event);
                        }
                    } else if (event instanceof DiceDistribution) {
                        DiceDistribution ete = (DiceDistribution) event;
                        ete.execute();
                        System.out.println(currentAction + ": " + event);
                    }
                } else {
                    paused = true;
                    gui.addDialogBox(new DialogBox("Replay file over.", new Point(0, 750),
                        new PushButton("Back to menu", new Point(0, 0)){
                            @Override
                            public void onClick() {
                                DiceWars.endGame();
                            }
                        },
                        new PushButton("Back to menu", new Point(0, 0)){
                            @Override
                            public void onClick() {
                                DiceWars.endGame();
                            }
                        })
                    );
                }
            }
        });
    }

    @Override
    public void startup() {
        this.gui.startup();
        paused = false;
        timer.start();
    }

    @Override
    public void shutdown() {
        this.gui.shutdown();
        timer.stop();
    }

    public void setGameSave(GameSave gs) {
        this.gs = gs;
        this.currentAction = 0;
        this.currentPlayer = gs.players.get(0);
    }

    private void endTurn() {
        int playersAlive = 0;
        for (Player p : gs.players) {
            if (gs.map.getTiles(p).size() != 0) {
                playersAlive++;
            }
        }
        if (playersAlive > 1) {
            do {
                currentPlayer = getNextPlayer();
            } while(gs.map.getTiles(currentPlayer).size() == 0);
            System.out.println("Next player (" + currentPlayer + ")");
        }
    }

    private Player getNextPlayer() {
        if (currentPlayer == null) return gs.players.get(0);
        int i;
        for (i = 0; i < gs.players.size() && gs.players.get(i) != currentPlayer; i++);
        return gs.players.get((i + 1) % gs.players.size());
    }

    @Override
    public void update() {
        if (!this.paused) {
            /*long currentTime = System.nanoTime();
            if (currentTime - lastTick > 1000000000) {
                lastTick = currentTime;
                if (currentAction < gs.actionHistory.size()) {
                    GameEvent event = gs.actionHistory.get(currentAction++);
                    System.out.println(currentAction + ": " + event);
                    if (event instanceof PlayerAction) {
                        PlayerAction action = (PlayerAction) event;
                        if (!action.isEndTurn() && !action.isActionOf(currentPlayer)) {
                            System.err.println("Wrong replay file! Action was not performed by this player.");
                            DiceWars.endGame();
                        }
                        if (action.isEndTurn()) {
                            endTurn();
                        } else {
                            action.execute();
                        }
                    } else if (event instanceof EndTurnEvent) {
                        EndTurnEvent ete = (EndTurnEvent) event;
                        ete.execute();
                    }
                } else {
                    this.paused = true;
                    gui.addDialogBox(new DialogBox("Replay file over.", new Point(0, 750),
                        new PushButton("Back to menu", new Point(0, 0)){
                            @Override
                            public void onClick() {
                                DiceWars.endGame();
                            }
                        },
                        new PushButton("Back to menu", new Point(0, 0)){
                            @Override
                            public void onClick() {
                                DiceWars.endGame();
                            }
                        })
                    );
                }
            }*/
        }
        gui.update();
    }

    private void renderMap() {
        Tile[][] map = gs.map.getMap();

        for (int y = 0; y < gs.map.ROWS; y++) {
            for (int x = 0; x < gs.map.COLUMNS; x++) {
                int screenX = 50 + x * 87 + (y % 2 == 1 ? 43 : 0);
                int screenY = 50 + y * 76;
                if (!map[x][y].neutral) {
                    Hexagon h = new Hexagon(screenX, screenY, 50);
                    if (h.contains(cursorPos))
                        renderer.addToQueue(new RenderablePolygon(h, Color.GREEN));
                    for (Player p: gs.players) {
                        if (p == map[x][y].getOwner()) {
                            renderer.addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 45), p.color));
                            break;
                        }
                    }
                } else {
                    Hexagon h = new Hexagon(screenX, screenY, 45);
                    renderer.addToQueue(new RenderablePolygon(h, Color.LIGHT_GRAY));
                }
            }
        }

        // Render number of dices
        for (int y = 0; y < gs.map.ROWS; y++) {
            for (int x = 0; x < gs.map.COLUMNS; x++) {
                if (!map[x][y].neutral) {
                    int screenX = 25 + x * 87 + (y % 2 == 1 ? 43 : 0);
                    int screenY = 50 + y * 76;
                    renderer.addToQueue(new RenderableText("K: " + map[x][y].getDices(), screenX, screenY, ARIAL_FONT, Color.BLACK));
                }
            }
        }
    }

    private void renderPlayerList() {
        int screenX = 15;
        int screenY = 600;

        for (int i = 0; i < gs.players.size(); i++) {
            Player p = gs.players.get(i);
            screenX = 15;
            renderer.addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 10), p.color));
            screenX = 30;
            screenY += 7;
            renderer.addToQueue(new RenderableText(
                    "Player " + (i + 1) + ": " + gs.map.getTiles(p).size() + " tiles " + (currentPlayer == p ? "<<" : ""),
                    screenX, screenY, ARIAL_FONT, Color.BLACK));
            screenY += 25;
        }
    }

    @Override
    public void render() {
        renderMap();
        renderPlayerList();
        renderer.addToQueue(new RenderableText(statusText, 0, renderer.getHeight() - 10, ARIAL_FONT, Color.BLACK));
        gui.render();
    }
}
