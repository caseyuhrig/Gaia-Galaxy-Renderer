package caseyuhrig.gaia;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class ScreenCoordinates {

    protected final int screenWidth;
    protected final int screenHeight;


    public ScreenCoordinates(final int screenWidth, final int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }


    public abstract Point getScreenCoordinates(final RenderingData data);

    public abstract Point2D.Double getDataCoordinates(final int screenX, final int screenY);


    public static int clamp(final double value, final int min, final int max) {
        return (int) Math.max(min, Math.min(value, max));
    }

    public static double clamp(final double value, final double min, final double max) {
        return Math.max(min, Math.min(value, max));
    }
}
