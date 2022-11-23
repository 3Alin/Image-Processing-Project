package app.main;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class FaceDetector {

    private float faceSize = 0.15f;
    private final Mat gray = new Mat();
    private final Mat equalised = new Mat();
    private final MatOfRect faces = new MatOfRect();

    private int facesCount = 0;

    public boolean detect(Mat image, Mat dest, boolean showFaces) {

        // Procesare
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, equalised);

        // Marimea fetei
        int height = equalised.height();
        int absoluteFaceSize = Math.max(Math.round(height * faceSize), 0);

        // Incarcarea fisierului .xml
        CascadeClassifier faceCascade = new CascadeClassifier();
        faceCascade.load("src/data/haarcascades/haarcascade_frontalface_alt.xml");

        // Detectarea fetelor
        faceCascade.detectMultiScale(equalised, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
            new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        // Marcarea fetelor
        Rect[] faceArray = faces.toArray();
        if (showFaces)
            for (Rect face : faceArray)
                Imgproc.rectangle(dest, face, new Scalar(0, 255, 0), 2);

        // Rezultat
        facesCount = faceArray.length;
        return faceArray.length != 0;
    }

    public void setFaceSize(float size) {
        faceSize = size;
    }
    public Mat getGray() {
        return gray;
    }
    public Mat getEqualised() {
        return equalised;
    }
    public int getFacesCount() {
        return facesCount;
    }
}
