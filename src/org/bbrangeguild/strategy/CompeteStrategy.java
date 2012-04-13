package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Npcs;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.Npc;

/**
 * @author BOOM BOOM
 */
public class CompeteStrategy extends Strategy implements Condition, Task {

    private BBRangeGuild script;

    public CompeteStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        return Settings.get(156) == 0 || Widgets.get(1184, 0).isVisible() || Widgets.get(1188, 3).isVisible();
    }

    @Override
    public void run() {
        if (!Widgets.get(1188, 3).isVisible()) {
            Npc judge = Npcs.getNearest(693);
            if (judge != null) {
                if (judge.isOnScreen()) {
                    if (judge.interact("Compete")) {
                        for (int i = 0; i < 20 && !Widgets.get(1188, 3).isVisible(); i++)
                            Time.sleep(100);
                    }
                } else
                    Camera.turnTo(judge);
            }
        }

        if (Widgets.get(1188, 3).isVisible()) {
            script.setCentralPoint(null);
            if (Widgets.get(1188, 3).click(true)) {
                for (int i = 0; i < 25 && Widgets.get(1188, 3).isVisible(); i++)
                    Time.sleep(100);
            }
        }
    }

}
