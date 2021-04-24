package rendering;

import java.awt.*;

public class Hexagon extends Polygon {
    public Hexagon(int px, int py, int r)
    {
        super();
        super.xpoints = new int[6];
        super.ypoints = new int[6];
        super.npoints = 6;

        for (int i = 0; i < 6; i++)
        {
            double x = Math.cos(Math.toRadians(i * 60 + 30)) * r;
            double y = Math.sin(Math.toRadians(i * 60 + 30)) * r;

            xpoints[i] = (int) Math.round(x);
            ypoints[i] = (int) Math.round(y);
        }

        translate(px, py);
    }
}
