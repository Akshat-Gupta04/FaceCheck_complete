import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

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
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.sql.ResultSet;


public class aa {


    static {
        //      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Declare the Connection variable outside the main method
    private static Connection con;

    public static void main(String[] args) {
        //Title
        JFrame frame = new JFrame("Camera Capture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);

        JLabel imageLabel = new JLabel();
        panel.add(imageLabel);

        JButton captureButton = new JButton("Capture");
        captureButton.setBounds(46,98,52,20);
        panel.add(captureButton);
        JTextField nameField = new JTextField(20);
        panel.add(nameField);

        JTextField nicknameField = new JTextField(20);
        panel.add(nicknameField);

        // Create a JPanel and set its layout to null
        JPanel panel1 = new JPanel();
        panel1.setLayout(null);

        JLabel Field = new JLabel("Enter Your Name");
        Field.setBounds(380,110,200,50);
        Field.setForeground(Color.BLACK);
        Field.setFont(new Font("Arial",Font.BOLD,20));
        panel1.add(Field);

        JTextField nameField1 = new JTextField("");
        nameField1.setBounds(357,157,210,30);
        nameField1.setOpaque(false);
        nameField1.setForeground(Color.WHITE);
        nameField1.setFont(new Font("Arial",Font.BOLD,20));
        panel1.add(nameField1);


        JLabel name = new JLabel("Enrollment Number");
        name.setBounds(370,210,200,50);
        name.setForeground(Color.BLACK);
        name.setFont(new Font("Arial",Font.BOLD,20));
        panel1.add(name);


        JTextField nicknameField1 = new JTextField("");
        nicknameField1.setBounds(390,251,143,29);
        nicknameField1.setOpaque(false);
        nicknameField1.setForeground(Color.WHITE);
        nicknameField1.setFont(new Font("Arial",Font.BOLD,20));
        panel1.add(nicknameField1);


        JButton captureButton1 = new JButton("CAPTURE");
        captureButton1.setBounds(448,393,150,40);
        captureButton1.setBackground(new Color(255, 255, 255));
        captureButton1.setForeground(Color.WHITE);
        //captureButton.addActionListener(this);
        captureButton1.setBorderPainted(false);
        captureButton1.setContentAreaFilled(false);
        captureButton1.setFont(new Font("Arial",Font.BOLD,15));
        panel1.add(captureButton1);

        JButton backButton = new JButton("BACK");
        backButton.setBounds(45,393,150,40);
        backButton.setBackground(new Color(255, 255, 255));
        backButton.setForeground(Color.WHITE);
        //backButton.addActionListener(this);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFont(new Font("Arial",Font.BOLD,15));
        panel1.add(backButton);

        // Load the background image
        ImageIcon image = new ImageIcon("C:\\Users\\ravip\\IdeaProjects\\Ravi\\src\\Images\\Create.jpg");



        // Create a JLabel to display the background image
        JLabel background = new JLabel("", image, JLabel.CENTER);
        background.setBounds(0, 0, 640, 480);

        // Add the JLabel to the panel
        panel.add(background);

        // Create a JFrame and add the panel to it
        JFrame frame1 = new JFrame("Face Check");
        frame1.setContentPane(panel);
        frame1.setSize(640, 480);
        VideoCapture capture = new VideoCapture(0);
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




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

                        // Insert the image data, name, and nickname into the SQL database
                        String insertQuery = "INSERT INTO ran (name, image,RollNo) VALUES (?, ?, ?)";
                        PreparedStatement insertStatement = con.prepareStatement(insertQuery);
                        insertStatement.setString(1, nameField1.getText());
                        insertStatement.setBytes(2, byteArray);
                        insertStatement.setString(3, nicknameField1.getText());
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