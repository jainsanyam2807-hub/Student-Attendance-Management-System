package attendance.gui;

import attendance.database.Database;
import attendance.models.AttendanceRecord;
import attendance.models.User;
import attendance.services.ReportGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class StudentDashboard extends JFrame {
    private Database database;
    private User student;
    private ReportGenerator reportGenerator;
    private JTable attendanceTable;

    public StudentDashboard(Database database, User student) {
        this.database = database;
        this.student = student;
        this.reportGenerator = new ReportGenerator(database);

        setTitle("Student Dashboard - " + student.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.add(new JLabel("Welcome, " + student.getName() + " (" + student.getId() + ")"), BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame(database).setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        double percentage = reportGenerator.getStudentAttendancePercentage(student.getId());
        JLabel summaryLabel = new JLabel(String.format("Overall Attendance: %.2f%%", percentage));
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(summaryLabel, BorderLayout.NORTH);

        String[] columnNames = {"Date", "Class ID", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        attendanceTable = new JTable(model);
        
        List<AttendanceRecord> records = database.getAllAttendance().stream()
            .filter(r -> r.getStudentId().equals(student.getId()))
            .collect(Collectors.toList());
            
        for (AttendanceRecord r : records) {
            model.addRow(new Object[]{r.getDate(), r.getClassId(), r.getStatus()});
        }
        
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        return panel;
    }
}
