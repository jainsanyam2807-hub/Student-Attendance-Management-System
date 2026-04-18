package attendance.database;

import attendance.models.AttendanceRecord;
import attendance.models.CourseClass;
import attendance.models.User;
import java.util.List;

public interface Database {
    void initialize();
    List<User> getAllUsers();
    void saveUser(User user);
    void deleteUser(String id);
    
    List<CourseClass> getAllClasses();
    void saveClass(CourseClass courseClass);
    void deleteClass(String id);
    
    List<AttendanceRecord> getAllAttendance();
    void saveAttendance(List<AttendanceRecord> records);
}
