package attendance.database;

import attendance.models.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileDatabase implements Database {
    private static final String USERS_FILE = "users.csv";
    private static final String CLASSES_FILE = "classes.csv";
    private static final String ATTENDANCE_FILE = "attendance.csv";

    @Override
    public void initialize() {
        createFileIfNotExists(USERS_FILE, "ID,Username,Password,Role,Name");
        createFileIfNotExists(CLASSES_FILE, "ClassID,ClassName");
        createFileIfNotExists(ATTENDANCE_FILE, "Date,StudentID,ClassID,Status");

        if (getAllUsers().isEmpty()) {
            saveUser(new Admin("A01", "admin", "admin123", "Super Admin"));
        }
    }

    private void createFileIfNotExists(String filename, String header) {
        File file = new File(filename);
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println(header);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String role = parts[3];
                    String name = parts[4];

                    switch (role) {
                        case "Admin": users.add(new Admin(id, username, password, name)); break;
                        case "Teacher": users.add(new Teacher(id, username, password, name)); break;
                        case "Student": users.add(new Student(id, username, password, name)); break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void saveUser(User user) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            pw.println(String.join(",", user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(String id) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getId().equals(id));
        rewriteUsers(users);
    }
    
    private void rewriteUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            pw.println("ID,Username,Password,Role,Name");
            for (User u : users) {
                pw.println(String.join(",", u.getId(), u.getUsername(), u.getPassword(), u.getRole(), u.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CourseClass> getAllClasses() {
        List<CourseClass> classes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CLASSES_FILE))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                 if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    classes.add(new CourseClass(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    @Override
    public void saveClass(CourseClass courseClass) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CLASSES_FILE, true))) {
            pw.println(String.join(",", courseClass.getClassId(), courseClass.getClassName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteClass(String id) {
        List<CourseClass> classes = getAllClasses();
        classes.removeIf(c -> c.getClassId().equals(id));
        try (PrintWriter pw = new PrintWriter(new FileWriter(CLASSES_FILE))) {
            pw.println("ClassID,ClassName");
            for (CourseClass c : classes) {
                pw.println(String.join(",", c.getClassId(), c.getClassName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AttendanceRecord> getAllAttendance() {
        List<AttendanceRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    records.add(new AttendanceRecord(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public void saveAttendance(List<AttendanceRecord> records) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_FILE, true))) {
            for (AttendanceRecord record : records) {
                pw.println(String.join(",", record.getDate(), record.getStudentId(), record.getClassId(), record.getStatus()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
