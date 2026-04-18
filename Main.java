package attendance;

import attendance.database.Database;
import attendance.database.FileDatabase;
import attendance.gui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Database database = new FileDatabase();
        database.initialize();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame(database).setVisible(true);
        });
    }
}
