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

public class loginmodule {

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

                        // Compare the image with pre-existing images in the database
                        String selectAllQuery = "SELECT * FROM ran";
                        PreparedStatement selectAllStatement = con.prepareStatement(selectAllQuery);
                        ResultSet allResultSet = selectAllStatement.executeQuery();

                        while (allResultSet.next()) {
                            byte[] dbByteArray = allResultSet.getBytes("image");
                            String name = allResultSet.getString("name");
                            String RollNo = allResultSet.getString("RollNo");
                            double matchPercentage = compareImages(byteArray, dbByteArray);
                            System.out.println("Match percentage with " + name + " : " + matchPercentage);
                            if (matchPercentage >= 42) {

                                JOptionPane.showMessageDialog(frame, "ATTENDENCE MARKED,You are  " + name + " (" + RollNo + ") with a match percentage of " + matchPercentage);
                                return;
                            }
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    imageLabel.setIcon(new ImageIcon(image));
                    frame.pack();
                }
            }
        });
    }

    /**
     * Compare two images and return the match percentage
     * @param image1
     * @param image2
     * @return the match percentage
     */
    private static double compareImages(byte[] image1, byte[] image2) {
        try {
            Mat mat1 = Imgcodecs.imdecode(new MatOfByte(image1), Imgcodecs.IMREAD_GRAYSCALE);
            Mat mat2 = Imgcodecs.imdecode(new MatOfByte(image2), Imgcodecs.IMREAD_GRAYSCALE);
            Mat diff = new Mat();
            Core.absdiff(mat1, mat2, diff);
            Mat thresh = new Mat();
            Imgproc.threshold(diff, thresh, 20, 255, Imgproc.THRESH_BINARY);
            double nonZero = Core.countNonZero(thresh);
            double total = mat1.rows() * mat1.cols();
            double matchPercentage = ((total - nonZero) / total) * 100;
            return matchPercentage;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}