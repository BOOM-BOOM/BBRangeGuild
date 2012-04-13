package org.bbrangeguild.util;

import java.awt.*;

/**
 * @author Enfilade
 */
public class MousePathPoint extends Point {

    private long finishTime;
    private double lastingTime;

    private int toColor(double d) {
        return Math.min(255, Math.max(0, (int) d));
    }

    public MousePathPoint(int x, int y, int lastingTime) {
        super(x, y);
        this.lastingTime = lastingTime;
        finishTime = System.currentTimeMillis() + lastingTime;
    }

    public boolean isUp() {
        return System.currentTimeMillis() > finishTime;
    }

    public Color getColor() {
        return new Color(0, 0, 0, toColor(256 * ((finishTime - System.currentTimeMillis()) / lastingTime)));
    }

}
