package app.util;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Objects;

public class Recorder {

    double fps;
    long frameCount;
    int fourcc;
    String fileName;
    VideoWriter video;
    Size resolution;

    boolean enabled;
    boolean recording;
    long duration;

    public Recorder() {

        fileName = "src/results/";
        fourcc = VideoWriter.fourcc('M','J','P','G');
        VideoCapture cam = new VideoCapture(0);
        fps = cam.get(Videoio.CAP_PROP_FPS);
        resolution = new Size(cam.get(Videoio.CAP_PROP_FRAME_WIDTH), cam.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        enabled = true;
        recording = false;
        duration = 60;
        frameCount = 0;
    }

    public void recordVideo(Mat frame) {

        if (!enabled)
            return;
        if (!recording)
            initialiseRecording();
        else
            continueRecording(frame);

        if (fps * duration < frameCount)
            stopRecording();
    }

    private void initialiseRecording() {

        recording = true;
        frameCount = 0;
        int fileCount = -1;
        try {
            Files.createDirectories(Paths.get("src/results/videos"));
            fileCount = Objects.requireNonNull(new File("src/results/videos").list()).length;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        fileName = "src/results/videos/video_" + fileCount + ".avi";
        video = new VideoWriter(fileName, fourcc, fps, resolution);
    }

    private void continueRecording(Mat frame) {
        video.write(frame);
        frameCount++;
    }

    private void stopRecording() {
        recording = false;
        video.release();
    }

    public static void saveTimestamp(String fileName) {
        try {
            if (Objects.equals(fileName, "File: None"))
                return;

            fileName = fileName.replace("File: ", "");
            fileName = fileName.replaceAll(".mp4", ".txt");
            Path path = Path.of("src/results/timestamps/" + fileName);

            if (!Files.exists(path))
                Files.createFile(path);

            String timestamps = String.valueOf(new Timestamp(System.currentTimeMillis()));
            timestamps = timestamps + "\n" + Files.readString(path);
            Files.writeString(path, timestamps);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return recording;
    }
    public void setDuration(int seconds) {
        duration = seconds;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
