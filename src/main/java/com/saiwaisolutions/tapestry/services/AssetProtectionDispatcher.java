/*
 * Created on Jul 28, 2007
 * 
 * 
 */
package com.saiwaisolutions.tapestry.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.AssetResourceLocator;
import org.apache.tapestry5.internal.services.RequestConstants;
import org.apache.tapestry5.services.ClasspathAssetAliasManager;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

import com.saiwaisolutions.tapestry.services.AssetPathAuthorizer.Order;

/**
 * Dispatcher that handles whether to allow or deny access to particular 
 * assets. Actual work of authorizing a particular url is handled by
 * implementations of AssetPathAuthorizer. Configuration is an ordered
 * list of AssetPathAuthorizers.  Each authorizer specifies an order of
 * operations as a list (see AssetPathAuthorizer.Order).
 * @author Robert D. Zeigler
 *
 */
public class AssetProtectionDispatcher implements Dispatcher {
    
    private final Collection<AssetPathAuthorizer> authorizers;
    private final ClasspathAssetAliasManager assetAliasManager;
    private final Logger logger;
    private final String pathPrefix;
    private final Map<String, String> paths;
    
    public AssetProtectionDispatcher(
                           List<AssetPathAuthorizer> auths, 
                           ClasspathAssetAliasManager manager, 
                           @Symbol(SymbolConstants.APPLICATION_VERSION) String applicationVersion,
                           @Symbol(SymbolConstants.APPLICATION_FOLDER) String applicationFolder,
                           @Symbol(SymbolConstants.ASSET_PATH_PREFIX) String assetPathPrefix,
                           Logger logger) {
        this.authorizers = Collections.unmodifiableList(auths);
        this.assetAliasManager = manager;
        this.logger = logger;

        //directly from 5.3.3 AssetDispatcher.
        String folder = applicationFolder.equals("") ? "" : "/" + applicationFolder;

        this.pathPrefix = folder + assetPathPrefix + applicationVersion + "/";

        //end paste. :)

        paths = new HashMap<String, String>();

        Map<String, String> mappings = manager.getMappings();
        for(String p : mappings.keySet()) {
            paths.put(this.pathPrefix + p, mappings.get(p));
        }

    }

    private String toResourcePath(String path) {
        for(String partialPath: paths.keySet()) {
            if (path.startsWith(partialPath)) {
                return paths.get(partialPath) + path.substring(partialPath.length());
            }
        }
        return null;
    }

    public boolean dispatch(Request request, Response response)
            throws IOException {
        String path = request.getPath();
        //we only protect assets, and don't examine any other url's.
        if (!path.startsWith(pathPrefix)) {
            return false;
        }
        String resourcePath = toResourcePath(path);
        //if we can't find a matching folder, we let the resource flow through.
        if (resourcePath == null) {
            return false;
        }
        for(AssetPathAuthorizer auth : authorizers) {
            for(Order o : auth.order()) {
                if (o == Order.ALLOW) {
                    if (auth.accessAllowed(resourcePath)) { 
                        logger.debug("Allowing access to " + resourcePath);
                        return false; 
                    }
                } else {
                    if (auth.accessDenied(resourcePath)) {
                        logger.debug("Denying access to " + resourcePath);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN,resourcePath);
                        return true;
                    }
                }
            }
        }
        //if we get here, no Authorizer had anything useful to say about the resourcePath.
        //so let it fall through.
        logger.debug("Fell through the list of authorizers. Allowing access to: " + resourcePath);
        return false;
    }

}
