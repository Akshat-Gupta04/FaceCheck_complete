import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

public class CAMERAA {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Declare the Connection variable outside the main method
    private static Connection con;

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

        try {
            // Establish a connection to the SQL database using JDBC
            String url = "jdbc:mysql://localhost:3306/facecheck";
            String username = "root";
            String password = "MOMDAD@20039m";
            con = DriverManager.getConnection(url, username, password);

            // Create a table to store the image data
            String createTableQuery = "CREATE TABLE IF NOT EXISTS images (id INT PRIMARY KEY AUTO_INCREMENT, data BLOB)";
            PreparedStatement createTableStatement = con.prepareStatement(createTableQuery);
            createTableStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

                    // Insert the image data into the SQL database
                    String insertQuery = "INSERT INTO ran (image) VALUES (?)";
                    PreparedStatement insertStatement = con.prepareStatement(insertQuery);
                    insertStatement.setBytes(1, byteArray);
                    insertStatement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageLabel.setIcon(new ImageIcon(image));
                frame.pack();
            }
        }
    }
}
