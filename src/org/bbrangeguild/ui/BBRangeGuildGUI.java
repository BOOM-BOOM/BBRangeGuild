package org.bbrangeguild.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BBRangeGuildGUI extends JFrame {

    private JToggleButton competeButton;
    private JToggleButton exchangeButton;
    private JPanel competePane;
    private JCheckBox spamClickBox;
    private JCheckBox equipArrowsBox;
    private JRadioButton antibanButton;
    private JCheckBox mouseAntibanBox;
    private JCheckBox skillAntibanBox;
    private JCheckBox afkModeBox;
    private JPanel exchangePane;
    private JComboBox<String> itemExchange;
    private JTextField amountBox;
    private boolean isRunning = true, compete, spamClick, equipArrows, antiban, mouseAnti, skillAnti, afkMode;
    private int exchangeMode, amount;

    public BBRangeGuildGUI() {
        initComponents();
    }

    private void initComponents() {
        competeButton = new JToggleButton();
        exchangeButton = new JToggleButton();
        competePane = new JPanel();
        spamClickBox = new JCheckBox();
        equipArrowsBox = new JCheckBox();
        antibanButton = new JRadioButton();
        mouseAntibanBox = new JCheckBox();
        skillAntibanBox = new JCheckBox();
        afkModeBox = new JCheckBox();
        exchangePane = new JPanel();
        itemExchange = new JComboBox<String>();
        JLabel amountLabel = new JLabel();
        amountBox = new JTextField();
        JButton startButton = new JButton();

        //======== BBRangeGuildGUI ========
        {
            setFont(new Font("Arial", Font.PLAIN, 12));
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("BBRangeGuild GUI");
            setResizable(false);
            Container BBRangeGuildGUIContentPane = getContentPane();
            BBRangeGuildGUIContentPane.setLayout(new GridBagLayout());
            ((GridBagLayout)BBRangeGuildGUIContentPane.getLayout()).columnWidths = new int[] {17, 23, 85, 23, 21, 23, 85, 23, 12, 0};
            ((GridBagLayout)BBRangeGuildGUIContentPane.getLayout()).rowHeights = new int[] {10, 0, 155, 0, 0, 0};
            ((GridBagLayout)BBRangeGuildGUIContentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout)BBRangeGuildGUIContentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- competeButton ----
            competeButton.setText("Compete");
            competeButton.setFont(new Font("Arial", Font.PLAIN, 11));
            competeButton.setSelected(true);
            BBRangeGuildGUIContentPane.add(competeButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));
            
            competeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (final Component component : competePane.getComponents()) {
                        if (competeButton.isSelected())
                            component.setEnabled(true);
                        else
                            component.setEnabled(false);
                    }

                    for (final Component component : exchangePane.getComponents()) {
                        if (exchangeButton.isSelected())
                            component.setEnabled(true);
                        else
                            component.setEnabled(false);
                    }

                    if (!antibanButton.isSelected()) {
                        mouseAntibanBox.setEnabled(false);
                        skillAntibanBox.setEnabled(false);
                        afkModeBox.setEnabled(false);
                    }
                }
            });

            //---- exchangeButton ----
            exchangeButton.setText("Exchange");
            BBRangeGuildGUIContentPane.add(exchangeButton, new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));
            
            exchangeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (final Component component : exchangePane.getComponents()) {
                        if (exchangeButton.isSelected())
                            component.setEnabled(true);
                        else
                            component.setEnabled(false);
                    }

                    for (final Component component : competePane.getComponents()) {
                        if (competeButton.isSelected())
                            component.setEnabled(true);
                        else
                            component.setEnabled(false);
                    }
                }
            });

            //======== competePane ========
            {
                competePane.setBorder(new TitledBorder(null, "Compete", TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Arial", Font.PLAIN, 12)));

                competePane.setLayout(new GridBagLayout());
                ((GridBagLayout)competePane.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)competePane.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
                ((GridBagLayout)competePane.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                ((GridBagLayout)competePane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- spamClickBox ----
                spamClickBox.setText("Spam click");
                spamClickBox.setFont(new Font("Arial", Font.PLAIN, 11));
                spamClickBox.setSelected(true);
                competePane.add(spamClickBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- equipArrowsBox ----
                equipArrowsBox.setText("Equip arrows");
                equipArrowsBox.setFont(new Font("Arial", Font.PLAIN, 11));
                equipArrowsBox.setSelected(true);
                competePane.add(equipArrowsBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- antibanButton ----
                antibanButton.setText("Antiban");
                antibanButton.setFont(new Font("Arial", Font.PLAIN, 11));
                competePane.add(antibanButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                antibanButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final boolean enabled = antibanButton.isSelected();
                        mouseAntibanBox.setEnabled(enabled);
                        skillAntibanBox.setEnabled(enabled);
                        afkModeBox.setEnabled(enabled);
                    }

                });

                //---- mouseAntibanBox ----
                mouseAntibanBox.setText("Mouse");
                mouseAntibanBox.setFont(new Font("Arial", Font.PLAIN, 11));
                mouseAntibanBox.setEnabled(false);
                competePane.add(mouseAntibanBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- skillAntibanBox ----
                skillAntibanBox.setText("Skill");
                skillAntibanBox.setFont(new Font("Arial", Font.PLAIN, 11));
                skillAntibanBox.setEnabled(false);
                competePane.add(skillAntibanBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- afkModeBox ----
                afkModeBox.setText("AFK Mode");
                afkModeBox.setFont(new Font("Arial", Font.PLAIN, 11));
                afkModeBox.setEnabled(false);
                competePane.add(afkModeBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            BBRangeGuildGUIContentPane.add(competePane, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //======== exchangePane ========
            {
                exchangePane.setBorder(new TitledBorder(null, "Exchange", TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Arial", Font.PLAIN, 12)));
                exchangePane.setLayout(new GridBagLayout());
                ((GridBagLayout)exchangePane.getLayout()).columnWidths = new int[] {60, 55, 0};
                ((GridBagLayout)exchangePane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)exchangePane.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
                ((GridBagLayout)exchangePane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};

                //---- itemExchange ----
                itemExchange.setFont(new Font("Arial", Font.PLAIN, 11));
                itemExchange.setModel(new DefaultComboBoxModel<String>(new String[] {
                        "Barbed bolt tips",
                        "Rune arrows",
                        "Adamant javelins"
                }));
                exchangePane.add(itemExchange, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                //---- amountLabel ----
                amountLabel.setText("Amount:");
                amountLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                exchangePane.add(amountLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 5), 0, 0));

                //---- amountBox ----
                amountBox.setText("MAX");
                exchangePane.add(amountBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                for (final Component component : exchangePane.getComponents()) {
                    if (exchangeButton.isSelected())
                        component.setEnabled(true);
                    else
                        component.setEnabled(false);
                }
            }
            BBRangeGuildGUIContentPane.add(exchangePane, new GridBagConstraints(5, 2, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            //---- startButton ----
            startButton.setText("Begin Botting");
            startButton.setFont(new Font("Arial", Font.PLAIN, 12));
            BBRangeGuildGUIContentPane.add(startButton, new GridBagConstraints(2, 3, 5, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

            startButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (compete = competeButton.isSelected()) {
                        spamClick = spamClickBox.isSelected();
                        equipArrows = equipArrowsBox.isSelected();
                        antiban = antibanButton.isSelected();
                        mouseAnti = mouseAntibanBox.isSelected();
                        skillAnti = skillAntibanBox.isSelected();
                        afkMode = afkModeBox.isSelected();
                    } else {
                        final String amountText = amountBox.getText();
                        exchangeMode = itemExchange.getSelectedIndex();
                        amount = amountText.equalsIgnoreCase("MAX") ? 0 : Integer.getInteger(amountText);
                    }

                    isRunning = false;
                    dispose();
                }

            });

            pack();
            setLocationRelativeTo(getOwner());
        }

        //---- modeGroup ----
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(competeButton);
        modeGroup.add(exchangeButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isCompeting() {
        return compete;
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
    
}
