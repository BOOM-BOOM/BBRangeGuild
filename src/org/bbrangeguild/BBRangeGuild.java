package org.bbrangeguild;

import org.bbrangeguild.strategy.*;
import org.bbrangeguild.ui.BBRangeGuildGUI;
import org.bbrangeguild.util.GeItem;
import org.bbrangeguild.util.MousePathPoint;
import org.bbrangeguild.util.SkillData;
import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyGroup;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.event.MessageEvent;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

@Manifest(name = "BBRangeGuild",
        authors = "BOOM BOOM",
        version = 1.0D,
        description = "TODO")
public class BBRangeGuild extends ActiveScript implements PaintListener, MessageListener, MouseListener {

    private int startTickets, targetMessage, gamesCompleted, absoluteY, price, exchangeMode, amount;
    private long startTime;
    private boolean mainHidden, barHidden, combatInitialized, spamClick, equipArrows, antiban, mouseAnti, skillAnti, afkMode;
    private String status = "Loading...";
    private Strategy setupStrategy, handler;
    private BufferedImage labelPic;
    private Point centralPoint;
    private SkillData skillData;
    private BBRangeGuildGUI gui;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
    private static final RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private static final Font ARIAL_12_BOLD = new Font("Arial", Font.BOLD, 12), ARIAL_12 = new Font("Arial", Font.PLAIN, 12);
    private static final Color BACKGROUND = new Color(194, 178, 146), GREEN = new Color(32, 95, 0);
    private static final LinkedList<MousePathPoint> mousePath = new LinkedList<MousePathPoint>();

    private String formatCommas(final int i) {
        return NumberFormat.getIntegerInstance().format(i);
    }

