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
    private final Renderer renderer;
    private final Point hoveredTile = new Point(-1, -1);
    private final Point selectedTile = new Point(-1, -1);
    private final Timer timer = new Timer(10, this);
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 20);

    private DiceWars()
    {
        players = new Player[] { new HumanPlayer(), new AIPlayer(), new AIPlayer(), new AIPlayer() };
        players[0].color = Color.BLUE;
        players[1].color = Color.YELLOW;
        players[2].color = Color.ORANGE;
        players[3].color = Color.RED;
        gameMap = new GameMap(players);
        renderer = new Renderer();
        timer.start();
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

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        selectedTile.x = hoveredTile.x;
        selectedTile.y = hoveredTile.y;
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

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == timer)
        {
            Tile[][] map = gameMap.getMap();

            Point cursorPos = MouseInfo.getPointerInfo().getLocation();
            cursorPos.translate(-renderer.getLocationOnScreen().x, -renderer.getLocationOnScreen().y);

            hoveredTile.x = -1;
            hoveredTile.y = -1;

            for (int y = 0; y < gameMap.ROWS; y++) {
                for (int x = 0; x < gameMap.COLUMNS; x++) {
                    if (!map[x][y].neutral) {
                        int screenX = 50 + x * 87 + (y % 2 == 1 ? 43 : 0);
                        int screenY = 50 + y * 76;
                        Hexagon h = new Hexagon(screenX, screenY, 50);
                        if (h.contains(cursorPos)) {
                            renderer.addToQueue(new RenderablePolygon(h, Color.GREEN));
                            hoveredTile.x = x;
                            hoveredTile.y = y;
                        } else {
                            for (Player p: players) {
                                if (p == map[x][y].owner) {
                                    renderer.addToQueue(new RenderablePolygon(h, p.color));
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (selectedTile.x != -1 && selectedTile.y != -1) {
                int screenX = 50 + selectedTile.x * 87 + (selectedTile.y % 2 == 1 ? 43 : 0);
                int screenY = 50 + selectedTile.y * 76;
                renderer.addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 40), Color.DARK_GRAY));
                Tile[] neighbours = gameMap.getNeighbours(selectedTile.x, selectedTile.y);
                for (Tile t : neighbours) {
                    if (t.owner != map[selectedTile.x][selectedTile.y].owner) {
                        int screenNX = 50 + t.X * 87 + (t.Y % 2 == 1 ? 43 : 0);
                        int screenNY = 50 + t.Y * 76;
                        renderer.addToQueue(new RenderablePolygon(new Hexagon(screenNX, screenNY, 20), Color.DARK_GRAY));
                    }
                }
            }

            for (int y = 0; y < gameMap.ROWS; y++) {
                for (int x = 0; x < gameMap.COLUMNS; x++) {
                    if (!map[x][y].neutral) {
                        int screenX = 25 + x * 87 + (y % 2 == 1 ? 43 : 0);
                        int screenY = 50 + y * 76;
                        renderer.addToQueue(new RenderableText("K: " + map[x][y].dices, screenX, screenY, ARIAL_FONT, Color.BLACK));
                    }
                }
            }

            renderer.revalidate();
            renderer.repaint();
        }
    }
}
