package app.gui.custom_components;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DisplayPanel extends JPanel {

    private BufferedImage frame;

    public void drawFrame(Mat image) {
        frame = (BufferedImage) HighGui.toBufferedImage(image);
        super.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(frame, 0, 0, super.getWidth(), super.getHeight(), null);
    }
}
