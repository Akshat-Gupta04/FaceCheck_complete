//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.core.Size;
//import org.opencv.features2d.DescriptorMatcher;
//import org.opencv.features2d.FeatureDetector;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
////import org.opencv.features2d.DescriptorExtractor;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class GUI {
//
//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }
//
//    // Declare the Connection variable outside the main method
//    private static Connection con;
//
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Camera Capture");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        JPanel panel = new JPanel();
//        frame.add(panel, BorderLayout.CENTER);
//
//        JLabel imageLabel = new JLabel();
//        panel.add(imageLabel);
//
//        JButton captureButton = new JButton("Capture");
//        panel.add(captureButton);
//        JTextField nameField = new JTextField(20);
//        panel.add(nameField);
//
//        JTextField nicknameField = new JTextField(20);
//        panel.add(nicknameField);
//
//        frame.setSize(640, 480);
//        frame.setVisible(true);
//
//        VideoCapture capture = new VideoCapture(0);
//
//        if (!capture.isOpened()) {
//            System.err.println("Unable to open camera");
//            System.exit(0);
//        }
//
//        Mat frameMat = new Mat();
//        MatOfByte frameBytes = new MatOfByte();
//
//        try {
//            // Establish a connection to the SQL database using JDBC
//            String url = "jdbc:mysql://localhost:3306/facecheck";
//            String username = "root";
//            String password = "MOMDAD@20039m";
//            con = DriverManager.getConnection(url, username, password);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Create a new thread that continuously updates the camera capture and displays it in the GUI
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    capture.read(frameMat);
//                    if (!frameMat.empty()) {
//                        Imgproc.resize(frameMat, frameMat, new Size(640, 480));
//                        Imgcodecs.imencode(".jpg", frameMat, frameBytes);
//                        byte[] byteArray = frameBytes.toArray();
//                        BufferedImage image = null;
//                        try {
//                            InputStream in = new ByteArrayInputStream(byteArray);
//                            image = ImageIO.read(in);
//                            imageLabel.setIcon(new ImageIcon(image));
//                            frame.pack();
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//        thread.start();
//        captureButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                capture.read(frameMat);
//                if (!frameMat.empty()) {
//                    Mat resizedMat = new Mat();
//                    Imgproc.resize(frameMat, resizedMat, new Size(640, 480));
//                    Mat grayMat = new Mat();
//                    Imgproc.cvtColor(resizedMat, grayMat, Imgproc.COLOR_BGR2GRAY);
//                    // Use OpenCV feature detection and matching to compare captured image with existing images in the database
//                    FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
//                    DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//                    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//
//                    // Create key points and descriptors for captured image
//                    MatOfKeyPoint keypoints = new MatOfKeyPoint();
//                    Mat descriptors = new Mat();
//                    detector.detect(grayMat, keypoints);
//                    extractor.compute(grayMat, keypoints, descriptors);
//
//                    // Query the database for all images and compare them with the captured image using feature matching
//                    try {
//                        PreparedStatement statement = con.prepareStatement("SELECT id, name, nickname, image FROM images");
//                        ResultSet rs = statement.executeQuery();
//                        while (rs.next()) {
//                            int id = rs.getInt("id");
//                            String name = rs.getString("name");
//                            String nickname = rs.getString("nickname");
//                            byte[] imageBytes = rs.getBytes("image");
//                            Mat dbMat = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_GRAYSCALE);
//                            MatOfKeyPoint dbKeypoints = new MatOfKeyPoint();
//                            Mat dbDescriptors = new Mat();
//                            detector.detect(dbMat, dbKeypoints);
//                            extractor.compute(dbMat, dbKeypoints, dbDescriptors);
//                            MatOfDMatch matches = new MatOfDMatch();
//                            matcher.match(descriptors, dbDescriptors, matches);
//                            if (matches.rows() > 0 && matches.rows() / (double) keypoints.rows() >= 0.3) {
//                                JOptionPane.showMessageDialog(null, "Match found! " + name + " (" + nickname + ")");
//                                break;
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//
//                    // Insert the captured image into the database
//                    try {
//                        String name = nameField.getText();
//                        String nickname = nicknameField.getText();
//                        if (!name.isEmpty() && !nickname.isEmpty()) {
//                            PreparedStatement statement = con.prepareStatement("INSERT INTO images (name, nickname, image) VALUES (?, ?, ?)");
//                            statement.setString(1, name);
//                            statement.setString(2, nickname);
//                            statement.setBytes(3, byteArray);
//                            statement.executeUpdate();
//                            JOptionPane.showMessageDialog(null, "Image saved to database");
//                        } else {
//                            JOptionPane.showMessageDialog(null, "Please enter a name and nickname");
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
