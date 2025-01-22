package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProfileServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            LOGGER.info("Accessing profile for user -> " + username);
            response.sendRedirect("profile.html?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));
        } else {
            LOGGER.warning("User  not logged in, redirecting to login page.");
            response.sendRedirect("login.html");
        }
    }
}