package gui;

import javax.swing.SwingUtilities;

import util.FileHandler;

/** Start the Smart Student Survival System (opens the login screen first). */
public class Main {
    public static void main(String[] args) {
        FileHandler.setDataDir("data");
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
