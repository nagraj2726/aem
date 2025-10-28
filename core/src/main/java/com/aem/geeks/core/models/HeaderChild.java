package com.aem.geeks.core.models;

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderChild {
    @ValueMapValue
    private String bookname;
    @ValueMapValue
    private Calendar bookpublishdate;
    
    public String getBookname() {
        return bookname;
    }

    public Calendar getBookpublishdate() {
        return bookpublishdate;
    }
   

}
