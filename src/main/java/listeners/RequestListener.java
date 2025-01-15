package listeners;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static utils.IConstantsPath.LOG_FILE_PATH_REQUEST;

@WebListener
public class RequestListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        String logMessage = "The request has been initialized -> " + sre.getServletRequest().getRemoteAddr() + " в " + LocalDateTime.now();
        logToFile(logMessage);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        String logMessage = "The request is destroyed -> " + sre.getServletRequest().getRemoteAddr() + " в " + LocalDateTime.now();
        logToFile(logMessage);
    }

    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH_REQUEST, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}