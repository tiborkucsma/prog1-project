package dicewars.rendering;

import javax.swing.JComponent;

import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.List;


/**
 * This class is responsible for rendering a game state (game map and player list), and taking mouse input from the map tiles
 */
public class GameStateRenderer extends Renderer {
    private GameState gameState;
    private Tile prevSelectedTile = null;
    private Tile selectedTile = null;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 18);
    private String statusText = "";

    public GameStateRenderer() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        GameMap map = gameState.getMap();
        
        if (gameState != null) {
            if (selectedTile != null && selectedTile.getOwner() == gameState.getCurrentPlayer()) {
                int screenX = 50 + selectedTile.X * 87 + (selectedTile.Y % 2 == 1 ? 43 : 0);
                int screenY = 50 + selectedTile.Y * 76;
                addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 50), Color.GREEN));
                List<Tile> neighbours = map.getNeighbours(selectedTile.X, selectedTile.Y);
                for (Tile t : neighbours) {
                    if (t.getOwner() != map.getTile(selectedTile.X, selectedTile.Y).getOwner()) {
                        int screenNX = 50 + t.X * 87 + (t.Y % 2 == 1 ? 43 : 0);
                        int screenNY = 50 + t.Y * 76;
                        addToQueue(new RenderablePolygon(new Hexagon(screenNX, screenNY, 50), Color.DARK_GRAY));
                    }
                }
            }
            
            for (int y = 0; y < map.ROWS; y++) {
                for (int x = 0; x < map.COLUMNS; x++) {
                    map.getTile(x, y).render(this);
                }
            }

            int screenX = 15;
            int screenY = 600;
    
            List<Player> players = gameState.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                screenX = 15;
                addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 10), p.color));
                screenX = 30;
                screenY += 7;
                addToQueue(new RenderableText(
                        "Player " + (i + 1) + ": " + gameState.getMap().getTiles(p).size() + " tiles " + (gameState.getCurrentPlayer() == p ? "<<" : ""),
                        screenX, screenY, ARIAL_FONT, Color.BLACK));
                screenY += 25;
            }

            addToQueue(new RenderableText(statusText, 0, getHeight() - 10, ARIAL_FONT, Color.BLACK));
        }

        super.paintComponent(g);
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public Tile getPrevSelectedTile() {
        return prevSelectedTile;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
        this.statusText = "";
    }

    @Override
    public Dimension getPreferredSize() {
        if (gameState != null) {
            return new Dimension(
                50 + gameState.getMap().COLUMNS * 87 + 43,
                50 + gameState.getMap().ROWS * 76 + 600 + gameState.getPlayers().size() * 32);
        }
        return super.getPreferredSize();
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        Tile h = gameState.getMap().getHoveredTile();
        if (e.getID() == MouseEvent.MOUSE_CLICKED &&
            e.getButton() == MouseEvent.BUTTON1)
        {
            if (gameState.getCurrentPlayer() instanceof HumanPlayer &&
                h != null)
            {
                prevSelectedTile = selectedTile;
                selectedTile = h;
            } else {
                prevSelectedTile = selectedTile;
                selectedTile = null;
            }
        }
        super.processMouseEvent(e);
        if (selectedTile != null && prevSelectedTile != null) {
            selectedTile = null;
            prevSelectedTile = null;
        }
    }

}
