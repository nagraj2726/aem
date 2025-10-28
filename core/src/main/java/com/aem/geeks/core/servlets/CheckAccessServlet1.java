package com.aem.geeks.core.servlets;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Iterator;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(service = javax.servlet.Servlet.class, property = {
        SLING_SERVLET_PATHS + "=/bin/checkAccess"
})
public class CheckAccessServlet1 extends SlingAllMethodsServlet {

    @Reference
    private SlingRepository repository;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Session session = null;

        try {
            // Get the session from the resource resolver
            ResourceResolver resourceResolver = request.getResourceResolver();
            session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to retrieve JCR session.");
                return;
            }

            // Get the current user ID
            String userId = session.getUserID();
            //String userId="Rajuu";

            if (userId == null) {
                response.sendError(SlingHttpServletResponse.SC_UNAUTHORIZED, "User ID not found in session.");
                return;
            }

            // Access UserManager from the Jackrabbit Session
            UserManager userManager = ((org.apache.jackrabbit.api.JackrabbitSession) session).getUserManager();

            if (userManager != null) {
                Authorizable user = userManager.getAuthorizable(userId);

                if (user != null) {
                    boolean isMemberOfAccessGroup = false;

                    // Check if the user is part of the Access group
                    Iterator<Group> groupIterator = user.memberOf();
                    while (groupIterator.hasNext()) {
                        Authorizable auth = groupIterator.next();
                        if (auth.isGroup() && "Access".equals(auth.getID())) {
                            isMemberOfAccessGroup = true;
                            break;
                        }
                    }

                    // Send the appropriate response
                    response.setContentType("application/json");
                    if (isMemberOfAccessGroup) {
                        response.getWriter().write("{\"message\": \"User " + userId + " has access.\"}");
                    } else {
                        response.getWriter().write("{\"message\": \"User " + userId + " does not have access.\"}");
                    }
                } else {
                    response.sendError(SlingHttpServletResponse.SC_UNAUTHORIZED, "User not found.");
                }
            } else {
                response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to retrieve UserManager.");
            }

        } catch (RepositoryException e) {
            response.sendError(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Repository error: " + e.getMessage());
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}