import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.awt.BorderLayout;
import java.sql.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraToDatabaseExample extends JFrame {
    private JLabel imageLabel;

    // Database connection parameters
     private String url = "jdbc:mysql://localhost:3306/facecheck";
    private String username = "root";
    private String password = "MOMDAD@20039m ";

    public CameraToDatabaseExample() {
        super("Camera to Database Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        imageLabel = new JLabel();
        contentPane.add(imageLabel, BorderLayout.CENTER);
        setContentPane(contentPane);

        setVisible(true);

        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Open the default camera
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Camera not opened.");
            return;
        }

        try {
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(url, username, password);

            while (true) {
                // Capture a frame from the camera
                Mat frame = new Mat();
                camera.read(frame);

                // Convert the frame to a BufferedImage for display
                BufferedImage image = Mat2BufferedImage(frame);
                imageLabel.setIcon(new ImageIcon(image));

                // Convert the frame to a byte array for storage in the database
                byte[] imageData = Mat2ByteArray(frame);

                // Insert the byte array into the database
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO pic(pic) VALUES (?)");
                InputStream inputStream = new ByteArrayInputStream(imageData);
                stmt.setBinaryStream(1, inputStream, imageData.length);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            }

            // Close the connection

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return;
        }
    }

    private BufferedImage Mat2BufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;
        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);
        return image;
    }

    private byte[] Mat2ByteArray(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        matrix.get(0, 0, data);

        return data;
    }

    public static void main(String[] args) {
        new CameraToDatabaseExample();
    }
}
