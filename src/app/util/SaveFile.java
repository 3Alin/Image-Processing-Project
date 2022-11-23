package app.util;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class SaveFile {

    private final String EXT = "";

    public boolean load(String fileName,
                        JCheckBox ShowMotion, JCheckBox ShowObjectMotion, JCheckBox ShowFaces,
                        JSlider FpsSlider, JSlider BlurSlider, JSlider ThreshSlider,
                        JSlider DilatedSizeSlider, JSlider DilatedIterationsSlider,
                        JSlider ObjectSizeSlider, JSlider FaceSizeSlider) {
        try {
            Files.writeString(Paths.get("src/data/saves/FileName"), fileName);
            String text = Files.readString(Path.of("src/data/saves/" + fileName + EXT));
            String[] values = text.split("\n");

            ShowMotion.setSelected(Boolean.parseBoolean(values[0]));
            ShowObjectMotion.setSelected(Boolean.parseBoolean(values[1]));
            ShowFaces.setSelected(Boolean.parseBoolean(values[2]));

            FpsSlider.setValue(Integer.parseInt(values[3]));
            BlurSlider.setValue(Integer.parseInt(values[4]));
            ThreshSlider.setValue(Integer.parseInt(values[5]));
            DilatedSizeSlider.setValue(Integer.parseInt(values[6]));
            DilatedIterationsSlider.setValue(Integer.parseInt(values[7]));
            ObjectSizeSlider.setValue(Integer.parseInt(values[8]));
            FaceSizeSlider.setValue(Integer.parseInt(values[9]));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean save(String fileName, boolean ShowMotion, boolean ShowObjectMotion, boolean ShowFaces,
                        int FpsSlider, int BlurSlider, int ThreshSlider, int DilatedSizeSlider,
                        int DilatedIterationsSlider, int ObjectSizeSlider, int FaceSizeSlider) {
        try {
            String text = "";
            text = text + ShowMotion + "\n";
            text = text + ShowObjectMotion + "\n";
            text = text + ShowFaces + "\n";
            text = text + FpsSlider + "\n";
            text = text + BlurSlider + "\n";
            text = text + ThreshSlider + "\n";
            text = text + DilatedSizeSlider + "\n";
            text = text + DilatedIterationsSlider + "\n";
            text = text + ObjectSizeSlider + "\n";
            text = text + FaceSizeSlider + "\n";

            Files.writeString(Paths.get("src/data/saves/" + fileName + EXT), text);
            Files.writeString(Paths.get("src/data/saves/FileName"), fileName);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean delete(String fileName) {
        try {
            if (Objects.equals(fileName, ""))
                    return false;
            Files.deleteIfExists(Path.of("src/data/saves/" + fileName + EXT));
            if (Objects.equals(fileName, getFileName()))
                Files.writeString(Paths.get("src/data/saves/FileName"), "Default");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getFileName() {

        String fileName = null;
        try {
            fileName = Files.readString(Path.of("src/data/saves/FileName"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }
}
