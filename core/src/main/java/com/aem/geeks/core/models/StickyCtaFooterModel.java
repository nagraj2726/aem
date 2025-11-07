package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, 
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StickyCtaFooterModel {

    @ValueMapValue
    private String region;

    @ValueMapValue
    private String primaryCta;

    @ValueMapValue
    private String primaryLink;

    @ValueMapValue
    private String secondaryCta;

    @ValueMapValue
    private String phoneEast;

    @ValueMapValue
    private String phoneWest;

    @ValueMapValue
    private boolean showClose;

    public String getRegion() {
        return region;
    }

    public String getPrimaryCta() {
        return primaryCta;
    }

    public String getPrimaryLink() {
        return primaryLink;
    }

    public String getSecondaryCta() {
        return secondaryCta;
    }

    public boolean isShowClose() {
        return showClose;
    }

    public String getPhoneNumber() {
        // Choose number based on region
        if ("west".equalsIgnoreCase(region)) {
            return phoneWest != null ? phoneWest : "";
        } else {
            return phoneEast != null ? phoneEast : "";
        }
    }
}
