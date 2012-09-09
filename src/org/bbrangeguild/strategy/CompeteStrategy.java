package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.NPC;

/**
 * @author BOOM BOOM
 */
public class CompeteStrategy extends Strategy implements Condition, Runnable {

    private BBRangeGuild script;

    public CompeteStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        return Settings.get(156) == 0 || Widgets.get(1184, 0).visible() || Widgets.get(1188, 3).visible();
    }

    @Override
    public void run() {
        String money;
        if (Inventory.getCount(true, 995) > 200 || Widgets.get(548, 201).visible() && (money = Widgets.get(548, 201).getText()) != null && script.parseMultiplier(money) > 200) {
            if (!Widgets.get(1188, 3).visible()) {
                script.setStatus("Talking To Judge...");
                final NPC judge = NPCs.getNearest(693);
                if (judge != null) {
                    if (judge.isOnScreen()) {
                        if (judge.interact("Compete", "Competition Judge")) {
                            for (int i = 0; i < 20 && !Widgets.get(1188, 3).visible(); i++)
                                Time.sleep(100);
                        }
                    } else
                        Camera.turnTo(judge);
                }
            }

            if (Widgets.get(1188, 3).visible()) {
                script.setStatus("Paying Judge...");
                script.setCentralPoint(null);
                if (Widgets.get(1188, 3).click(true)) {
                    for (int i = 0; i < 25 && Widgets.get(1188, 3).visible(); i++)
                        Time.sleep(100);
                }
            }
        } else if (!Widgets.get(548, 201).visible()) {
            Mouse.click(532, 146, true);
            for (int i = 0; i < 20 && !Widgets.get(548, 201).visible(); i++)
                Time.sleep(100);
        } else {
            script.log.info("You do not have any coins with you.");
            script.stop();
        }
    }

}
