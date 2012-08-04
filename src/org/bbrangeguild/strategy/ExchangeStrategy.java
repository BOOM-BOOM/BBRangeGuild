package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

/**
 * @author BOOM BOOM
 */
public class ExchangeStrategy extends Strategy implements Condition, Task {

    private int child;
    private BBRangeGuild script;

    public ExchangeStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void run() {
        if (Widgets.get(278, 0).visible() &&  Widgets.get(512, 0).visible()) {
            script.setStatus("Exchanging...");
            for (final WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
                if (widgetChild.getChildId() == 1464 && script.getAmount() > 0 ? widgetChild.getChildStackSize() >= script.getAmount() : widgetChild.getChildStackSize() < 1020) {
                    script.log.info("You do not have enough tickets to exchange.");
                    script.stop();
                    return;
                }
            }

            if (script.getExchangeMode() == 0)
                child = 0;
            else if (script.getExchangeMode() == 1)
                child = 2;
            else if (script.getExchangeMode() == 2)
                child = 5;

            if (Widgets.get(278, 16).getChild(child).interact("Buy"))
                Time.sleep(Random.nextInt(100, 300));
        } else {
            script.setStatus("Trading...");
            final NPC merchant = NPCs.getNearest(694);
            if (merchant != null) {
                if (merchant.isOnScreen()) {
                    if (merchant.interact("Trade")) {
                        for (int i = 0; i < 25 && !Widgets.get(278, 0).visible() && !Widgets.get(512, 0).visible(); i++) {
                            if (Players.getLocal().isMoving())
                                i = 0;
                            Time.sleep(100);
                        }
                    }
                } else
                    Camera.turnTo(merchant);
            }
        }
    }

}
