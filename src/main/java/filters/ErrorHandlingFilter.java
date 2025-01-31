package filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("/*")
public class ErrorHandlingFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandlingFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("ErrorHandlingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            LOGGER.severe("Error occurred: " + e.getMessage());
            request.getRequestDispatcher("/error.html").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("ErrorHandlingFilter destroyed");
    }
}