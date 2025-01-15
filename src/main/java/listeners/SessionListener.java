package listeners;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static utils.IConstantsPath.LOG_FILE_PATH_SESSION;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String logMessage = "The session has been created -> " + se.getSession().getId() + " в " + LocalDateTime.now();
        logToFile(logMessage);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String logMessage = "The session is destroyed -> " + se.getSession().getId() + " в " + LocalDateTime.now();
        logToFile(logMessage);
    }

    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH_SESSION, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}