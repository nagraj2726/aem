package com.aem.geeks.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {PaginatedModel.class, ComponentExporter.class},
        resourceType = "aemgeeks/components/authorship",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PaginatedModelImpl implements ComponentExporter {


    private static final Logger LOG = LoggerFactory.getLogger(PaginatedModelImpl.class);

    @ValueMapValue
    private String pagePath;

    @ValueMapValue
    private int pageLimit;

    @ValueMapValue
    private int pageSize;

    private List<String> paginatedItems;
    private int totalItems;
    private int totalPages;
    private int nextPageNumber;
    private int previousPageNumber;

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init() {
        paginatedItems = new ArrayList<>();

        try {
            LOG.info("=== PAGINATION DEBUG START ===");
            LOG.info("Authored pagePath: {}", pagePath);
            LOG.info("Page limit: {}, Page size: {}", pageLimit, pageSize);

            if (resourceResolver == null) {
                LOG.error("ResourceResolver is NULL!");
                return;
            }

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager == null) {
                LOG.error("PageManager is NULL!");
                return;
            }

            if (pagePath == null || pagePath.isEmpty()) {
                LOG.error("Page path is missing! Please author a valid page path in the dialog.");
                return;
            }

            Page parentPage = pageManager.getPage(pagePath);
            if (parentPage == null) {
                LOG.error("No page found at path: {}", pagePath);
                return;
            }

            LOG.info("Parent page found: {}", parentPage.getPath());

            // Collect all child pages
            List<Page> allPages = new ArrayList<>();
            Iterator<Page> children = parentPage.listChildren();
            while (children.hasNext()) {
                allPages.add(children.next());
            }

            totalItems = allPages.size();
            LOG.info("Total child pages found: {}", totalItems);

            if (totalItems == 0) {
                LOG.warn("No child pages found under: {}", pagePath);
                return;
            }

            if (pageLimit <= 0) pageLimit = 3;
            if (pageSize <= 0) pageSize = 1;

            totalPages = (int) Math.ceil((double) totalItems / pageLimit);
            LOG.info("Calculated totalPages: {}", totalPages);

            // Simple pagination logic (simulate page 1)
            int startIndex = 0;
            int endIndex = Math.min(pageLimit, totalItems);
            for (int i = startIndex; i < endIndex; i++) {
                paginatedItems.add(allPages.get(i).getPath());
            }

            nextPageNumber = (totalPages > 1) ? 2 : 0;
            previousPageNumber = 1;

            LOG.info("Paginated items: {}", paginatedItems);
            LOG.info("=== PAGINATION DEBUG END ===");

        } catch (Exception e) {
            LOG.error("Error in pagination model: ", e);
        }
    }

    public List<String> getPaginatedItems() {
        return paginatedItems;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalItems() {
        return totalItems;
    }

    
    public int getNextPageNumber() {
        return nextPageNumber;
    }

    public int getPreviousPageNumber() {
        return previousPageNumber;
    }

    @Override
    public String getExportedType() {
        return "aemgeeks/components/authorship";
    }
}
