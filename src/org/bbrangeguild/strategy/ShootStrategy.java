package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;

import java.awt.*;

/**
 * @author BOOM BOOM
 */
public class ShootStrategy implements Condition, Task {

    private BBRangeGuild script;
    private int fails;
    private static final Tile targetTile = new Tile(2679, 3426, 0);
    private static final Tile spot = new Tile(2670, 3418, 0);

    public ShootStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        return !script.isCombatInitialized();
    }

    @Override
    public void run() {
        script.setStatus("Shooting...");
        if (Calculations.distance(Players.getLocal().getLocation(), spot) > 4) {
            if (Walking.walk(spot)) {
                for (int i = 0; i < 25 && Calculations.distance(Players.getLocal().getLocation(), spot) > 1; i++) {
                    if (Players.getLocal().isMoving())
                        i = 0;
                    Time.sleep(100);
                }
            }
        }

        final SceneObject target = getAt(targetTile);
        if (target != null) {
            if (target.isOnScreen()) {
                if (Players.getLocal().isInCombat()) {
                    script.setCombatInitialized(true);
                    return;
                }

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

    private boolean interact(final SceneObject location, final String action, final boolean open) {
        final Point center = location.getCentralPoint();
        if (script.getCentralPoint() == null || distanceBetween(Mouse.getLocation(), center) > 4) {
            script.setCentralPoint(center);
            Mouse.move(center.x, center.y, 4, 4);
        }

        if (open) {
            Mouse.click(true);
            return false;
        }
        return Menu.contains(action) && Menu.select(action);
    }

    private double distanceBetween(Point current, Point destination) {
        return Math.sqrt(((current.x - destination.x) * (current.x - destination.x)) + ((current.y - destination.y) * (current.y - destination.y)));
    }

    private SceneObject getAt(final Tile tile) {
        SceneObject[] locations = SceneEntities.getLoaded(tile);
        return locations.length > 0 ? locations[0] : null;
    }

}
