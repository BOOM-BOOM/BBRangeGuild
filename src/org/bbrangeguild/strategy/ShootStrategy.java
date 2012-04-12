package org.bbrangeguild.strategy;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Location;

import java.awt.*;

/**
 * @author BOOM BOOM
 */
public class ShootStrategy implements Condition, Task {

    private int fails;

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void run() {
        Location target = Locations.getNearest(1308);
        if (target != null) {
            if (target.isOnScreen()) {
                if (!Widgets.get(325, 40).isVisible()) {
                    if (interact(target, "Fire-at")) {
                        fails = 0;
                        Time.sleep(Random.nextInt(50, 80));
                    } else if (fails > 20) {
                        if (Widgets.get(325, 40).isOnScreen()) {
                            if (Widgets.get(325, 40).click(true)) {
                                for (int i = 0; i < 20 && Widgets.get(325, 40).isOnScreen(); i++)
                                    Time.sleep(100);
                            }
                        }
                    } else
                        fails++;
                }
            } else
                Camera.turnTo(target);
        }
    }

    private boolean interact(final Location location, final String action) {
        if (!Menu.contains(action)) {
            final Point center = location.getCentralPoint();
            Mouse.move(center.x, center.y);
        }
        if (Menu.contains(action)) {
            if (!Menu.isOpen())
                Mouse.click(false);
            return Menu.select(action);
        }
        return false;
    }

}
