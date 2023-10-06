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
import java.sql.ResultSet;

public class lo {

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

                        // Compare the captured image with each image in the database
                        String query = "SELECT * FROM images";
                        PreparedStatement stmt = con.prepareStatement(query);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            // Read the image from the database
                            byte[] imgData = rs.getBytes("image");
                            Mat imgMat = Imgcodecs.imdecode(new MatOfByte(imgData), Imgcodecs.IMREAD_COLOR);

                            // Check if the captured image matches the database image using template matching
                            Mat result = new Mat();
                            Imgproc.matchTemplate(frameMat, imgMat, result, Imgproc.TM_CCOEFF_NORMED);
                            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                            double matchPercent = mmr.maxVal * 100;

                            // If the captured image matches the database image, display the name and nickname of the person
                            if (matchPercent >= 30) {
                                String name = rs.getString("name");
                                String nickname = rs.getString("RollNo");
                                JOptionPane.showMessageDialog(null, "Match found! This is " + name + ", also known as " + nickname);
                                break;
                            }
                        }
                        // Close the SQL database connection
                        con.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }}
