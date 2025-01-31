package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@WebServlet("/getUsername")
public class GetUsernameServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GetUsernameServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        LOGGER.info("Received request for username");

        if (session != null) {
            LOGGER.info("Session ID: " + session.getId());
            if (username != null) {
                LOGGER.info("Username found in session: " + username);
            } else {
                LOGGER.warning("No username found in session");
            }
        } else {
            LOGGER.warning("No active session found");
        }

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print(username != null ? username : "Гость");
    }
}
