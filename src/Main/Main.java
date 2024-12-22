package Main;

import Component.PanelGame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

public class Main extends JFrame {
    public Main() {
        init();
    }

    private void init() {
        // Set the title of the application window to "Plane and Rocket - OOP Project".
        setTitle("Plane and Rocket - OOP Project");

        // Set the size of the window to 1366x768 pixels.
        setSize(1366, 768);
        
        setLocationRelativeTo(null);
        // Center the window on the screen.
        
        // Prevent the user from resizing the window.
        setResizable(false);
        
        // Set the default close operation to exit the application.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use BorderLayout as the layout manager for the window.
        setLayout(new BorderLayout());
        
        // Create an instance of PanelGame, which might be part of the game interface.
        PanelGame panelGame = new PanelGame();
        
        // Add the panelGame to the main window.
        add(panelGame);
        
        // Add a WindowListener to listen for the window opening event.
        // When the window is opened, call the start() method of panelGame.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panelGame.start();
            }
        });
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
