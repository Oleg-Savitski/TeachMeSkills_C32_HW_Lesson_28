package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import repository.impl.GoalManagerRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/goals")
public class GoalsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GoalsServlet.class.getName());
    private GoalManagerRepository goalsRepository;

    @Override
    public void init() {
        goalsRepository = new GoalManagerRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            LOGGER.warning("Unauthorized access!!!");
            response.sendRedirect("login.html");
            return;
        }
        String action = request.getParameter("action");
        if ("load".equals(action)) {
            loadGoals(request, response);
        } else {
            request.getRequestDispatcher("/goals.html").forward(request, response);
        }
    }

    private void loadGoals(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            LOGGER.warning("Unauthorized access!!!");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The user is not logged in");
            return;
        }

        String username = (String) session.getAttribute("username");
        List<String> goalStrings = goalsRepository.loadGoals(username);
        LOGGER.info("Uploaded goals for " + username + ": " + goalStrings.size());
        goalStrings.forEach(goal -> LOGGER.info("Goal -> " + goal));
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        for (String goal : goalStrings) {
            response.getWriter().println(goal);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            LOGGER.warning("Unauthorized access!!!");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The user is not logged in");
            return;
        }

        String username = (String) session.getAttribute("username");
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line).append("\n");
            }
        }

        String goalsContent = requestBody.toString().trim();
        List<String> goals = Arrays.stream(goalsContent.split("\n"))
                .map(String::trim)
                .filter(goal -> !goal.isEmpty())
                .collect(Collectors.toList());

        List<String> processedGoals = new ArrayList<>();
        for (String goal : goals) {
            if (goal.contains("[completed:")) {
                processedGoals
                        .add(goal.replaceAll("\\[completed:.+?\\]", "")
                        .trim() + " [completed:" + LocalDateTime
                        .now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]");
            } else {
                processedGoals.add(goal);
            }
        }

        goalsRepository.saveGoals(username, processedGoals);

        LOGGER.info("Saved goals for the user -> " + username);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}