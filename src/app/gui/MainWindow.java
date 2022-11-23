package app.gui;
import app.gui.custom_components.DisplayPanel;
import app.gui.custom_components.mySpinner;
import app.main.SecurityCamera;
import app.util.SaveFile;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MainWindow {

    SecurityCamera camera;

    private JPanel MainPanel;
    private JPanel CameraPanel;
    private JLabel facesLabel;

    private JComboBox ImageMode;
    private JCheckBox ShowMotion;
    private JCheckBox ShowObjectMotion;
    private JCheckBox ShowFaces;

    private JSlider FpsSlider;
    private JButton openCameraButton;
    private JButton openFileButton;
    private JLabel videoPathLabel;

    private JSlider BlurSlider;
    private JSlider ThreshSlider;
    private JSlider DilatedSizeSlider;
    private JSlider DilatedIterationsSlider;
    private JSlider ObjectSizeSlider;
    private JSlider FaceSizeSlider;

    private JCheckBox recordCheckBox;
    private JSpinner secondsSpinner;

    private JList<String> saveList;
    private JButton SaveButton;
    private JButton LoadButton;
    private JButton DeleteButton;

    public MainWindow() {
        camera = new SecurityCamera((DisplayPanel) CameraPanel, facesLabel, openCameraButton, openFileButton, videoPathLabel, recordCheckBox);
        addGuiListeners();
        FpsSlider.setValue(camera.getFps());
        recordCheckBox.setSelected(false);
        secondsSpinner.setValue(5);
        loadSettings(false);
        refreshSaveList();
    }

    private void addGuiListeners() {
        openCameraButton.addActionListener(e -> { videoPathLabel.setText("File: None"); camera.setCamera(0); });
        ImageMode.addActionListener(e -> camera.setImageMode(ImageMode.getSelectedIndex()));
        FpsSlider.addChangeListener(e -> camera.setFps(FpsSlider.getValue()));
        recordCheckBox.addChangeListener(e -> camera.enableRecorder(recordCheckBox.isSelected()));
        secondsSpinner.addChangeListener(e -> camera.setTapeDuration((int) secondsSpinner.getValue()));
        addDisplayModeListener();
        addImgProcVariablesListener();
        addChooseFileListener();
        addSaveLoadDeleteListener();
    }

    private void addDisplayModeListener() {
        ChangeListener displayModeListener = e -> {
            JCheckBox src = (JCheckBox) e.getSource();
            boolean val = src.isSelected();
            if (src == ShowMotion)
                camera.setShowMotion(val);
            if (src == ShowObjectMotion)
                camera.setShowObjectMotion(val);
            if (src == ShowFaces)
                camera.setShowFaces(val);
        };
        ShowMotion.addChangeListener(displayModeListener);
        ShowObjectMotion.addChangeListener(displayModeListener);
        ShowFaces.addChangeListener(displayModeListener);
    }

    private void addImgProcVariablesListener() {
        ChangeListener imgProcVariablesListener = e -> {
            JSlider src = (JSlider) e.getSource();
            int val = src.getValue();
            if (src == BlurSlider) {
                if (val % 2 == 0)
                    val += 1;
                camera.setBlurSize(val);
            }
            if (src == ThreshSlider)
                camera.setThreshValue(val);
            if (src == DilatedSizeSlider)
                camera.setDilatedSize(val);
            if (src == DilatedIterationsSlider)
                camera.setDilatedIterations(val);
            if (src == ObjectSizeSlider)
                camera.setObjectSize(val);
            if (src == FaceSizeSlider)
                camera.setFaceSize(val);
        };
        BlurSlider.addChangeListener(imgProcVariablesListener);
        ThreshSlider.addChangeListener(imgProcVariablesListener);
        DilatedSizeSlider.addChangeListener(imgProcVariablesListener);
        DilatedIterationsSlider.addChangeListener(imgProcVariablesListener);
        ObjectSizeSlider.addChangeListener(imgProcVariablesListener);
        FaceSizeSlider.addChangeListener(imgProcVariablesListener);
    }

    private void addChooseFileListener(){
        openFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.getName().split("\\.").length != 2 || !Objects.equals(file.getName().split("\\.")[1], "mp4")) {
                    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(MainPanel), "Choose an .mp4 file!");
                    videoPathLabel.setText("File: None");
                    return;
                }

                String path = file.getAbsolutePath();
                videoPathLabel.setText("File: " + file.getName());
                camera.setCamera(path);
            }
        });
    }

    private void addSaveLoadDeleteListener() {
        ActionListener saveLoadDeleteListener = e -> {
            JButton src = (JButton) e.getSource();
            if (src == SaveButton)
                saveSettings();
            if (src == LoadButton)
                loadSettings(true);
            if (src == DeleteButton)
                deleteSettings();
        };
        SaveButton.addActionListener(saveLoadDeleteListener);
        LoadButton.addActionListener(saveLoadDeleteListener);
        DeleteButton.addActionListener(saveLoadDeleteListener);
    }

    private void saveSettings() {
        if (saveList.getSelectedValue() == null)
            saveList.setSelectedIndex(0);

        String selectedValue = saveList.getSelectedValue();
        Window window = SwingUtilities.getWindowAncestor(MainPanel);

        if (Objects.equals(selectedValue, "Default")) {
            JOptionPane.showMessageDialog(window, "You can't overwrite the default file!");
            return;
        }
        if (Objects.equals(selectedValue, "New File")) {
            String fileName = JOptionPane.showInputDialog(window, "File name");
            if (fileName == null)
                return;
            if (Objects.equals(fileName, "New File") || Objects.equals(fileName, "Default")) {
                JOptionPane.showMessageDialog(window, "Invalid name!");
                return;
            }
            selectedValue = fileName;
            for (int i = 0; i < saveList.getModel().getSize(); i++)
                if (Objects.equals(selectedValue, saveList.getModel().getElementAt(i)))
                    if (JOptionPane.showConfirmDialog(window, "Overwrite " + selectedValue + "?") != JOptionPane.YES_OPTION)
                        return;
        }
        else {
            if (JOptionPane.showConfirmDialog(window, "Overwrite " + selectedValue + "?") != JOptionPane.YES_OPTION)
                return;
        }

        SaveFile saveFile = new SaveFile();
        if (saveFile.save(selectedValue,
                ShowMotion.isSelected(), ShowObjectMotion.isSelected(), ShowFaces.isSelected(), FpsSlider.getValue(),
                BlurSlider.getValue(), ThreshSlider.getValue(), DilatedSizeSlider.getValue(), DilatedIterationsSlider.getValue(),
                ObjectSizeSlider.getValue(), FaceSizeSlider.getValue()))
            refreshSaveList();
        else
            JOptionPane.showMessageDialog(window, "Invalid name!");
    }

    private void loadSettings(boolean confirmDialog) {
        Window window = SwingUtilities.getWindowAncestor(MainPanel);
        String selectedValue = saveList.getSelectedValue();

        if (!confirmDialog)
            selectedValue = SaveFile.getFileName();

        if (selectedValue == null) {
            JOptionPane.showMessageDialog(window, "Choose a file!");
            return;
        }
        if (Objects.equals(selectedValue, "New File")) {
            JOptionPane.showMessageDialog(window, "You can't load that!");
            return;
        }
        if (confirmDialog)
            if (JOptionPane.showConfirmDialog(window, "Load " + selectedValue + "?") != JOptionPane.YES_OPTION)
                return;

        SaveFile saveFile = new SaveFile();
        if(!saveFile.load(selectedValue,
                ShowMotion, ShowObjectMotion, ShowFaces, FpsSlider, BlurSlider, ThreshSlider,
                DilatedSizeSlider, DilatedIterationsSlider, ObjectSizeSlider, FaceSizeSlider))
            JOptionPane.showMessageDialog(window, "Couldn't load file!");
    }

    private void deleteSettings() {
        Window window = SwingUtilities.getWindowAncestor(MainPanel);
        String selectedValue = saveList.getSelectedValue();

        if (selectedValue == null) {
            JOptionPane.showMessageDialog(window, "Choose a file!");
            return;
        }
        if (Objects.equals(selectedValue, "New File") || selectedValue.equals("Default")) {
            JOptionPane.showMessageDialog(window, "You can't delete that!");
            return;
        }
        if (JOptionPane.showConfirmDialog(window, "Delete " + selectedValue + "?") != JOptionPane.YES_OPTION)
            return;

        SaveFile saveFile = new SaveFile();
        if(saveFile.delete(selectedValue))
            refreshSaveList();
        else
            JOptionPane.showMessageDialog(window, "Couldn't delete!");
    }

    private void refreshSaveList() {
        File savePath = new File("src/data/saves");
        File[] saveFiles = savePath.listFiles();
        Collection<String> fileNames = new ArrayList<>();
        assert saveFiles != null;
        for (File file : saveFiles)
            if (!file.getName().equals("Default") && !file.getName().equals("FileName"))
                fileNames.add(file.getName());
        DefaultListModel<String> model = new DefaultListModel<>();
        model.add(0, "New File");
        model.add(1, "Default");
        model.addAll(fileNames);
        saveList.setModel(model);
        saveList.setSelectedValue(SaveFile.getFileName(), true);
    }

    public void startProgram() {
        camera.start();
    }

    public JPanel getPanel() {
        return MainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        CameraPanel = new DisplayPanel();
        secondsSpinner = new mySpinner(1, 9999);

    }
}
