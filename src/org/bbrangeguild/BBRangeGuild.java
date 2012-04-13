package org.bbrangeguild;

import org.bbrangeguild.strategy.CompeteStrategy;
import org.bbrangeguild.strategy.EquipStrategy;
import org.bbrangeguild.strategy.ShootStrategy;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;

@Manifest(name = "BBRangeGuild", authors = "BOOM BOOM", version = 1.0D, description = "TODO", premium = true)
public class BBRangeGuild extends ActiveScript {

    @Override
    protected void setup() {
        final Strategy camera = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                final int yaw = Camera.getYaw();
                return Camera.getPitch() > 8 || (yaw < 320 && yaw > 303);
            }
        }, new Task() {
            @Override
            public void run() {
                if (Camera.getPitch() > 8)
                    Camera.setPitch(Random.nextInt(0, 9));
                final int yaw = Camera.getYaw();
                if (yaw < 320 && yaw > 303)
                    Camera.setAngle(Random.nextBoolean() ? Random.nextInt(320, 335) : Random.nextInt(285, 300));
            }
        });
        final EquipStrategy equipStrategy = new EquipStrategy(this);
        final CompeteStrategy competeStrategy = new CompeteStrategy();
        final ShootStrategy shootStrategy = new ShootStrategy();
        camera.setLock(false);
        competeStrategy.setReset(true);
        provide(new Strategy(equipStrategy, equipStrategy));
        provide(camera);
        provide(competeStrategy);
        provide(new Strategy(shootStrategy, shootStrategy));
    }

}
