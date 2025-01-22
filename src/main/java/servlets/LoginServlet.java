package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.impl.UserManagerRepository;
import model.User;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private final UserManagerRepository userManager = UserManagerRepository.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        LOGGER.info("Login Attempt -> username=" + username);

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            LOGGER.warning("Empty credentials");
            response.sendRedirect("login.html?error=empty");
            return;
        }

        try {
            User user = userManager.getUser (username);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("username", username);

                    LOGGER.info("Successful login -> " + username);
                    response.sendRedirect("profile.html");
                } else {
                    LOGGER.warning("Invalid password for -> " + username);
                    response.sendRedirect("login.html?error=invalid");
                }
            } else {
                LOGGER.warning("The user was not found -> " + username);
                response.sendRedirect("login.html?error=notfound");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when logging in", e);
            response.sendRedirect("login.html?error=system");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("login.html");
    }
}