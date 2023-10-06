import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class NameNicknameGUI extends JFrame implements ActionListener {
    // GUI components
    private JTextField nameField, nicknameField;
    private JButton saveButton;

    // Database connection parameters
    private String url = "jdbc:mysql://localhost:3306/pension_record_database";
    private String username = "root";
    private String password = "MOMDAD@20039m";

    public NameNicknameGUI() {
        // Set up the GUI components
        setTitle("Name and RollNo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));
        add(new JLabel("Name: "));
        nameField = new JTextField();
        add(nameField);
        add(new JLabel("Roll No: "));
        nicknameField = new JTextField();
        add(nicknameField);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        add(saveButton);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            try {
                // Establish a connection to the database
                Connection conn = DriverManager.getConnection(url, username, password);

                // Create a prepared statement
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO tr (Name,Roll) VALUES (?, ?)");

                // Set the parameters
                stmt.setString(1, nameField.getText());
                stmt.setString(2, nicknameField.getText());

                // Execute the statement
                stmt.executeUpdate();

                // Close the statement and connection
                stmt.close();
                conn.close();

                // Show a success message
                JOptionPane.showMessageDialog(this, "Data saved successfully.");
            } catch (SQLException ex) {
                // Show an error message
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        NameNicknameGUI gui = new NameNicknameGUI();
        gui.setVisible(true);
    }
}
