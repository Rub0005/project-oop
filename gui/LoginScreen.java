package gui;

import model.HospitalSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Login screen for the Hospital Management System.
 */
public class LoginScreen extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class.getName());
    private final HospitalSystem hospitalSystem;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton guestButton;

    public LoginScreen() {
        hospitalSystem = new HospitalSystem();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Hospital Management System - Login");
        setMinimumSize(new Dimension(800, 350));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        HospitalUIUtils.applyHospitalTheme();

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(HospitalUIUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(HospitalUIUtils.PRIMARY_COLOR);
        JLabel headerLabel = new JLabel("Hospital Management System");
        headerLabel.setFont(HospitalUIUtils.TITLE_FONT);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Login panel
        JPanel loginPanel = HospitalUIUtils.createStyledPanel("Login", new GridLayout(3, 2, 10, 10));

        // Username field
        JLabel usernameLabel = HospitalUIUtils.createStyledLabel("Username:");
        usernameField = HospitalUIUtils.createStyledTextField(20);
        usernameField.getAccessibleContext().setAccessibleDescription("Enter your username");

        // Password field
        JLabel passwordLabel = HospitalUIUtils.createStyledLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(HospitalUIUtils.TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HospitalUIUtils.PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.getAccessibleContext().setAccessibleName("Password field");
        passwordField.getAccessibleContext().setAccessibleDescription("Enter your password");

        // Button panel
        loginButton = HospitalUIUtils.createStyledButton("Login as Admin", KeyEvent.VK_L);
        guestButton = HospitalUIUtils.createStyledButton("Continue as Guest", KeyEvent.VK_G);

        // Add action listeners
        loginButton.addActionListener(e -> login());
        guestButton.addActionListener(e -> continueAsGuest());

        // Add components to panels
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty space

        JPanel buttonPanel = HospitalUIUtils.createButtonPanel(loginButton, guestButton);
        loginPanel.add(buttonPanel);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        try {
            if (username.isEmpty() || password.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Username and password are required!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Assume hospitalSystem.verifyAdminPassword now accepts char[]
            if (hospitalSystem.verifyAdminPassword(username, password)) {
                openMainApplication(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.severe("Login error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error during login: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, ' '); // Clear password from memory
        }
    }

    private void continueAsGuest() {
        openMainApplication(false);
    }

    private void openMainApplication(boolean isAdmin) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            HospitalManagementGUI mainApp = new HospitalManagementGUI(hospitalSystem, isAdmin);
            mainApp.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}