package es.qopuir.idealistabot;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Bean
    public Filter shallowEtagHeaderFilter(BotProperties botProperties) {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                if (request.getMethod().equalsIgnoreCase(RequestMethod.POST.name())
                        && request.getRequestURI().endsWith(BotController.IDEALISTABOT_URL)) {
                    if (!request.getRequestURI().startsWith("/" + botProperties.getApiKey())) {
                        LOG.info("Rejected request '{}': api-key is missing", BotController.IDEALISTABOT_URL);
                        return;
                    } else {
                        String newUrl = request.getRequestURI().substring(botProperties.getApiKey().length() + 1);

                        LOG.debug("Redirecting request '{}' to {}", request.getRequestURI(), newUrl);

                        RequestDispatcher requestDispatcher = request.getRequestDispatcher(newUrl);

                        requestDispatcher.forward(request, response);
                    }
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}