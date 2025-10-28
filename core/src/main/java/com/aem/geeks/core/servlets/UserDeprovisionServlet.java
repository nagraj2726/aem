package com.aem.geeks.core.servlets;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "=AEM User Deprovisioning Servlet",
        "sling.servlet.paths=/bin/deprovisionUser",
        "sling.servlet.methods=POST"
    }
)
public class UserDeprovisionServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String userId = request.getParameter("userId");

        if (userId == null || userId.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Missing userId parameter\"}");
            return;
        }

        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        try {
            UserManager userManager = resolver.adaptTo(UserManager.class);
            Authorizable authorizable = userManager.getAuthorizable(userId);

            if (authorizable == null) {
                response.setStatus(404);
                response.getWriter().write("{\"error\": \"User not found\"}");
                return;
            }

            if (!(authorizable instanceof User)) {
                response.setStatus(400);
                response.getWriter().write("{\"error\": \"Authorizable is not a user\"}");
                return;
            }

            
            authorizable.remove();
            session.save();

            response.getWriter().write("{\"status\": \"User deleted successfully\", \"userId\": \"" + userId + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}