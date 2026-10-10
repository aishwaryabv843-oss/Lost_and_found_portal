package com.lostandfound.ui;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;

/**
 * Main Application Launcher
 * Initiates Look-and-Feel parameters and safely fires up the Login Window.
 */
public class MainApp {
    public static void main(String[] args) {
        // Apply professional Flat/Nimbus rendering theme built directly into standard JDK libraries
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Safe fallback to native Operating System theme parameters
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Fire up Swing on the EDT (Event Dispatch Thread) to ensure thread-safe component drawing
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
