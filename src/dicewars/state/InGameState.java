package dicewars.state;

import dicewars.DiceWars;
import dicewars.DiceDistribution;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFileChooser;

public class InGameState implements GameState, MouseListener {
    final Renderer renderer;
    private final GUI gui;
    private GameMap gameMap;
    private List<Player> players;
    private Player currentPlayer;
    private Point cursorPos = new Point(0, 0);
    private Tile hoveredTile = null;
    private Tile selectedTile = null;
    private long lastAITick = 0;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);
    private String statusText = "";
    private boolean paused = false;
    private boolean aiOnly;
    private GameSave gs;

    public InGameState(Renderer r, boolean aiOnly) {
        this.renderer = r;
        this.gui = new GUI(this.renderer);
        gui.addButton(new PushButton("Save replay", new Point(0, 800)){
            @Override
            public void onClick() {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                int option = fc.showSaveDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                        oos.writeObject(gs);
                    } catch (FileNotFoundException e) {
                        System.err.println("Failed to save replay!");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.err.println("Failed to save replay!");
                        e.printStackTrace();
                    }
                }
                DiceWars.endGame();
            }
        });
        this.aiOnly = aiOnly;
    }

    @Override
    public void startup() {
        this.renderer.addMouseListener(this);
        this.gui.startup();
        paused = false;
        gs = new GameSave(players, gameMap);
    }

    @Override
    public void shutdown() {
        this.renderer.removeMouseListener(this);
        this.gui.shutdown();
    }

    public void setPlayers(Player[] players) {
        this.players = new ArrayList<>();
        this.players.addAll(Arrays.asList(players));
        this.currentPlayer = players[0];
    }

    public void setMap(GameMap map) {
        this.gameMap = map;
    }

    public Player[] getPlayers() {
        Player[] arr = new Player[this.players.size()];
        arr = this.players.toArray(arr);
        return arr;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    private void endTurn() {
        ArrayList<Tile> tilesOfPlayer = gameMap.getTiles(currentPlayer);
        int k = tilesOfPlayer.size() / 2;
        DiceDistribution dd = gameMap.distributeDices(tilesOfPlayer, k);
        gs.addEndTurnEvent(dd);
        int playersAlive = 0, humanPlayersAlive = 0;
        for (Player p : players) {
            if (gameMap.getTiles(p).size() != 0) {
                playersAlive++;
                if (p instanceof HumanPlayer) humanPlayersAlive++;
            }
        }
        if (!aiOnly && humanPlayersAlive == 0) {
            aiOnly = true;
            paused = true;
            gui.addDialogBox(new DialogBox("No human players left alive.", new Point(0, 0),
                new PushButton("Watch AI players finish...", new Point(0, 0)){
                    @Override
                    public void onClick() {
                        paused = false;
                    }
                },
                new PushButton("End game.", new Point(0, 0)){
                    @Override
                    public void onClick() {
                        DiceWars.endGame();
                    }
                })
            );
        }
        if (playersAlive > 1) {
            do {
                currentPlayer = getNextPlayer();
            } while(gameMap.getTiles(currentPlayer).size() == 0);
            System.out.println("Next player (" + currentPlayer + ")");
        } else {
            paused = true;
            gui.addDialogBox(new DialogBox("Would you like to save a replay?", new Point(0, 0),
                new PushButton("Save replay", new Point(0, 0)){
                    @Override
                    public void onClick() {
                        JFileChooser fc = new JFileChooser();
                        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                        int option = fc.showSaveDialog(null);
                        if (option == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                                oos.writeObject(gs);
                            } catch (FileNotFoundException e) {
                                System.err.println("Failed to save replay!");
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.err.println("Failed to save replay!");
                                e.printStackTrace();
                            }
                        }
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

    private Player getNextPlayer() {
        if (currentPlayer == null) return players.get(0);
        int i;
        for (i = 0; i < players.size() && players.get(i) != currentPlayer; i++);
        return players.get((i + 1) % players.size());
    }

    @Override
    public void update() {
        if (!this.paused) {
            long currentTime = System.nanoTime();
            Tile[][] map = gameMap.getMap();
            this.cursorPos = MouseInfo.getPointerInfo().getLocation();
            this.cursorPos.translate(-renderer.getLocationOnScreen().x, -renderer.getLocationOnScreen().y);
            hoveredTile = null;
            for (int y = 0; y < gameMap.ROWS; y++) {
                for (int x = 0; x < gameMap.COLUMNS; x++) {
                    if (!map[x][y].neutral) {
                        int screenX = 50 + x * 87 + (y % 2 == 1 ? 43 : 0);
                        int screenY = 50 + y * 76;
                        Hexagon h = new Hexagon(screenX, screenY, 50);
                        if (h.contains(cursorPos)) {
                            hoveredTile = map[x][y];
                        }
                    }
                }
            }
            if (currentPlayer instanceof AIPlayer && currentTime - lastAITick > 100000000) {
                lastAITick = currentTime;
                PlayerAction pAction = ((AIPlayer) currentPlayer).tick(gameMap);
                gs.addPlayerAction(pAction);
                if (pAction.isEndTurn()) {
                    endTurn();
                } else {
                    pAction.execute();
                    this.statusText = pAction.toString();
                }
            }
        }
        gui.update();
    }

    private void renderMap() {
        Tile[][] map = gameMap.getMap();

        // Render the selected tile indicator
        if (selectedTile != null) {
            int screenX = 50 + selectedTile.X * 87 + (selectedTile.Y % 2 == 1 ? 43 : 0);
            int screenY = 50 + selectedTile.Y * 76;
            renderer.addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 50), Color.GREEN));
            ArrayList<Tile> neighbours = gameMap.getNeighbours(selectedTile.X, selectedTile.Y);
            for (Tile t : neighbours) {
                if (t.getOwner() != map[selectedTile.X][selectedTile.Y].getOwner()) {
                    int screenNX = 50 + t.X * 87 + (t.Y % 2 == 1 ? 43 : 0);
                    int screenNY = 50 + t.Y * 76;
                    renderer.addToQueue(new RenderablePolygon(new Hexagon(screenNX, screenNY, 50), Color.DARK_GRAY));
                }
            }
        }

        for (int y = 0; y < gameMap.ROWS; y++) {
            for (int x = 0; x < gameMap.COLUMNS; x++) {
                int screenX = 50 + x * 87 + (y % 2 == 1 ? 43 : 0);
                int screenY = 50 + y * 76;
                if (!map[x][y].neutral) {
                    Hexagon h = new Hexagon(screenX, screenY, 50);
                    if (h.contains(cursorPos))
                        renderer.addToQueue(new RenderablePolygon(h, Color.GREEN));
                    for (Player p: players) {
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
        for (int y = 0; y < gameMap.ROWS; y++) {
            for (int x = 0; x < gameMap.COLUMNS; x++) {
                if (!map[x][y].neutral) {
                    int screenX = 25 + x * 87 + (y % 2 == 1 ? 43 : 0);
                    int screenY = 50 + y * 76;
                    renderer.addToQueue(new RenderableText("K: " + map[x][y].getDices(), screenX, screenY, ARIAL_FONT, Color.BLACK));
                }
            }
        }

        int screenX = renderer.getWidth() - 50;
        int screenY = renderer.getHeight() - 50;
        Hexagon h = new Hexagon(screenX, screenY, 50);
        if (currentPlayer instanceof HumanPlayer)
            renderer.addToQueue(new RenderablePolygon(h, currentPlayer.color));
        else
            renderer.addToQueue(new RenderablePolygon(h, Color.GRAY));
        renderer.addToQueue(new RenderableText("End turn", screenX - 35, screenY + 5, ARIAL_FONT, Color.WHITE));
        if (h.contains(cursorPos)) {
            hoveredTile = new Tile(-2, -2);
        }
    }

    private void renderPlayerList() {
        int screenX = 15;
        int screenY = 600;

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            screenX = 15;
            renderer.addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 10), p.color));
            screenX = 30;
            screenY += 7;
            renderer.addToQueue(new RenderableText(
                    "Player " + (i + 1) + ": " + gameMap.getTiles(p).size() + " tiles " + (currentPlayer == p ? "<<" : ""),
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

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (!this.paused) {
            if (currentPlayer instanceof HumanPlayer) {
                if (hoveredTile != null && hoveredTile.X == -2 && hoveredTile.Y == -2) {
                    gs.addPlayerAction(new PlayerAction(null, null, true));
                    System.out.println("End turn");
                    endTurn();
                } else if (selectedTile != null) {
                    if (hoveredTile != null) {
                        PlayerAction a = new PlayerAction(selectedTile, hoveredTile, false);
                        gs.addPlayerAction(a);
                        a.execute();
                        this.statusText = a.toString();
                    }
                    selectedTile = null;
                } else if (hoveredTile != null && hoveredTile.getOwner() == currentPlayer) {
                    selectedTile = hoveredTile;
                }
            }
        }
    }

    public void setAiOnly(boolean aiOnly) {
        this.aiOnly = aiOnly;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
