package application;

import gui.LoginScreen;

import javax.swing.*;

/**
 * Main application class to launch the Hospital Management System
 */
public class HospitalApplication {
    public static void main(String[] args) {
        // Launch the login screen first
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}