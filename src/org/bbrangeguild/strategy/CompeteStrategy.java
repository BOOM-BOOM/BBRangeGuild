package org.bbrangeguild.strategy;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Npcs;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.Npc;

/**
 * @author BOOM BOOM
 */
public class CompeteStrategy implements Condition, Task {

    @Override
    public boolean validate() {
        return Settings.get(156) == 0 || Widgets.get(242, 6).isOnScreen() || Widgets.get(243, 1).isOnScreen();
    }

    @Override
    public void run() {
        if (Widgets.get(236, 1).isVisible()) {
            if (Widgets.get(236, 1).click(true)) {
                for (int i = 0; i < 25 && Widgets.get(236, 1).isVisible(); i++)
                    Time.sleep(100);
            }
        } else {
            Npc judge = Npcs.getNearest(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return npc.getName() != null && npc.getName().equalsIgnoreCase("Competition Judge");
                }
            });
            if (judge != null) {
                if (judge.isOnScreen()) {
                    if (judge.interact("Compete", judge.getName())) {
                        for (int i = 0; i < 20 && !Widgets.get(236, 1).isVisible(); i++)
                            Time.sleep(100);
                    }
                } else
                    Camera.turnTo(judge);
            }
        }
    }

}
