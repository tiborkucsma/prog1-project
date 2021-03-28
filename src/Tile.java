public class Tile {
    Player owner = null;
    int dices = 0;
    boolean neutral = true;
    final int X;
    final int Y;

    public Tile(int x, int y) {
        this.X = x;
        this.Y = y;
    }
}
