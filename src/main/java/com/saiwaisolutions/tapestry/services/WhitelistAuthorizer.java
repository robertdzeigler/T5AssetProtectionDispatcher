/*
 * Created on Jul 28, 2007
 * 
 * 
 */
package com.saiwaisolutions.tapestry.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistAuthorizer implements AssetPathAuthorizer {
    
    public List<Order> order() {
        return Arrays.asList(Order.ALLOW, Order.DENY);
    }

    //hash the resource paths for fast lookups.
    private final Map<String, Boolean> _paths;
    
    public WhitelistAuthorizer(Collection<String> paths) {
        _paths = new ConcurrentHashMap<String, Boolean>();
        for(String path : paths) {
            _paths.put(path, true);
        }
    }
    
    public boolean accessAllowed(String resourcePath) {
        return (_paths.containsKey(resourcePath));
    }

    public boolean accessDenied(String resourcePath) {
        return !_paths.containsKey(resourcePath);
    }

}
