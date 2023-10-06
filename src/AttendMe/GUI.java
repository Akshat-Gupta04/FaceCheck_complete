package AttendMe;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GUI {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Declare the Connection variable outside the main method
    private static Connection con;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Camera Capture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.YELLOW);
        frame.add(panel, BorderLayout.CENTER);

        JLabel imageLabel = new JLabel();
        panel.add(imageLabel);

        JButton captureButton = new JButton("Capture");
        panel.add(captureButton);
        JTextField nameField = new JTextField(20);
        Border border = BorderFactory.createTitledBorder("Enter your name:");
        nameField.setBorder(border);
        panel.add(nameField);

        JTextField nicknameField = new JTextField(20);
        Border border1 = BorderFactory.createTitledBorder("Enrollment No:");
        nicknameField.setBorder(border1);
        panel.add(nicknameField);

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a new thread that continuously updates the camera capture and displays it in the GUI
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                            imageLabel.setIcon(new ImageIcon(image));
                            frame.pack();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
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

                        // Check if the image already exists in the database
                        String selectQuery = "SELECT COUNT(*) FROM ran WHERE image = ?";
                        PreparedStatement selectStatement = con.prepareStatement(selectQuery);
                        selectStatement.setBytes(1, byteArray);
                        ResultSet resultSet = selectStatement.executeQuery();
                        resultSet.next();
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            JOptionPane.showMessageDialog(frame, "This image already exists in the database!");
                            return;
                        }

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

