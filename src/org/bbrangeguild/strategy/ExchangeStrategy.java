package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

/**
 * @author BOOM BOOM
 */
public class ExchangeStrategy extends Strategy implements Condition, Task {

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
            for (WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
                if (widgetChild.getChildId() == 1464 && (script.getAmount() > 0 ? maxAmount() : widgetChild.getChildStackSize() < script.getExchangeItem().getValue())) {
                    script.log.info("You do not have enough tickets to exchange. You have: " + widgetChild.getChildStackSize());
                    script.stop();
                    return;
                }
            }

            int count = getCount();
            if (Widgets.get(278, 16).getChild(script.getExchangeItem().getChildId()).interact("Buy")) {
                for (int i = 0; i < 20 && count == getCount(); i++)
                    Time.sleep(100);
            }
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

    private boolean maxAmount() {
        for (WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
            if (widgetChild.getChildId() == script.getExchangeItem().getItemId())
                return widgetChild.getChildStackSize() >= script.getAmount();
        }
        return false;
    }

    private int getCount() {
        int count = 0;
        for (WidgetChild widgetChild : Widgets.get(512, 0).getChildren())
            count += widgetChild.getChildStackSize();
        return count;
    }

}
