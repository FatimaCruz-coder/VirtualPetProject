package group33.VirtualPet.src.main;

import group33.VirtualPet.src.main.ui.MainMenuScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenuScreen mainMenu = new MainMenuScreen();
            mainMenu.setVisible(true);
        });
    }
}
