package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MultifieldModel {

    @SlingObject
    private Resource resource;
    
    
	

	public List<Map<String, Object>> getMultiField() {
        List<Map<String, Object>> mList = new ArrayList<>();
        Resource mf = resource.getChild("mField");
        if (mf != null) {
            for (Resource item : mf.getChildren()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("tField", item.getValueMap().get("tField", String.class));
                map.put("pField", item.getValueMap().get("pField", String.class));
                map.put("imgField", item.getValueMap().get("imgField", String.class));
                mList.add(map);
            }
        }
        return mList;
    }
    
}
