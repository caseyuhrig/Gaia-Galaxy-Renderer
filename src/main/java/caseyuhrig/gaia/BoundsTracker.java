package caseyuhrig.gaia;

import java.awt.*;

public class BoundsTracker {

    private final Rectangle bounds;

    public BoundsTracker() {
        super();
        bounds = new Rectangle(0, 0, 0, 0);
    }

    public BoundsTracker(final int x, final int y, final int width, final int height) {
        this();
        bounds.setBounds(x, y, width, height);
    }

    public void updateBounds(final int x, final int y) {
        if (x < bounds.x) {
            bounds.width += bounds.x - x;
            bounds.x = x;
        } else if (x > bounds.x + bounds.width) {
            bounds.width = x - bounds.x;
        }
        if (y < bounds.y) {
            bounds.height += bounds.y - y;
            bounds.y = y;
        } else if (y > bounds.y + bounds.height) {
            bounds.height = y - bounds.y;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }


    public void resetBounds() {
        bounds.setBounds(0, 0, 0, 0);
    }
}
