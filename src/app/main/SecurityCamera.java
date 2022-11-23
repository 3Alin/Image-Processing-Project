package app.main;

import app.gui.custom_components.DisplayPanel;
import app.util.Recorder;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.absdiff;
import static org.opencv.imgproc.Imgproc.*;

public class SecurityCamera {

    private final FaceDetector faceDetector = new FaceDetector();
    private final Recorder recorder = new Recorder();
    private final VideoCapture camera = new VideoCapture(0);
    private final DisplayPanel display;
    private final JLabel facesLabel;
    private final JButton openCameraButton;
    private final JButton openFileButton;
    private final JLabel videoPathLabel;
    private final JCheckBox recordCheckBox;

    private Thread mainThread;
    private int target_fps = (int) camera.get(Videoio.CAP_PROP_FPS);
    private long optimal_time = 1000000000 / target_fps;

    private final Mat frame = new Mat();
    private final Mat frame1 = new Mat();
    private final Mat frame2 = new Mat();
    private final Mat diff = new Mat();
    private final Mat gray = new Mat();
    private final Mat blur = new Mat();
    private final Mat thresh = new Mat();
    private final Mat dilated = new Mat();
    private Mat displayedImage = frame;

    private boolean showMotion = true;
    private boolean showObjectMotion = true;
    private boolean showFaces = true;

    private Size blurSize = new Size(5, 5);
    private int threshValue = 20;
    private Size dilatedSize = new Size(4, 4);
    private int dilatedIterations = 3;
    private int objectSize = 5000;

    public SecurityCamera(DisplayPanel displayPanel, JLabel facesLabel, JButton openCameraButton, JButton openFileButton, JLabel videoPathLabel, JCheckBox recordCheckBox) {
        this.display = displayPanel;
        this.facesLabel = facesLabel;
        this.openCameraButton = openCameraButton;
        this.openFileButton = openFileButton;
        this.videoPathLabel = videoPathLabel;
        this.recordCheckBox = recordCheckBox;
    }
    public void start() {

        mainThread = new Thread(() -> {

            camera.read(frame);
            camera.read(frame1);
            camera.read(frame2);
            faceDetector.detect(frame1, frame, false);
            openCameraButton.setEnabled(true);
            openFileButton.setEnabled(true);

            long startTime;
            long totalTime;
            while (camera.read(frame2)) {

                startTime = System.nanoTime();
                processFrame();
                display.drawFrame(displayedImage);
                frame2.copyTo(frame1);
                totalTime = System.nanoTime() - startTime;
                if (totalTime < optimal_time)
                    try {Thread.sleep((optimal_time - totalTime) / 1000000);} catch(Exception e){e.printStackTrace();}
            }
        });

        try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
        mainThread.start();
    }

    private void processFrame() {

        frame2.copyTo(frame);
        if (detectMotion())
            if (faceDetector.detect(frame2, frame, showFaces)) {
                Recorder.saveTimestamp(videoPathLabel.getText());
                recorder.recordVideo(frame2);
            }
        facesLabel.setText("Faces found: " + faceDetector.getFacesCount());
        if (recorder.isRecording())
            recorder.recordVideo(frame1);
    }

    private boolean detectMotion() {

        // Detectarea miscarii
        absdiff(frame1, frame2, diff);
        cvtColor(diff, gray, COLOR_RGB2GRAY);
        // Marirea suprafetei detectate
        GaussianBlur(gray, blur, blurSize, 0);
        threshold(blur, thresh, threshValue, 255, THRESH_BINARY);
        dilate(thresh, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, dilatedSize), new Point(), dilatedIterations);
        //Gasirea contururilor
        List<MatOfPoint> contours = new ArrayList<>();
        findContours(dilated, contours, new Mat(), RETR_TREE, CHAIN_APPROX_SIMPLE);
        if (showMotion)
            drawContours(frame, contours, -1, new Scalar(255, 0, 0), 2);
        // Detectarea miscarilor in functie de variabila
        for (Mat mCon : contours) {
            if (contourArea(mCon) < objectSize)
                continue;
            if (showObjectMotion) {
                Rect points = boundingRect(mCon);
                rectangle(frame, new Point(points.x, points.y), new Point(points.x + points.width, points.y + points.height),
                        new Scalar(255, 255, 0), 2);
            }
            return true;
        }
        return false;
    }

    public void setCamera(int val) {
        endThread();
        camera.open(val);
        start();
        recorder.setEnabled(recordCheckBox.isSelected());
        recordCheckBox.setEnabled(true);
    }

    public void setCamera(String path) {
        recordCheckBox.setEnabled(false);
        recorder.setEnabled(false);
        endThread();
        camera.open(path);
        start();
    }

    public void endThread() {
        openCameraButton.setEnabled(false);
        openFileButton.setEnabled(false);
        try {
            mainThread.join(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setFps(int fps) {
        target_fps = fps;
        optimal_time = 1000000000 / target_fps;
    }
    public int getFps() {
        return target_fps;
    }
    public void setImageMode(int mode) {
        Mat[] modes = new Mat[] { frame, diff, gray, blur, thresh, dilated, faceDetector.getGray(), faceDetector.getEqualised() };
        displayedImage = modes[mode];
    }
    public void setShowMotion(boolean show) {
        showMotion = show;
    }
    public void setShowObjectMotion(boolean show) {
        showObjectMotion = show;
    }
    public void setShowFaces(boolean show) {
        showFaces = show;
    }
    public void setTapeDuration(int seconds) {
        recorder.setDuration(seconds);
    }
    public void enableRecorder(boolean enable) {
        recorder.setEnabled(enable);
    }
    public void setBlurSize(int blurSize) {
        this.blurSize = new Size(blurSize, blurSize);
    }
    public void setThreshValue(int threshValue) {
        this.threshValue = threshValue;
    }
    public void setDilatedSize(int dilatedSize) {
        this.dilatedSize = new Size(dilatedSize, dilatedSize);
    }
    public void setDilatedIterations(int dilatedIterations) {
        this.dilatedIterations = dilatedIterations;
    }
    public void setObjectSize(int objectSize) {
        this.objectSize = objectSize;
    }
    public void setFaceSize(int size) {
        faceDetector.setFaceSize((float) size / 100);
    }
}
