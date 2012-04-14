package org.bbrangeguild.strategy;

import org.bbrangeguild.BBRangeGuild;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;

/**
 * @author BOOM BOOM
 */
public class EquipStrategy implements Condition, Task {

    private BBRangeGuild script;

    public EquipStrategy(final BBRangeGuild script) {
        this.script = script;
    }

    @Override
    public boolean validate() {
        String text;
        return Widgets.get(1184, 13).isVisible() && (text = Widgets.get(1184, 13).getText()) != null && text.contains("bronze arrows");
    }

    @Override
    public void run() {
        if (Inventory.getCount(882) > 0) {
            script.setStatus("Equiping Arrows...");
            for (final Item item : Inventory.getItems()) {
                if (item.getId() == 882 && item.getWidgetChild().click(true)) {
                    for (int i = 0; i < 15 && Inventory.getCount(882) > 0; i++)
                        Time.sleep(100);
                    return;
                }
            }
        } else {
            script.log.info("You do not have any more arrows.");
            script.stop();
        }
    }

}
