package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StickyCtaFooterModel {

    @ValueMapValue private String region;
    @ValueMapValue private String primaryCta;
    @ValueMapValue private String primaryLink;
    @ValueMapValue private String secondaryCta;
    @ValueMapValue private String secondaryLink;
    @ValueMapValue private String phoneEast;
    @ValueMapValue private String phoneWest;
    @ValueMapValue private boolean showClose;

    // ✅ New property
    @ValueMapValue private boolean disableComponent;

    public String getRegion() { return region; }
    public String getPrimaryCta() { return primaryCta; }
    public String getPrimaryLink() { return primaryLink; }
    public String getSecondaryCta() { return secondaryCta; }
    public String getSecondaryLink() {return secondaryLink;}
    public String getPhoneNumber() {
        return "west".equalsIgnoreCase(region) ? phoneWest : phoneEast;
    }
    public boolean isShowClose() { return showClose; }

    public boolean isDisableComponent() { return disableComponent; }
}
