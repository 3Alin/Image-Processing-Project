package app.main;

import app.gui.MainWindow;
import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Proiect Proecsarea Imaginilor - Jinariu Paul Alin");
        frame.setSize(900, 740);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new CardLayout());
        MainWindow window = new MainWindow();
        frame.setContentPane(window.getPanel());
        frame.setVisible(true);

        window.startProgram();
    }
}
