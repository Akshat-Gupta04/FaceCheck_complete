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

public class finala {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Declare the Connection variable outside the main method
    private static Connection con;
    /**
     * Calculate the hash of an image
     * @param mat The OpenCV Mat representation of the image
     * @return An int array representing the hash of the image
     */
    private static int[] calculateImageHash (Mat mat){
        Mat resizedMat = new Mat();
        Imgproc.resize(mat, resizedMat, new Size(8, 8));
        Mat grayMat = new Mat();
        Imgproc.cvtColor(resizedMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        int[] hash = new int[64];
        int index = 0;
        for (int i = 0; i < grayMat.rows(); i++) {
            for (int j = 0; j < grayMat.cols(); j++) {
                double[] pixel = grayMat.get(i, j);
                hash[index] = (int) pixel[0];
                index++;
            }
        }
        return hash;
    }

    /**
     * Calculate the similarity between two image hashes
     * @param hash1 The first image hash
     * @param hash2 The second image hash
     * @return A double value representing the similarity between the two hashes
     */
    private static double calculateSimilarity ( int[] hash1, int[] hash2){
        int numMatches = 0;
        for (int i = 0; i < hash1.length; i++) {
            if (hash1[i] == hash2[i]) {
                numMatches++;
            }
        }
        return (double) numMatches / (double) hash1.length;
    }

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

                        // Calculate the hash of the captured image
                        int[] captureHash = calculateImageHash(frameMat);

                        // Query the database to get all the saved image hashes
                        String selectQuery = "SELECT RollNo, image FROM ran";
                        PreparedStatement selectStatement = con.prepareStatement(selectQuery);
                        ResultSet resultSet = selectStatement.executeQuery();

                        while (resultSet.next()) {
                            String id = resultSet.getString("RollNo");

                            byte[] savedImageBytes = resultSet.getBytes("image");

                            // Calculate the hash of the saved image
                            Mat savedImageMat = Imgcodecs.imdecode(new MatOfByte(savedImageBytes), Imgcodecs.IMREAD_GRAYSCALE);
                            int[] savedHash = calculateImageHash(savedImageMat);

                            // Compare the two hashes to see if they match
                            double similarity = calculateSimilarity(captureHash, savedHash);
                            if (similarity >= 0.3) {
                                JOptionPane.showMessageDialog(frame, "This image matches with the database record (ID: " + id + ") with similarity score: " + similarity);
                            } else {
                                JOptionPane.showMessageDialog(frame, "This image does not match with any record in the database.");
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


    }}
