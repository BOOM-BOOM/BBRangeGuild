package org.bbrangeguild;

import org.bbrangeguild.strategy.CompeteStrategy;
import org.bbrangeguild.strategy.EquipStrategy;
import org.bbrangeguild.strategy.ShootStrategy;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;

@Manifest(name = "BBRangeGuild", authors = "BOOM BOOM", version = 1.0D, description = "TODO")
public class BBRangeGuild extends ActiveScript {

    @Override
    protected void setup() {
        final EquipStrategy equipStrategy = new EquipStrategy(this);
        final CompeteStrategy competeStrategy = new CompeteStrategy();
        final ShootStrategy shootStrategy = new ShootStrategy();
        provide(new Strategy(equipStrategy));
        provide(new Strategy(competeStrategy));
        provide(new Strategy(shootStrategy));
    }

}
