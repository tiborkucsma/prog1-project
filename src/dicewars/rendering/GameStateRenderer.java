package dicewars.rendering;

import dicewars.map.GameMap;
import dicewars.map.Tile;
import dicewars.player.HumanPlayer;
import dicewars.player.Player;
import dicewars.state.GameState;

import java.awt.*;
import java.awt.event.MouseEvent;

import java.util.List;


/**
 * This class is responsible for rendering a game state (game map and player list), and taking mouse input from the map tiles
 */
public class GameStateRenderer extends Renderer {
    private GameState gameState;
    private Tile hoveredTile = null;
    private Tile prevSelectedTile = null;
    private Tile selectedTile = null;
    private static final Font ARIAL_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font ARIAL_FONT_SMALL = new Font("Arial", Font.PLAIN, 15);
    private String statusText = "";

    /**
     * Default constructor
     */
    public GameStateRenderer() {
        super();
    }

    /**
     * Renders the game state
     * @param g Graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        GameMap map = gameState.getMap();
        
        if (gameState != null) {
            List<Player> players = gameState.getPlayers();

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

            Point mousePos = getMousePosition();
            hoveredTile = null;
            
            for (int y = 0; y < map.ROWS; y++) {
                for (int x = 0; x < map.COLUMNS; x++) {
                    Tile tile = gameState.getMap().getTile(x, y);
                    int screenX = 50 + x * 87 + (y % 2 == 1 ? 43 : 0);
                    int screenY = 50 + y * 76;
                    
                    Hexagon h = new Hexagon(screenX, screenY, 50);
                    
                    if (mousePos != null && h.contains(mousePos)) hoveredTile = tile;
                    
                    if (!tile.neutral) {
                        if (tile == hoveredTile)
                            addToQueue(new RenderablePolygon(h, Color.GREEN));
                        addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 45), tile.getOwner().color));
                    
                        screenX -= 25;
                        addToQueue(new RenderableText("D: " + tile.getDices(), screenX, screenY - 10, ARIAL_FONT, Color.BLACK));
                        addToQueue(new RenderableText("P" + (players.indexOf(tile.getOwner()) + 1), screenX, screenY + 15, ARIAL_FONT_SMALL, Color.BLACK));
                    } else {
                        addToQueue(new RenderablePolygon(new Hexagon(screenX, screenY, 45), Color.LIGHT_GRAY));
                    }
                }
            }

            int screenX = 15;
            int screenY = 600;
    
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

    /**
     * Sets the status text (appears in lower left corner in game)
     * @param statusText text
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     *
     * @return Current selected tile
     */
    public Tile getSelectedTile() {
        return selectedTile;
    }

    /**
     *
     * @return Previous selected tile
     */
    public Tile getPrevSelectedTile() {
        return prevSelectedTile;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
        this.statusText = "";
    }

    /**
     *
     * @return Calculate needed screen space
     */
    @Override
    public Dimension getPreferredSize() {
        if (gameState != null) {
            return new Dimension(
                50 + gameState.getMap().COLUMNS * 87 + 43,
                50 + gameState.getMap().ROWS * 76 + 600 + gameState.getPlayers().size() * 32);
        }
        return super.getPreferredSize();
    }

    /**
     * Update the current and previous selected tile, then pass on the event
     * @param e Event
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED &&
            e.getButton() == MouseEvent.BUTTON1)
        {
            if (gameState.getCurrentPlayer() instanceof HumanPlayer &&
                hoveredTile != null)
            {
                prevSelectedTile = selectedTile;
                selectedTile = hoveredTile;
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
