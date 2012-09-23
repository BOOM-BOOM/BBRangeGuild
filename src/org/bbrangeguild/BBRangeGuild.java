package org.bbrangeguild;

import org.bbrangeguild.strategy.*;
import org.bbrangeguild.ui.BBRangeGuildGUI;
import org.bbrangeguild.util.ExchangeItem;
import org.bbrangeguild.util.MousePathPoint;
import org.bbrangeguild.util.SkillData;
import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyGroup;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.util.net.GeItem;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.event.listener.MessageListener;
import org.powerbot.game.bot.event.listener.PaintListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

@Manifest(name = "BBRangeGuild",
        authors = "BOOM BOOM",
        version = 1.03D,
        description = "The ultimate Range Guild script! Over a year in experience!",
        website = "https://www.powerbot.org/community/topic/679291-bbrangeguild-over-a-year-in-range-guilding-experience/",
        topic = 679291)
public class BBRangeGuild extends ActiveScript implements PaintListener, MessageListener, MouseListener {

    private int startItems, targetMessage, gamesCompleted, absoluteY, price, exchangeMode, amount, ticketCount;
    private long startTime;
    private boolean mainHidden, barHidden, combatInitialized, spamClick, equipArrows, antiban, mouseAnti, skillAnti, afkMode, checkingSkills;
    private String status = "Loading...";
    private Strategy setupStrategy;
    private BufferedImage labelPic;
    private Point centralPoint;
    private SkillData skillData;
    private BBRangeGuildGUI gui;
    private ExchangeItem exchangeItem;
    private Timer antibanTimer = new Timer(Random.nextInt(300000, 900000));
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
    private static final RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private static final Font ARIAL_12_BOLD = new Font("Arial", Font.BOLD, 12), ARIAL_12 = new Font("Arial", Font.PLAIN, 12);
    private static final Color BACKGROUND = new Color(194, 178, 146), GREEN = new Color(32, 95, 0);
    private static final LinkedList<MousePathPoint> MOUSE_PATH_POINTS = new LinkedList<MousePathPoint>();

    private String formatCommas(final int i) {
        return NumberFormat.getIntegerInstance().format(i);
    }

