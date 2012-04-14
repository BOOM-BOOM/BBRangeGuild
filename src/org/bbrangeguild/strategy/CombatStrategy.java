package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Location;

/**
 * @author BOOM BOOM
 */
public class CombatStrategy extends Strategy implements Condition, Task {

    private BBRangeGuild script;
    private final Tile spot = new Tile(2670, 3418, 0);

    public CombatStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        return Players.getLocal().isInCombat() || script.isCombatInitialized();
    }

    @Override
    public void run() {
        if (!script.isCombatInitialized())
            script.setCombatInitialized(true);

        if (Game.getPlane() == 0) {
            if (Widgets.get(564, 0).isVisible()) {
                if (Widgets.get(564, 15).click(true)) {
                    for (int i = 0; i < 25 && Widgets.get(564, 0).isVisible(); i++)
                        Time.sleep(100);
                }
            } else {
                final Location ladder = Locations.getNearest(2511);
                if (ladder != null) {
                    if (Calculations.distance(Players.getLocal().getPosition(), ladder.getPosition()) < 8 && ladder.isOnScreen()) {
                        if (ladder.interact("Climb-up")) {
                            for (int i = 0; i < 25 && Game.getPlane() == 0 && !Widgets.get(564, 0).isVisible(); i++)
                                Time.sleep(100);
                        }
                    } else if (Calculations.distance(Players.getLocal().getPosition(), ladder.getPosition()) < 8 && !ladder.isOnScreen())
                        Camera.turnTo(ladder);
                    else {
                        if (Walking.walk(ladder.getPosition())) {
                            for (int i = 0; i < 25 && Calculations.distance(Players.getLocal().getPosition(), ladder.getPosition()) > 1; i++) {
                                if (Players.getLocal().isMoving())
                                    i = 0;
                                Time.sleep(100);
                            }
                        }
                    }
                }
            }
        } else {
            final Location ladder = Locations.getNearest(2512);
            if (ladder != null) {
                if (ladder.isOnScreen()) {
                    if (ladder.interact("Climb-down")) {
                        for (int i = 0; i < 25 && Game.getPlane() != 0; i++)
                            Time.sleep(100);

                        if (Game.getPlane() == 0) {
                            script.setCombatInitialized(false);
                            if (Walking.walk(spot)) {
                                for (int i = 0; i < 25 && Calculations.distance(Players.getLocal().getPosition(), spot) > 1; i++) {
                                    if (Players.getLocal().isMoving())
                                        i = 0;
                                    Time.sleep(100);
                                }
                            }
                        }
                    }
                } else
                    Camera.turnTo(ladder);
            }
        }
    }

}
