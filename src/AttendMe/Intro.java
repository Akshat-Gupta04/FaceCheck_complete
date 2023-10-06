package AttendMe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Intro extends JFrame implements ActionListener{
    JButton but;
    Intro(){
        but = new JButton("NEXT");
        but.setBounds(478,400,90,25);
        but.setBackground(new Color(0, 0, 0, 0));
        // but.setForeground(Color.BLUE);
        but.addActionListener(this);
        but.setBorderPainted(false);
        but.setContentAreaFilled(false);
        but.setOpaque(false);
        //but.setOpaque(true);
        but.setFont(new Font("Arial",Font.BOLD,20));
        but.setForeground(new Color(255,255,255));
        add(but);
        ImageIcon image = new ImageIcon("C:\\Users\\User\\IdeaProjects\\facecheck\\src\\Intro.jpg");


        // Create a JLabel to display the background image
        JLabel background = new JLabel("", image, JLabel.CENTER);
        background.setBounds(-2, 0, 640, 480);

        add(background);



//        getContentPane().setBackground(Color.BLACK);//Setting the Background color.
        setLayout(null);
        setTitle("Face Check");//Sets Title
        setSize(640,480);
        setLocation(350,160);//Setting the Location of the Frame.
        setVisible(true);//Frame is not visible by Default.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);



    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==but){
            setVisible(false);
            new Second();
        }
    }
    public static void main(String[] args){
        new Intro();
    }
}