package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.UserManager;
import model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private final UserManager userManager = UserManager.getInstance();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        LOGGER.info("Login Attempt -> username=" + username);
        if (username == null || password == null) {
            LOGGER.warning("Empty credentials");
            response.sendRedirect("login.html?error=" +
                    URLEncoder.encode("Enter your username and password", StandardCharsets.UTF_8));
            return;
        }

        try {
            User user = userManager.getUser(username);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("username", username);

                    LOGGER.info("Successful login -> " + username);
                    response.sendRedirect("success.html?username=" +
                            URLEncoder.encode(username, StandardCharsets.UTF_8));
                } else {
                    LOGGER.warning("Invalid password for -> " + username);
                    response.sendRedirect("login.html?error=" +
                            URLEncoder.encode("Invalid password", StandardCharsets.UTF_8));
                }
            } else {
                LOGGER.warning("The user was not found -> " + username);
                response.sendRedirect("login.html?error=" +
                        URLEncoder.encode("The user was not found", StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when logging in", e);
            response.sendRedirect("login.html?error=" +
                    URLEncoder.encode("System error", StandardCharsets.UTF_8));
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}