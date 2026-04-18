package attendance.gui;

import attendance.database.Database;
import attendance.models.AttendanceRecord;
import attendance.models.CourseClass;
import attendance.models.Student;
import attendance.models.User;
import attendance.services.AttendanceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private Database database;
    private User teacher;
    private AttendanceManager attendanceManager;
    private JTable studentTable;
    private JComboBox<CourseClass> classBox;
    private JTextField dateField;

    public TeacherDashboard(Database database, User teacher) {
        this.database = database;
        this.teacher = teacher;
        this.attendanceManager = new AttendanceManager(database);

        setTitle("Teacher Dashboard - " + teacher.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Teacher Dashboard - Logged in as: " + teacher.getName()), BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame(database).setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        controls.add(dateField);
        
        controls.add(new JLabel("Class:"));
        classBox = new JComboBox<>();
        for (CourseClass c : database.getAllClasses()) {
            classBox.addItem(c);
        }
        controls.add(classBox);
        
        JButton fetchBtn = new JButton("Fetch Students");
        fetchBtn.addActionListener(e -> refreshStudentTable());
        controls.add(fetchBtn);
        
        panel.add(controls, BorderLayout.NORTH);

        String[] columnNames = {"Student ID", "Name", "Status (Present/Absent)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        studentTable = new JTable(model);
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent"});
        studentTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));

        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save Attendance");
        saveBtn.addActionListener(this::handleSaveAttendance);
        btnPanel.add(saveBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshStudentTable() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);
        
        List<Student> students = attendanceManager.getStudents();
        for (Student s : students) {
            model.addRow(new Object[]{s.getId(), s.getName(), "Present"});
        }
    }

    private void handleSaveAttendance(ActionEvent e) {
        if (studentTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No students to save.");
            return;
        }

        CourseClass selectedClass = (CourseClass) classBox.getSelectedItem();
        String date = dateField.getText();
        
        if (selectedClass == null || date.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Class and Date are required.");
            return;
        }
        
        List<AttendanceRecord> existing = attendanceManager.getRecordsForClass(selectedClass.getClassId(), date);
        if (!existing.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Attendance for this class on this date is already recorded!");
            return;
        }

        List<AttendanceRecord> records = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String studentId = (String) model.getValueAt(i, 0);
            String status = (String) model.getValueAt(i, 2);
            records.add(new AttendanceRecord(date, studentId, selectedClass.getClassId(), status));
        }

        attendanceManager.markAttendance(records);
        JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
    }
}
