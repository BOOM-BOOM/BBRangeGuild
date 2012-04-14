package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Npcs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.Npc;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

/**
 * @author BOOM BOOM
 */
public class ExchangeStrategy implements Condition, Task {

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
        if (Widgets.get(278, 0).isVisible() &&  Widgets.get(512, 0).isVisible()) {
            for (final WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
                if (widgetChild.getChildId() == 1464 && widgetChild.getChildStackSize() < 1020) {
                    script.log.info("You do not have enough tickets to exchange.");
                    script.stop();
                    break;
                }
            }
            if (Widgets.get(278, 16).getChildren()[2].interact("Buy"))
                Time.sleep(Random.nextInt(100, 300));
        } else {
            final Npc merchant = Npcs.getNearest(694);
            if (merchant != null) {
                if (merchant.isOnScreen()) {
                    if (merchant.interact("Trade")) {
                        for (int i = 0; i < 25 && !Widgets.get(278, 0).isVisible() && !Widgets.get(512, 0).isVisible(); i++) {
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
