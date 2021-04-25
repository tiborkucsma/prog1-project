package dicewars.state;

import dicewars.DiceWars;
import dicewars.GameSave;
import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.AIPlayer;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.player.PlayerAction;
import dicewars.rendering.Hexagon;
import dicewars.rendering.RenderablePolygon;
import dicewars.rendering.RenderableText;
import dicewars.rendering.Renderer;
import dicewars.ui.DialogBox;
import dicewars.ui.GUI;
import dicewars.ui.PushButton;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ReplayGameState implements GameState {
    private final Renderer renderer;
    private final GUI gui;
    private Player currentPlayer;
    private Point cursorPos = new Point(0, 0);
    private long lastTick = 0;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);
    private String statusText = "";
    private boolean paused = false;
    private GameSave gs;
    private int currentAction = 0;

    public ReplayGameState(Renderer r) {
        this.renderer = r;
        this.gui = new GUI(this.renderer);
    }

    @Override
    public void startup() {
        this.gui.startup();
        paused = false;
    }

    @Override
    public void shutdown() {
        this.gui.shutdown();
    }

    public void setGameSave(GameSave gs) {
        this.gs = gs;
        this.currentAction = 0;
        this.currentPlayer = gs.players.get(0);
    }

    private void endTurn() {
        ArrayList<Tile> tilesOfPlayer = gs.map.getTiles(currentPlayer);
        Random rand = new Random();
        int k = tilesOfPlayer.size() / 2;
        while (k > 0 && tilesOfPlayer.size() > 0) {
            int r = rand.nextInt(tilesOfPlayer.size());
            if (tilesOfPlayer.get(r).dices < 8) {
                tilesOfPlayer.get(r).dices++;
                k--;
            }
            if (tilesOfPlayer.get(r).dices == 8) tilesOfPlayer.remove(r);
        }
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
            long currentTime = System.nanoTime();
            if (currentTime - lastTick > 10000000) {
                lastTick = currentTime;
                if (currentAction < gs.actionHistory.size()) {
                    PlayerAction action = gs.actionHistory.get(currentAction++);
                    if (!action.isEndTurn() && !action.isActionOf(currentPlayer)) {
                        System.err.println("Wrong replay file! Action was not performed by this player.");
                        DiceWars.endGame();
                    }
                    if (action.isEndTurn()) {
                        endTurn();
                    } else {
                        action.execute();
                    }
                } else {
                    this.paused = true;
                    gui.addDialogBox(new DialogBox("Replay file over.", new Point(0, 0),
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
                        if (p == map[x][y].owner) {
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
                    renderer.addToQueue(new RenderableText("K: " + map[x][y].dices, screenX, screenY, ARIAL_FONT, Color.BLACK));
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
