package AttendMe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Second extends JFrame implements ActionListener{
    JButton exist,back,create;
    Second(){
        exist = new JButton("EXIST");
        exist.setBounds(350,170,150,40);
        exist.setBackground(new Color(255, 255, 255));
        exist.setForeground(Color.WHITE);
        exist.addActionListener(this);
        exist.setBorderPainted(false);
        exist.setContentAreaFilled(false);
        exist.setFont(new Font("Arial Rounded MT Bold",Font.PLAIN,40));
        add(exist);

        back = new JButton("BACK");
        back.setBounds(75,218,170,40);
        back.setBackground(new Color(255, 255, 255));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFont(new Font("Arial Rounded MT Bold",Font.PLAIN,30));
        add(back);

        create = new JButton("CREATE");
        create.setBounds(340,275,170,40);
        create.setBackground(new Color(255, 255, 255));
        create.setForeground(Color.WHITE);
        create.addActionListener(this);
        create.setBorderPainted(false);
        create.setContentAreaFilled(false);
        create.setFont(new Font("Arial Rounded MT Bold",Font.PLAIN,25));
        add(create);

        ImageIcon i1 = null;
        ImageIcon image = new ImageIcon("C:\\Users\\User\\IdeaProjects\\facecheck\\src\\02.jpg");


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
        if (e.getSource() == exist) {
            setVisible(false);

            new loginmodule();
        }
        else if(e.getSource()==back){
            setVisible(false);
            new Intro();
        }
        else if(e.getSource()==create){
            setVisible(false);
            new GUI();
        }
    }

    public static void main(String[]args) {
        new Second();
    }
}