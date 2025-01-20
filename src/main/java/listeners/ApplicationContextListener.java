package listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;
import repository.UserManager;
import servlets.LoginServlet;
import servlets.RegistrationServlet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static utils.IConstantsPath.LOG_FILE_PATH_APPLICATION;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logToFile("===================================================");
        logToFile("APPLICATION STARTUP REPORT");
        logToFile("===================================================");
        logToFile("Application start time: " + LocalDateTime.now().format(FORMATTER));

        initializeUserManager();
        performStartupTasks();
        logServletInformation();

        logToFile("===================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logToFile("===================================================");
        logToFile("APPLICATION SHUTDOWN REPORT");
        logToFile("===================================================");
        logToFile("Application shutdown time: " + LocalDateTime.now().format(FORMATTER));

        performShutdownTasks();

        logToFile("===================================================");
    }

    private void initializeUserManager() {
        try {
            UserManager userManager = UserManager.getInstance();
            logToFile("Total registered users: " + userManager.users.size());
        } catch (Exception e) {
            logToFile("Error initializing UserManager: " + e.getMessage());
        }
    }

    private void performStartupTasks() {
        logToFile("Performing startup tasks...");
        validateApplicationData();
        logSystemInformation();
    }

    private void validateApplicationData() {
        try {
            UserManager userManager = UserManager.getInstance();
            long invalidUsers = userManager.users.values().stream()
                    .filter(user -> user.getUsername() == null ||
                            user.getEmail() == null ||
                            user.getPassword() == null)
                    .count();

            if (invalidUsers > 0) {
                logToFile("Found " + invalidUsers + " invalid user records");
            } else {
                logToFile("All user records are valid");
            }
        } catch (Exception e) {
            logToFile("Data validation error: " + e.getMessage());
        }
    }

    private void logSystemInformation() {
        logToFile("System Information:");
        logToFile("Java Version: " + System.getProperty("java.version"));
        logToFile("OS Name: " + System.getProperty("os.name"));
        logToFile("User Directory: " + System.getProperty("user.dir"));
        logToFile("Available Processors: " + Runtime.getRuntime().availableProcessors());
        logToFile("Total Memory: " + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " MB");
        logToFile("Free Memory: " + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " MB");
    }

    private void logServletInformation() {
        logToFile("Servlet Configuration:");

        // Информация о сервлетах
        logServletDetails("Login Servlet", LoginServlet.class);
        logServletDetails("Registration Servlet", RegistrationServlet.class);
    }

    private void logServletDetails(String servletName, Class<?> servletClass) {
        logToFile("--- " + servletName + " ---");
        logToFile("Class: " + servletClass.getName());
        logToFile("Annotations:");

        // Логирование аннотаций сервлета
        if (servletClass.isAnnotationPresent(WebServlet.class)) {
            WebServlet annotation = servletClass.getAnnotation(WebServlet.class);
            logToFile("  URL Patterns: " + String.join(", ", annotation.value()));
        }
    }

    private void performShutdownTasks() {
        try {
            UserManager userManager = UserManager.getInstance();
            logToFile("Users at shutdown: " + userManager.users.size());
            userManager.saveUsersToFile();

            logToFile("Application shutdown sequence completed");
        } catch (Exception e) {
            logToFile("Error during application shutdown: " + e.getMessage());
        }
    }

    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH_APPLICATION, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}