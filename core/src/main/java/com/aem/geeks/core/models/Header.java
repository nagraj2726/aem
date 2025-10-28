package com.aem.geeks.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Header {
    @ValueMapValue
private String text;

@ValueMapValue
private String pathfield;

    @ValueMapValue

    private boolean checkbox;
    @ChildResource
    public List<HeaderChild>bookdetailswithmap;
    public List<HeaderChild> getBookdetailswithmap() {
        return bookdetailswithmap;
    }
    public String getText() {
        return text;
    }
    public String getPathfield() {
        return pathfield;
    }
    public boolean isCheckbox() {
        return checkbox;
    }

}