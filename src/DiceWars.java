import rendering.RenderablePolygon;
import rendering.RenderableText;
import rendering.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class DiceWars implements MouseListener, ActionListener {
    private final GameMap gameMap;
    private Player[] players;
    private Player currentPlayer;
    private final Renderer renderer;
    private Point cursorPos = new Point(0, 0);
    private Tile hoveredTile = null;
    private Tile selectedTile = null;
    private final Timer timer = new Timer(10, this);
    private final Timer aiTimer = new Timer(500, this);
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);

    private DiceWars()
    {
        players = new Player[] { new HumanPlayer(), new AIPlayer(), new AIPlayer(), new AIPlayer(), new AIPlayer(), new AIPlayer() };
        players[0].color = Color.BLUE;
        players[1].color = Color.YELLOW;
        players[2].color = Color.ORANGE;
        players[3].color = Color.RED;
        players[4].color = Color.CYAN;
        players[5].color = Color.MAGENTA;
        currentPlayer = players[0];
        gameMap = new GameMap(players);
        renderer = new Renderer();
        timer.start();
        aiTimer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dice Wars");
        frame.setSize(550, 250);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DiceWars dw = new DiceWars();
        System.out.println(dw.gameMap);
        frame.addMouseListener(dw);
        frame.add(dw.renderer);
    }

    private void endTurn() {
        aiTimer.stop();
        currentPlayer = getNextPlayer();
        System.out.println("Next player (" + currentPlayer + ")");
        aiTimer.start();
    }

    private Player getNextPlayer() {
        if (currentPlayer == null) return players[0];
        int i;
        for (i = 0; i < players.length && players[i] != currentPlayer; i++);
        return players[(i + 1) % players.length];
    }

    private void updateState() {
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
                if (t.owner != map[selectedTile.X][selectedTile.Y].owner) {
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
        for (int y = 0; y < gameMap.ROWS; y++) {
            for (int x = 0; x < gameMap.COLUMNS; x++) {
                if (!map[x][y].neutral) {
                    int screenX = 25 + x * 87 + (y % 2 == 1 ? 43 : 0);
                    int screenY = 50 + y * 76;
                    renderer.addToQueue(new RenderableText("K: " + map[x][y].dices, screenX, screenY, ARIAL_FONT, Color.BLACK));
                }
            }
        }

        int screenX = renderer.getWidth() - 50;
        int screenY = renderer.getHeight() - 50;
        Hexagon h = new Hexagon(screenX, screenY, 50);
        renderer.addToQueue(new RenderablePolygon(h, Color.GRAY));
        renderer.addToQueue(new RenderableText("End turn", screenX - 35, screenY + 5, ARIAL_FONT, Color.WHITE));
        if (h.contains(cursorPos)) {
            hoveredTile = new Tile(-2, -2);
        }

        /* For testing adjacency method
        if (hoveredTile != null) {
            for (int y = 0; y < gameMap.ROWS; y++) {
                for (int x = 0; x < gameMap.COLUMNS; x++) {
                    if (!map[x][y].neutral && GameMap.adjacent(hoveredTile, map[x][y])) {
                        int screenX = 50 + x * 87 + (y % 2 == 1 ? 43 : 0);
                        int screenY = 50 + y * 76;
                        renderer.addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 50), Color.GREEN));
                    }
                }
            }
        }*/
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == timer) {
            updateState();
            renderMap();

            renderer.revalidate();
            renderer.repaint();
        } else if (actionEvent.getSource() == aiTimer && currentPlayer instanceof AIPlayer) {
            if (((AIPlayer) currentPlayer).tick(gameMap) == -1) {
                endTurn();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (currentPlayer instanceof HumanPlayer) {
            if (hoveredTile != null && hoveredTile.X == -2 && hoveredTile.Y == -2) {
                System.out.println("End turn");
                endTurn();
            } else if (selectedTile != null) {
                if (hoveredTile != null &&
                        hoveredTile.owner != currentPlayer &&
                        GameMap.adjacent(selectedTile, hoveredTile)) {
                    currentPlayer.attack(selectedTile, hoveredTile);
                }
                selectedTile = null;
            } else if (hoveredTile != null && hoveredTile.owner == currentPlayer) {
                selectedTile = hoveredTile;
            }
        }
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
