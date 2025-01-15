package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);

            if (session != null) {
                String username = (String) session.getAttribute("username");
                LOGGER.info("The user logs out of the system -> " + username);
                session.invalidate();
                LOGGER.info("User session " + username + " completed successfully");
            } else {
                LOGGER.warning("Attempt to log out without an active session");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.sendRedirect("login.html");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when logging out", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.sendRedirect("error.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}