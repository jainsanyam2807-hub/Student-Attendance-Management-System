package attendance.gui;

import attendance.database.Database;
import attendance.models.Admin;
import attendance.models.CourseClass;
import attendance.models.Student;
import attendance.models.Teacher;
import attendance.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AdminDashboard extends JFrame {
    private Database database;
    private User admin;
    private JTable userTable;
    private JTable classTable;

    public AdminDashboard(Database database, User admin) {
        this.database = database;
        this.admin = admin;

        setTitle("Admin Dashboard - " + admin.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Users", createUserPanel());
        tabbedPane.addTab("Manage Classes", createClassPanel());

        add(createHeader(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Admin Dashboard - Logged in as: " + admin.getName() + " (" + admin.getId() + ")"), BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame(database).setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"ID", "Username", "Role", "Name"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(model);
        refreshUserTable();
        
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add User");
        JButton delBtn = new JButton("Delete User");
        
        addBtn.addActionListener(this::handleAddUser);
        delBtn.addActionListener(this::handleDeleteUser);

        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createClassPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"Class ID", "Class Name"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        classTable = new JTable(model);
        refreshClassTable();
        
        panel.add(new JScrollPane(classTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add Class");
        JButton delBtn = new JButton("Delete Class");
        
        addBtn.addActionListener(this::handleAddClass);
        delBtn.addActionListener(this::handleDeleteClass);

        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshUserTable() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
        List<User> users = database.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole(), u.getName()});
        }
    }

    private void refreshClassTable() {
        DefaultTableModel model = (DefaultTableModel) classTable.getModel();
        model.setRowCount(0);
        List<CourseClass> classes = database.getAllClasses();
        for (CourseClass c : classes) {
            model.addRow(new Object[]{c.getClassId(), c.getClassName()});
        }
    }

    private void handleAddUser(ActionEvent e) {
        JTextField idField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Teacher", "Student", "Admin"});
        JTextField nameField = new JTextField();

        Object[] message = {
            "ID:", idField,
            "Username:", userField,
            "Password:", passField,
            "Role:", roleBox,
            "Name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String role = (String) roleBox.getSelectedItem();
            User newUser;
            if (role.equals("Admin")) {
                newUser = new Admin(idField.getText(), userField.getText(), new String(passField.getPassword()), nameField.getText());
            } else if (role.equals("Teacher")) {
                newUser = new Teacher(idField.getText(), userField.getText(), new String(passField.getPassword()), nameField.getText());
            } else {
                newUser = new Student(idField.getText(), userField.getText(), new String(passField.getPassword()), nameField.getText());
            }
            database.saveUser(newUser);
            refreshUserTable();
        }
    }

    private void handleDeleteUser(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) userTable.getValueAt(selectedRow, 0);
            if (id.equals(admin.getId())) {
                JOptionPane.showMessageDialog(this, "Cannot delete yourself!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            database.deleteUser(id);
            refreshUserTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void handleAddClass(ActionEvent e) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        Object[] message = {
            "Class ID:", idField,
            "Class Name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Class", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            database.saveClass(new CourseClass(idField.getText(), nameField.getText()));
            refreshClassTable();
        }
    }

    private void handleDeleteClass(ActionEvent e) {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) classTable.getValueAt(selectedRow, 0);
            database.deleteClass(id);
            refreshClassTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a class to delete.");
        }
    }
}
