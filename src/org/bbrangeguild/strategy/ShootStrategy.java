package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Location;

import java.awt.*;

/**
 * @author BOOM BOOM
 */
public class ShootStrategy implements Condition, Task {

    private BBRangeGuild script;
    private int fails;

    public ShootStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void run() {
        Location target = Locations.getNearest(1308);
        if (target != null) {
            if (target.isOnScreen()) {
                if (interact(target, "Fire-at", Widgets.get(325, 40).isVisible()))
                    fails = 0;

                if (fails > 5) {
                    if (Widgets.get(325, 40).isVisible()) {
                        if (Widgets.get(325, 40).click(true)) {
                            for (int i = 0; i < 20 && Widgets.get(325, 40).isVisible(); i++)
                                Time.sleep(100);
                        }
                    }
                } else if (Widgets.get(325, 40).isVisible())
                    fails++;
            } else
                Camera.turnTo(target);
        }
    }

    private boolean interact(final Location location, final String action, final boolean open) {
        if (script.getCentralPoint() == null || !isClose(Mouse.getLocation())) {
            final Point center = location.getCentralPoint();
            script.setCentralPoint(center);
            Mouse.move(center.x, center.y, 4, 4);
        }

        if (open) {
            Mouse.click(true);
            return false;
        }
        return Menu.contains(action) && Menu.select(action);
    }

    private boolean isClose(final Point point) {
        return point.distance(script.getCentralPoint()) < 5;
    }

}
