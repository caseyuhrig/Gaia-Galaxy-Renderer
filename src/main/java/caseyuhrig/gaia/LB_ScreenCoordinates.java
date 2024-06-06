package caseyuhrig.gaia;

import java.awt.*;
import java.awt.geom.Point2D;

public class LB_ScreenCoordinates extends ScreenCoordinates {

    //private final double l_width = Math.abs(GaiaConst.min_l) + Math.abs(GaiaConst.max_l);
    //private final double l_offset = Math.abs(GaiaConst.min_l);
    //private final double b_height = Math.abs(GaiaConst.min_b) + Math.abs(GaiaConst.max_b);
    //private final double b_offset = Math.abs(GaiaConst.min_b);
    //private final double l_step;
    //private final double b_step;

    private final double spaceMinX = 0.0; //GaiaConst.min_l;
    private final double spaceMaxX = 360.0; //GaiaConst.max_l;
    private final double spaceMinY = -89.0; //GaiaConst.min_b;
    private final double spaceMaxY = 90.0; //GaiaConst.max_b;

    private final double spaceWidth = spaceMaxX - spaceMinX;
    private final double spaceHeight = spaceMaxY - spaceMinY;


    public LB_ScreenCoordinates(final int screenWidth, final int screenHeight) {
        super(screenWidth, screenHeight);
        //this.l_step = screenWidth / l_width;
        //this.b_step = screenHeight / b_height;
    }


    @Override
    public Point getScreenCoordinates(final RenderingData data) {
        double spaceX = data.l;
        // shift the spaceX value by 180 degrees
        spaceX = (spaceX + 180.0) % spaceMaxX;
        if (spaceX < spaceMinX) {
            spaceX += 360.0;
        }

        final double spaceY = data.b;
        final double normalizedSpaceX = (spaceX - spaceMinX) / spaceWidth;
        final double normalizedSpaceY = (spaceY - spaceMinY) / spaceHeight;
        // Invert the screenX calculation to flip the image on the y-axis
        final int screenW = screenWidth - 1;
        final int screenH = screenHeight - 1;
        final double screenX = screenW - (normalizedSpaceX * screenW);
        final double screenY = (1 - normalizedSpaceY) * screenH;

        final int x = clamp(screenX, 0, screenW);
        final int y = clamp(screenY, 0, screenH);

        return new Point(x, y);
    }


    @Override
    public Point2D.Double getDataCoordinates(final int screenX, final int screenY) {
        // Invert the screenX calculation to flip the image on the y-axis
        final double normalizedScreenX = ((double) screenWidth - (double) screenX) / (double) screenWidth;
        final double normalizedScreenY = 1 - ((double) screenY / (double) screenHeight);

        final double spaceX = normalizedScreenX * spaceWidth + spaceMinX;
        final double spaceY = normalizedScreenY * spaceHeight + spaceMinY;

        // shift the spaceX value back by 180 degrees
        double dataL = spaceX - 180;
        if (dataL < 0) {
            dataL += 360;
        }
        final double dataB = spaceY;

        return new Point2D.Double(dataL, dataB);
    }
}