    private BufferedImage getImage(final String name, final String URL, final String format) {
        try {
            final File file = new File("./BBRangeGuild/resources/", name);

            if (!file.exists()) {
                final BufferedImage image = ImageIO.read(new java.net.URL(URL));
                ImageIO.write(image, format, file);
                return image;
            } else
                return ImageIO.read(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int parseMultiplier(String value) {
        value = value.toLowerCase();
        if (value.contains("b") || value.contains("m") || value.contains("k")) {
            return (int) (Double.parseDouble(value.substring(0, value.length() - 1))
                    * (value.endsWith("b") ? 1000000000D : value.endsWith("m") ? 1000000
                    : value.endsWith("k") ? 1000 : 1));
        } else {
            return Integer.parseInt(value);
        }
    }

    public Point getCentralPoint() {
       return centralPoint;
    }

    public boolean isCombatInitialized() {
        return combatInitialized;
    }

    public boolean isSpamClicking() {
        return spamClick;
    }

    public boolean isEquipingArrows() {
        return equipArrows;
    }

    public boolean isAntiban() {
        return antiban;
    }

    public boolean isMouseAntiban() {
        return mouseAnti;
    }

    public boolean isSkillAntiban() {
        return skillAnti;
    }

    public boolean isAfkMode() {
        return afkMode;
    }

    public int getExchangeMode() {
        return exchangeMode;
    }

    public int getAmount() {
        return amount;
    }

    public void setCentralPoint(final Point centralPoint) {
        this.centralPoint = centralPoint;
    }

    public void setCombatInitialized(final boolean combatInitialized) {
        this.combatInitialized = combatInitialized;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setSpamClicking(final boolean spamClick) {
        this.spamClick = spamClick;
    }

    public void setEquipingArrows(final boolean equipArrows) {
        this.equipArrows = equipArrows;
    }

    public void setAntiban(final boolean antiban) {
        this.antiban = antiban;
    }

    public void setMouseAntiban(final boolean mouseAnti) {
        this.mouseAnti = mouseAnti;
    }

    public void setSkillAntiban(final boolean skillAnti) {
        this.skillAnti = skillAnti;
    }

    public void setAfkMode(final boolean afkMode) {
        this.afkMode = afkMode;
    }

    public void setExchangeMode(final int exchangeMode) {
        this.exchangeMode = exchangeMode;
    }

    public void setAmount (final int amount) {
        this.amount = amount;
    }

    @Override
    protected void setup() {
        final Strategy camera = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                final int yaw = Camera.getYaw();
                return Game.isLoggedIn() && Camera.getPitch() > 8 || (yaw < 320 && yaw > 303);
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

        final CombatStrategy combatStrategy = new CombatStrategy(this);
        final EquipStrategy equipStrategy = new EquipStrategy(this);
        final CompeteStrategy competeStrategy = new CompeteStrategy(this);
        final ShootStrategy shootStrategy = new ShootStrategy(this);
        final ExchangeStrategy exchangeStrategy = new ExchangeStrategy(this);
        final StrategyGroup groupCompete = new StrategyGroup(new Strategy[] { combatStrategy, new Strategy(equipStrategy, equipStrategy), camera, combatStrategy,
                new Strategy(shootStrategy, shootStrategy) });

        setupStrategy = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                return true;
            }
        }, new Task() {
            @Override
            public void run() {
                if (Game.isLoggedIn() && Players.getLocal() != null && Players.getLocal().isOnScreen() && !Widgets.get(1252, 1).visible() && !Widgets.get(1234, 10).visible()) {
                    String money;
                    if (Inventory.getCount(true, 995) > 200 || Widgets.get(548, 204).visible() && (money = Widgets.get(548, 204).getText()) != null && parseMultiplier(money) > 200) {
                        labelPic = getImage("bbrangeguild.jpeg", "http://i53.tinypic.com/2jalnrc.jpeg", ".jpeg");

                        if (Inventory.getCount(1464) > 0)
                            startTickets = Inventory.getCount(true, 1464);

                        if (Widgets.get(325, 40).visible()) {
                            if (Widgets.get(325, 40).click(true)) {
                                for (int i = 0; i < 20 && Widgets.get(325, 40).visible(); i++)
                                    Time.sleep(100);
                            }
                        }

                        gui = new BBRangeGuildGUI();
                        gui.setVisible(true);

                        while (gui.isRunning() && gui.isVisible())
                            Time.sleep(100);
                        if (gui.isRunning()) {
                            gui.dispose();
                            stop();
                        }

                        if (gui.isCompeting()) {
                            setSpamClicking(gui.isSpamClicking());
                            setEquipingArrows(gui.isEquipingArrows());
                            setAntiban(gui.isAntiban());
                            setMouseAntiban(gui.isMouseAntiban());
                            setSkillAntiban(gui.isSkillAntiban());
                            setAfkMode(gui.isAfkMode());
                        } else {
                            setExchangeMode(gui.getExchangeMode());
                            setAmount(gui.getAmount());
                        }

                        price = GeItem.lookup(892).getPrice();
                        skillData = new SkillData(Skills.RANGE, (startTime = System.currentTimeMillis()));
                        revoke(setupStrategy);
                    } else {
                        log.info("You do not have any coins with you.");
                        stop();
                    }
                }
            }
        });

        handler = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                return true;
            }
        }, new Task() {
            @Override
            public void run() {
                if (!gui.isRunning()) {
                    if (gui.isCompeting())
                        revoke(exchangeStrategy);
                    else
                        revoke(groupCompete);
                    revoke(handler);
                }
            }
        });

        setupStrategy.setReset(true);
        handler.setReset(true);
        combatStrategy.setReset(true);
        competeStrategy.setReset(true);
        provide(setupStrategy);
        provide(handler);
        provide(groupCompete);
        provide(exchangeStrategy);
    }

    @Override
    public void onStop() {
        if (startTime != 0) {
            //Environment.saveScreenCapture();
            final int gainedXP = skillData.getGainedXP();
            final int gainedTickets = Inventory.getCount(true, 1464) - startTickets;
            final int eph = (int) (gainedXP * 3600000D / (System.currentTimeMillis() - startTime));
            final int tph = (int) (gainedTickets * 3600000D / (System.currentTimeMillis() - startTime));
            final double profit = (gainedTickets != 0 ? gainedTickets / 20.4 * price : 0);
            final int pph = (int) (profit * 3600000D / (System.currentTimeMillis() - startTime));
            final int gph = (int) (gamesCompleted * 3600000D / (System.currentTimeMillis() - startTime));
            log.info("Total levels gained: " + skillData.getGainedLevels() + ".");
            log.info("Total XP gained: " + formatCommas(gainedXP) + " (" + formatCommas(eph) + "/H).");
            log.info("Total profit gained: " + formatCommas((int) profit - (gamesCompleted * 200)) + " (" + formatCommas(pph - (gph * 200)) + "/H).");
            log.info("Total tickets gained: " + formatCommas(gainedTickets) + " (" + formatCommas(tph) + "/H).");
            log.info("Total games played: " + formatCommas(gamesCompleted) + " (" + formatCommas(gph) + "/H).");
        }
        log.info((startTime != 0 ? "Total run time: " + Time.format(skillData.getElapsedTime()) : "Script wasn't loaded") + ".");
    }

    private void drawMouse(final Graphics g) {
        final Point location = Mouse.getLocation();
        while (!mousePath.isEmpty() && mousePath.peek().isUp())
            mousePath.remove();
        Point clientCursor = Mouse.getLocation();
        MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, 1500);
        if (mousePath.isEmpty() || !mousePath.getLast().equals(mpp))
            mousePath.add(mpp);
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : mousePath) {
            if (lastPoint != null) {
                g.setColor(a.getColor());
                g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
            }
            lastPoint = a;
        }

        if (System.currentTimeMillis() - Mouse.getPressTime() < 500)
            g.setColor(new Color(168, 9, 9));
        else
            g.setColor(GREEN);
        g.fillOval(location.x - 5, location.y - 5, 10, 10);
        g.setColor(Color.BLACK);
        g.drawLine(location.x - 7, location.y, location.x + 7, location.y);
        g.drawLine(location.x, location.y - 7, location.x, location.y + 7);
        g.drawOval(location.x - 5, location.y - 5, 10, 10);
    }

    @Override
    public void onRepaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHints(RENDERING_HINTS);
        final WidgetChild chatbox = Widgets.get(137, 0);
        if (skillData != null && Game.isLoggedIn() && chatbox.getAbsoluteX() > 0 && chatbox.getAbsoluteY() > 0) {
            absoluteY = chatbox.getAbsoluteY() - 1;

            if (!mainHidden) {
                g.setColor(BACKGROUND);
                g.fillRect(6, absoluteY, 506, chatbox.getHeight());

                g.setFont(ARIAL_12_BOLD);
                g.setColor(Color.BLACK);
                final long runTime = skillData.getElapsedTime();
                g.drawString("Time Running: " + (startTime != 0 ? Time.format(runTime) : "Waiting..."), 12, absoluteY + 17);
                g.drawString("Current State: " + status, 158, absoluteY + 17);

                g.setColor(GREEN);
                g.drawString("GUI Settings:", 12, absoluteY + 36);
                g.drawString("Gained Profit:", 12, absoluteY + 51);
                g.drawString("Gained XP:", 12, absoluteY + 66);
                g.drawString("Gained Ticks:", 12, absoluteY + 81);
                g.drawString("Games Done:", 12, absoluteY + 96);

                g.setFont(ARIAL_12);
                g.setColor(Color.BLACK);
                final int gainedXP = skillData.getGainedXP();
                final int gainedTickets = Inventory.getCount(true, 1464) - startTickets;
                final double profit = gainedTickets / 20.4 * price;
                g.drawString("" + formatCommas((int) profit - (gamesCompleted * 200)), 100, absoluteY + 51);
                g.drawString("" + formatCommas(gainedXP), 100, absoluteY + 66);
                g.drawString("" + formatCommas(gainedTickets), 100, absoluteY + 81);
                g.drawString("" + formatCommas(gamesCompleted), 100, absoluteY + 96);

                final int eph = (int) (gainedXP * 3600000D / runTime);
                final int tph = (int) (gainedTickets * 3600000D / runTime);
                final int pph = (int) (profit * 3600000D / runTime);
                final int gph = (int) (gamesCompleted * 3600000D / runTime);
                g.drawString("(" + formatCommas(pph - (gph * 200)) + "/H)", 158, absoluteY + 51);
                g.drawString("(" + formatCommas(eph) + "/H)", 158, absoluteY + 66);
                g.drawString("(" + formatCommas(tph) + "/H)", 158, absoluteY + 81);
                g.drawString("(" + formatCommas(gph) + "/H)", 158, absoluteY + 96);

                g.drawImage(labelPic, 6, absoluteY, null);
            }

            if (!barHidden) {
                g.setColor(BACKGROUND);
                g.fillRect(2, absoluteY - 28, 515, 22);
                g.setColor(new Color(108, 107, 107));
                g.drawRect(2, absoluteY - 28, 515, 22);
                g.setColor(new Color(184, 170, 142));
                g.drawRect(3, absoluteY - 27, 513, 20);
                g.setColor(new Color(255, 219, 130));
                g.fillRect(5, absoluteY - 25, 510, 16);

                final int percent = Skills.getRealLevel(Skills.RANGE) == 99 ? 100 : skillData.getPercentToNextLevel();
                final int eph = (int) (skillData.getGainedXP() * 3600000D / (System.currentTimeMillis() - startTime));
                final long ttl = (long) ((skillData.getExperienceToLevel() * 3600000D) / eph);
                String levelInfo = percent + "% To Level "
                        + (Skills.getRealLevel(Skills.RANGE) == 99 ? 99 : Skills.getRealLevel(Skills.RANGE) + 1) + " (" + skillData.getGainedLevels()
                        + " Gained)" + " - " + FORMAT.format(skillData.getExperienceToLevel() / 1000D) + "K XP - "
                        + (eph > 0 && startTime != 0 ? Time.format(ttl) : "Calculating...") + " TTL";

                g.setColor(new Color(0, 127, 14, 200));
                g.fillRect(5, absoluteY - 25, (int) (5.1 * percent), 16);
                g.setColor(Color.BLACK);
                g.drawRect(5, absoluteY - 25, (int) (5.1 * percent), 16);
                g.setFont(ARIAL_12_BOLD);
                g.setColor(new Color(33, 28, 28));
                g.drawString(levelInfo, absoluteY - 99 - ((int) g.getFontMetrics().getStringBounds(levelInfo, g).getWidth() / 2), absoluteY - 12);
            }
            drawMouse(g);
        }
    }

    @Override
    public void messageReceived(MessageEvent messageEvent) {
        switch (messageEvent.getId()) {
            case 109:
                if (messageEvent.getMessage().equalsIgnoreCase("You carefully aim at the target...")) {
                    if (targetMessage < 9)
                        targetMessage++;
                    else {
                        targetMessage = 0;
                        gamesCompleted++;
                    }
                } else if (messageEvent.getMessage().equalsIgnoreCase("200 coins have been removed from your money pouch.") && targetMessage > 0) {
                    targetMessage = 0;
                    gamesCompleted++;
                }
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                if (new Rectangle(7, absoluteY, 505, 130).contains(e.getPoint()))
                    mainHidden = !mainHidden;
                else if (new Rectangle(2, absoluteY - 28, 515, 22).contains(e.getPoint()))
                    barHidden = !barHidden;
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
