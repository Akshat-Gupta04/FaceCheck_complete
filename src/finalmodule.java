import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class finalmodule {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Declare the Connection variable outside the main method
    private static Connection con;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Camera Capture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.CENTER);

        JLabel imageLabel = new JLabel();
        panel.add(imageLabel);

        JButton captureButton = new JButton("Capture");
        panel.add(captureButton);
        JTextField nameField = new JTextField(20);
        panel.add(nameField);

        JTextField nicknameField = new JTextField(20);
        panel.add(nicknameField);

        frame.setSize(640, 600);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        captureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                capture.read(frameMat);
                if (!frameMat.empty()) {
                    Imgproc.resize(frameMat, frameMat, new Size(640, 480));
                    Imgcodecs.imencode(".jpg", frameMat, frameBytes);
                    byte[] byteArray = frameBytes.toArray();
                    BufferedImage image = null;
                    try {
                        InputStream in = new ByteArrayInputStream(byteArray);
                        image = ImageIO.read(in);

                        // Insert the image data, name, and nickname into the SQL database
                        String insertQuery = "INSERT INTO ran (name, image,RollNo) VALUES (?, ?, ?)";
                        PreparedStatement insertStatement = con.prepareStatement(insertQuery);
                        insertStatement.setString(1, nameField.getText());
                        insertStatement.setBytes(2, byteArray);
                        insertStatement.setString(3, nicknameField.getText());
                        insertStatement.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Image and data saved successfully!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    imageLabel.setIcon(new ImageIcon(image));
                    frame.pack();
                }
            }
        });
    }
}
