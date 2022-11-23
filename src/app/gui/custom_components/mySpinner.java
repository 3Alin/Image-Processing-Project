package app.gui.custom_components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

public class mySpinner extends JSpinner {

    public mySpinner(int min, int max) {
        super.setUI(new BasicSpinnerUI() {
            protected Component createNextButton() {
                return null;
            }
            protected Component createPreviousButton() {
                return null;
            }
        });
        super.addChangeListener(e -> {
            if ((int) super.getValue() > max)
                super.setValue(max);
            if ((int) super.getValue() < min)
                super.setValue(min);
        });
        super.setValue(min);
    }
}
