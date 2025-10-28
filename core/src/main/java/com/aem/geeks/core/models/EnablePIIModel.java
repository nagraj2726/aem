package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class EnablePIIModel {

    @Self
    private Resource resource;

    @ValueMapValue
    private boolean enablepii;

    @ValueMapValue
    private String cssexe;

    public boolean isEnablepii() {
        return enablepii;
    }

    public String getCssexe() {
        return cssexe;
    }

    public Resource getResource() {
        return resource;
    }
}
