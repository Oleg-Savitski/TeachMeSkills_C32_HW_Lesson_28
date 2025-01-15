package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.UserManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegistrationServlet.class.getName());
    private final UserManager userManager = UserManager.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String country = request.getParameter("country");
        String city = request.getParameter("city");
        LOGGER.info("Registration attempt -> username=" + username);
        if (username == null || email == null || password == null ||
                confirmPassword == null || fullName == null ||
                country == null || city == null) {

            LOGGER.warning("Incomplete registration data");
            response.sendRedirect("error.html?message=" +
                    URLEncoder.encode("Fill in all fields", StandardCharsets.UTF_8));
            return;
        }

        try {
            userManager.registerUser(
                    username, email, password, confirmPassword,
                    fullName, country, city
            );

            LOGGER.info("Successful registration -> " + username);
            response.sendRedirect("login.html");

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Registration error", e);
            String errorMessage = URLEncoder.encode(
                    e.getMessage(),
                    StandardCharsets.UTF_8
            );

            response.sendRedirect("error.html?message=" + errorMessage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "System error during registration", e);
            String errorMessage = URLEncoder.encode(
                    "System error",
                    StandardCharsets.UTF_8
            );

            response.sendRedirect("error.html?message=" + errorMessage);
        }
    }
}