    private BufferedImage getImage(final String name, final String URL, final String format) {
        final File file = new File(Environment.getStorageDirectory().getAbsolutePath(), name);
        try {
            if (!file.exists()) {
                final BufferedImage image = ImageIO.read(new java.net.URL(URL));
                ImageIO.write(image, format, file);
                return image;
            } else
                return ImageIO.read(file);
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

    public boolean isEquippingArrows() {
        return equipArrows;
    }

    public int getAmount() {
        return amount;
    }

    public ExchangeItem getExchangeItem() {
        return exchangeItem;
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

    private int getCount(final int id) {
        if (Widgets.get(512, 0).visible()) {
            for (WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
                if (widgetChild.getChildId() == id)
                    return widgetChild.getChildStackSize();
            }
        }
        return 0;
    }

    @Override
    protected void setup() {
        final Strategy antibanStrategy = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                return !antibanTimer.isRunning();
            }
        }, new Runnable() {
            @Override
            public void run() {
                antibanTimer.setEndIn(Random.nextInt(300000, 900000));

                if (mouseAnti && Random.nextInt(0, 100) <= 25) {
                    Timer moveTimer = new Timer(Random.nextInt(500, 1500));
                    while (moveTimer.isRunning())
                        Mouse.move((int) Mouse.getLocation().getX() + Random.nextInt(-8, 8), (int) Mouse.getLocation().getY() + Random.nextInt(-8, 8));
                }

                if (skillAnti && Random.nextInt(0, 100) <= 25) {
                    checkingSkills = true;
                    if (!Tabs.getCurrent().equals(Tabs.STATS)) {
                        Tabs.STATS.open();
                        for (int i = 0; i < 20 && !Tabs.getCurrent().equals(Tabs.STATS); i++)
                            Time.sleep(100);
                    }

                    if (Tabs.getCurrent().equals(Tabs.STATS)) {
                        Widgets.get(320, 36).hover();
                        Time.sleep(Random.nextInt(1400, 2100));
                    }
                    checkingSkills = false;
                }

                if (afkMode && Random.nextInt(0, 100) <= 50) {
                    Timer afkTimer = new Timer(Random.nextInt(10000, 30000));
                    while (afkTimer.isRunning())
                        Time.sleep(100);
                }
            }
        });

        final Strategy cameraStrategy = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                final int yaw = Camera.getYaw();
                return Game.isLoggedIn() && Camera.getPitch() > 8 || (yaw < 320 && yaw > 303) || (yaw >= 0 && yaw  <= 2) || (yaw <= 360 && yaw >= 358);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (Camera.getPitch() > 8)
                    Camera.setPitch(Random.nextInt(0, 9));
                final int yaw = Camera.getYaw();
                if ((yaw < 320 && yaw > 303) || (yaw >= 0 && yaw  <= 2) || (yaw <= 360 && yaw >= 358))
                    Camera.setAngle(Random.nextBoolean() ? Random.nextInt(320, 335) : Random.nextInt(285, 300));
            }
        });

        final Strategy widgetStrategy = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                return Widgets.get(594, 0).visible();
            }
        } ,new Runnable() {
            @Override
            public void run() {
                if (Widgets.get(594, 0).visible()) {
                    if (Widgets.get(594, 17).click(true)) {
                        for (int i = 0; i < 20 && Widgets.get(594, 0).visible(); i++)
                            Time.sleep(100);
                    }
                }
            }
        });

        final CombatStrategy combatStrategy = new CombatStrategy(this);
        final EquipStrategy equipStrategy = new EquipStrategy(this);
        final CompeteStrategy competeStrategy = new CompeteStrategy(this);
        final ShootStrategy shootStrategy = new ShootStrategy(this);
        final ExchangeStrategy exchangeStrategy = new ExchangeStrategy(this);
        final StrategyGroup groupCompete = new StrategyGroup(new Strategy[] { widgetStrategy, combatStrategy, new Strategy(equipStrategy, equipStrategy), cameraStrategy, competeStrategy,
                new Strategy(shootStrategy, shootStrategy) });

        setupStrategy = new Strategy(new Condition() {
            @Override
            public boolean validate() {
                return true;
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (Game.isLoggedIn() && Players.getLocal() != null && Players.getLocal().isOnScreen() && !Widgets.get(1252, 1).visible() && !Widgets.get(1234, 10).visible()) {
                    String money;
                    if (Inventory.getCount(true, 995) > 200 || Widgets.get(548, 201).visible() && (money = Widgets.get(548, 201).getText()) != null && parseMultiplier(money) > 200) {
                        labelPic = getImage("bbrangeguild.jpeg", "http://i53.tinypic.com/2jalnrc.jpeg", "jpeg");

                        if (Inventory.getCount(1464) > 0)
                            ticketCount = startItems = Inventory.getCount(true, 1464);

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
                            spamClick = gui.isSpamClicking();
                            equipArrows = gui.isEquipingArrows();
                            antiban = gui.isAntiban();
                            mouseAnti = gui.isMouseAntiban();
                            skillAnti = gui.isSkillAntiban();
                            afkMode = gui.isAfkMode();
                            skillData = new SkillData(Skills.RANGE, (startTime = System.currentTimeMillis()));

                            if (antiban)
                                antibanTimer.reset();
                            else
                                revoke(antibanStrategy);
                            revoke(exchangeStrategy);
                        } else {
                            exchangeMode = gui.getExchangeMode();
                            amount = gui.getAmount();

                            if (exchangeMode == 0)
                                exchangeItem = new ExchangeItem(0, 114, 47);
                            else if (exchangeMode == 1)
                                exchangeItem = new ExchangeItem(2, 1020, 892);
                            else if (exchangeMode == 2)
                                exchangeItem = new ExchangeItem(5, 292, 829);

                            if (Inventory.getCount(exchangeItem.getItemId()) > 0)
                                startItems = Inventory.getCount(true, exchangeItem.getItemId());
                            else if (getCount(exchangeItem.getItemId()) > 0)
                                startItems = getCount(exchangeItem.getItemId());

                            revoke(groupCompete);
                            startTime = System.currentTimeMillis();
                        }

                        price = GeItem.lookup(gui.isCompeting() ? 892 : exchangeItem.getItemId()).getPrice();
                        revoke(setupStrategy);
                    } else if (!Widgets.get(548, 201).visible()) {
                        if (Widgets.get(548, 202).click(true)) {
                            for (int i = 0; i < 20 && !Widgets.get(548, 201).visible(); i++)
                                Time.sleep(100);
                        }
                    } else {
                        log.info("You do not have any coins with you.");
                        stop();
                    }
                }
            }
        });

        setupStrategy.setReset(true);
        widgetStrategy.setReset(true);
        combatStrategy.setReset(true);
        competeStrategy.setReset(true);
        provide(setupStrategy);
        provide(antibanStrategy);
        provide(groupCompete);
        provide(exchangeStrategy);
    }

    @Override
    public void onStop() {
        if (startTime != 0) {
            final int gainedXP = skillData.getGainedXP();
            final int gainedTickets = Inventory.getCount(true, 1464) - startItems;
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
        while (!MOUSE_PATH_POINTS.isEmpty() && MOUSE_PATH_POINTS.peek().isUp())
            MOUSE_PATH_POINTS.remove();
        Point clientCursor = Mouse.getLocation();
        MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, 1500);
        if (MOUSE_PATH_POINTS.isEmpty() || !MOUSE_PATH_POINTS.getLast().equals(mpp))
            MOUSE_PATH_POINTS.add(mpp);
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : MOUSE_PATH_POINTS) {
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
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHints(RENDERING_HINTS);
        WidgetChild chatbox = Widgets.get(137, 0);
        if (skillData != null && Game.isLoggedIn() && chatbox.getAbsoluteX() > 0 && chatbox.getAbsoluteY() > 0) {
            absoluteY = chatbox.getAbsoluteY() - 1;

            if (!mainHidden) {
                g.setColor(BACKGROUND);
                g.fillRect(6, absoluteY, 506, chatbox.getHeight());

                g.setFont(ARIAL_12_BOLD);
                g.setColor(Color.BLACK);
                long runTime = skillData.getElapsedTime();
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
                int gainedXP = skillData.getGainedXP();
                int gainedTickets = (checkingSkills ? ticketCount - startItems : (ticketCount = Inventory.getCount(true, 1464)) - startItems);
                double profit = gainedTickets == 0 ? 0 : gainedTickets / 20.4 * price;
                g.drawString("" + gui.isSpamClicking() + ", " + gui.isEquipingArrows() + ", " + gui.isAntiban(), 100, absoluteY + 36);
                g.drawString("" + formatCommas((int) profit - (gamesCompleted * 200)), 100, absoluteY + 51);
                g.drawString("" + formatCommas(gainedXP), 100, absoluteY + 66);
                g.drawString("" + formatCommas(gainedTickets), 100, absoluteY + 81);
                g.drawString("" + formatCommas(gamesCompleted), 100, absoluteY + 96);

                int eph = (int) (gainedXP * 3600000D / runTime);
                int tph = (int) (gainedTickets * 3600000D / runTime);
                int pph = (int) (profit * 3600000D / runTime);
                int gph = (int) (gamesCompleted * 3600000D / runTime);
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

                int percent = Skills.getRealLevel(Skills.RANGE) == 99 ? 100 : skillData.getPercentToNextLevel();
                int eph = (int) (skillData.getGainedXP() * 3600000D / (System.currentTimeMillis() - startTime));
                long ttl = (long) ((skillData.getExperienceToLevel() * 3600000D) / eph);
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
            } else if (!gui.isCompeting() && Game.isLoggedIn() && chatbox.getAbsoluteX() > 0 && chatbox.getAbsoluteY() > 0) {
                absoluteY = chatbox.getAbsoluteY() - 1;

                if (!mainHidden) {
                    g.setColor(BACKGROUND);
                    g.fillRect(6, absoluteY, 506, chatbox.getHeight());

                    g.setFont(ARIAL_12_BOLD);
                    g.setColor(Color.BLACK);
                    long runTime = System.currentTimeMillis() -  startTime;
                    g.drawString("Time Running: " + (startTime != 0 ? Time.format(runTime) : "Waiting..."), 12, absoluteY + 17);
                    g.drawString("Current State: " + status, 158, absoluteY + 17);

                    g.setColor(GREEN);
                    g.drawString("Profit", 12, absoluteY + 36);
                    g.drawString("Bought:", 12, absoluteY + 51);
                    g.drawString("Item Price:", 12, absoluteY + 66);
                    g.drawString("Buys Left:", 12, absoluteY + 81);

                    g.setFont(ARIAL_12);
                    g.setColor(Color.BLACK);

                    int boughtItems = getCount(exchangeItem.getItemId()) - startItems;
                    int profit = boughtItems * price;
                    int buysLeft = getCount(1464) / exchangeItem.getValue();
                    g.drawString("" + formatCommas(profit), 12, absoluteY + 36);
                    g.drawString("" + formatCommas(boughtItems), 100, absoluteY + 51);
                    g.drawString("" + formatCommas(price), 100, absoluteY + 66);
                    g.drawString("" + formatCommas(buysLeft), 100, absoluteY + 81);

                    g.drawImage(labelPic, 6, absoluteY, null);
                }
            }
            drawMouse(g);
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

    @Override
    public void messageReceived(org.powerbot.core.event.events.MessageEvent messageEvent) {
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
}
