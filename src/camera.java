import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class camera {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Camera Capture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setSize(640, 480);
        frame.setVisible(true);

        VideoCapture capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            System.err.println("Unable to open camera");
            System.exit(0);
        }

        Mat frameMat = new Mat();
        MatOfByte frameBytes = new MatOfByte();

        while (true) {
            capture.read(frameMat);
            if (!frameMat.empty()) {
                Imgproc.resize(frameMat, frameMat, new Size(640, 480));
                Imgcodecs.imencode(".jpg", frameMat, frameBytes);
                byte[] byteArray = frameBytes.toArray();
                BufferedImage image = null;
                try {
                    InputStream in = new ByteArrayInputStream(byteArray);
                    image = ImageIO.read(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageLabel.setIcon(new ImageIcon(image));
                frame.pack();
            }
        }
    }
}
