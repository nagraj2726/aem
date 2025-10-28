package com.aem.geeks.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Random;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_PATHS + "=/bin/simple-random"
    }
)
public class SimpleRandomServlet extends SlingAllMethodsServlet {

    Random random = new Random();

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String output = "";

        if (type == null) {
            output = "Please pass ?type=Number | Letters | Random";
        } else {
            switch (type.toLowerCase()) {
                case "number":
                    output = getRandomNumbers(6);
                    break;
                case "letters":
                    output = getRandomLetters(6);
                    break;
                case "random":
                    output = getRandomNumbers(3) + getRandomLetters(3);
                    break;
                default:
                    output = "Invalid type. Use Number, Letters, or Random";
            }
        }

        response.setContentType("text/plain");
        response.getWriter().write(output);
    }

    private String getRandomNumbers(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(random.nextInt(10)); // 0-9
        }
        return sb.toString();
    }

    private String getRandomLetters(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append((char) ('A' + random.nextInt(26))); // A-Z
        }
        return sb.toString();
    }
}