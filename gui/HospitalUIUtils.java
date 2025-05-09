package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Utility class for styling Swing components consistently across the Hospital Management System.
 */
public class HospitalUIUtils {
    public static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    public static final Color PRIMARY_COLOR = new Color(30, 144, 255);
    public static final Color SECONDARY_COLOR = new Color(135, 206, 250);
    public static final Color TEXT_COLOR = new Color(25, 25, 112);
    public static final Color ERROR_COLOR = new Color(220, 20, 60);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final String DATE_FORMAT = "YYYY-MM-DD";

    /**
     * Applies the hospital theme to the Swing UI.
     */
    public static void applyHospitalTheme() {
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", BOLD_FONT);
        UIManager.put("Label.font", REGULAR_FONT);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TextField.font", REGULAR_FONT);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("ComboBox.font", REGULAR_FONT);
        UIManager.put("ComboBox.foreground", TEXT_COLOR);
        UIManager.put("Table.font", REGULAR_FONT);
        UIManager.put("TableHeader.font", BOLD_FONT);
    }

    /**
     * Creates a styled JPanel with a titled border.
     *
     * @param title the title of the panel
     * @param layout the layout manager
     * @return a styled JPanel
     */
    public static JPanel createStyledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2), title,
                0, 0, BOLD_FONT, TEXT_COLOR));
        return panel;
    }

    /**
     * Creates a styled JLabel.
     *
     * @param text the label text
     * @return a styled JLabel
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * Creates a styled JTextField.
     *
     * @param columns the number of columns
     * @return a styled JTextField
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(REGULAR_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    /**
     * Creates a styled JPasswordField.
     *
     * @param columns the number of columns
     * @return a styled JPasswordField
     */
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(REGULAR_FONT);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return passwordField;
    }

    /**
     * Creates a styled JComboBox.
     *
     * @param <T> the type of the combo box items
     * @return a styled JComboBox
     */
    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(REGULAR_FONT);
        comboBox.setBackground(BACKGROUND_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        return comboBox;
    }

    /**
     * Creates a styled JButton.
     *
     * @param text      the button text
     * @param mnemonic  the mnemonic key
     * @return a styled JButton
     */
    public static JButton createStyledButton(String text, int mnemonic) {
        JButton button = new JButton(text);
        button.setFont(BOLD_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setMnemonic(mnemonic);
        button.setFocusPainted(false);

        return button;
    }

    /**
     * Creates a panel for buttons with FlowLayout.
     *
     * @param buttons the buttons to include
     * @return a JPanel containing the buttons
     */
    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }
        return buttonPanel;
    }

    /**
     * Creates a form panel with labeled fields.
     *
     * @param title  the title of the form
     * @param labels the labels for the fields
     * @param fields the fields corresponding to the labels
     * @return a JPanel containing the form
     */
    public static JPanel createFormPanel(String title, String[] labels, JComponent[] fields) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2), title,
                0, 0, BOLD_FONT, TEXT_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            formPanel.add(createStyledLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(fields[i], gbc);
        }

        return formPanel;
    }

    /**
     * Creates a styled JTable.
     *
     * @param model the table model
     * @return a styled JTable
     */
    public static JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(REGULAR_FONT);
        table.setBackground(BACKGROUND_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setGridColor(SECONDARY_COLOR);
        table.setRowHeight(25);

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, renderer);

        return table;
    }
}