package filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        String loginURI = httpRequest.getContextPath() + "/login";
        String registrationURI = httpRequest.getContextPath() + "/register";
        String loginHtmlURI = httpRequest.getContextPath() + "/login.html";
        String registrationHtmlURI = httpRequest.getContextPath() + "/register.html";

        boolean loggedIn = (session != null && session.getAttribute("username") != null);
        boolean isLoginOrRegistrationRequest =
                httpRequest.getRequestURI().equals(loginURI) ||
                        httpRequest.getRequestURI().equals(loginHtmlURI) ||
                        httpRequest.getRequestURI().equals(registrationURI) ||
                        httpRequest.getRequestURI().equals(registrationHtmlURI);

        LOGGER.info("Request URI: " + httpRequest.getRequestURI());
        LOGGER.info("User  logged in: " + loggedIn);

        if (loggedIn || isLoginOrRegistrationRequest) {
            LOGGER.info(loggedIn ? "User  is logged in, proceeding with the request." :
                    "Request for login or registration page, proceeding with the request.");
            chain.doFilter(request, response);
        } else {
            LOGGER.warning("User  not logged in, redirecting to login page.");
            httpResponse.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("AuthFilter destroyed");
    }
}