package com.aem.geeks.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Carousel {
    @ValueMapValue
    private String text;
    @ChildResource
    public List<CarouselParent>bookdetailswithmap;
    public String getText() {
        return text;
    }
    public List<CarouselParent> getBookdetailswithmap() {
        return bookdetailswithmap;
    }

}