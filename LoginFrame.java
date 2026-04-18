package attendance.gui;

import attendance.database.Database;
import attendance.models.User;
import attendance.services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private AuthService authService;
    private Database database;

    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    public LoginFrame(Database database) {
        this.database = database;
        this.authService = new AuthService(database);

        setTitle("Student Attendance Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        panel.add(roleBox);

        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(this::handleLogin);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> System.exit(0));

        panel.add(loginBtn);
        panel.add(cancelBtn);

        add(new JLabel("Welcome to Attendance System", SwingConstants.CENTER), BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void handleLogin(ActionEvent e) {
        String role = (String) roleBox.getSelectedItem();
        String username = userField.getText();
        String password = new String(passField.getPassword());

        User user = authService.login(username, password, role);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            this.dispose();
            openDashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            switch (user.getRole()) {
                case "Admin":
                    new AdminDashboard(database, user).setVisible(true);
                    break;
                case "Teacher":
                    new TeacherDashboard(database, user).setVisible(true);
                    break;
                case "Student":
                    new StudentDashboard(database, user).setVisible(true);
                    break;
            }
        });
    }
}
