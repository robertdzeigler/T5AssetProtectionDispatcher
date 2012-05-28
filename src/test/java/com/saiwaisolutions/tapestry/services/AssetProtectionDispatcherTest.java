package com.saiwaisolutions.tapestry.services;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.services.ClasspathAssetAliasManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.slf4j.Logger;

import com.saiwaisolutions.tapestry.services.AssetPathAuthorizer.Order;

/*
 * Created on Jul 28, 2007
 * 
 * 
 */

public class AssetProtectionDispatcherTest extends Assert {

    @Test
    public void ignores_nonassets() throws IOException {
        //shouldn't need any configuration here...
        List<AssetPathAuthorizer> auths = Collections.emptyList();
        Logger logger = createMock(Logger.class);
        ClasspathAssetAliasManager caam = createMock(ClasspathAssetAliasManager.class);
        Map<String,String> mappings = Collections.emptyMap();
        expect(caam.getMappings()).andReturn(mappings);
        Request request = createMock(Request.class);
        expect(request.getPath()).andReturn("start");
        Response response = createMock(Response.class);
        replay(request,response,logger,caam);
        AssetProtectionDispatcher disp = new AssetProtectionDispatcher(auths,caam,"1.0", "", "/asset/", logger);
        assertFalse(disp.dispatch(request, response));
        verify(request,response,caam,logger);
    }
    
    @Test
    public void checks_authorizers() throws IOException {
        String assetPathPrefix = "/asset/";

        Logger logger = createMock(Logger.class);
        List<AssetPathAuthorizer> auths = new ArrayList<AssetPathAuthorizer>();
        AssetPathAuthorizer auth = createMock(AssetPathAuthorizer.class);

        expect(auth.order()).andReturn(Arrays.asList(Order.ALLOW,Order.DENY)).times(2);

        expect(auth.accessAllowed("/cayenne.xml")).andReturn(false);
        expect(auth.accessDenied("/cayenne.xml")).andReturn(true);
        expect(auth.accessAllowed("org/apache/tapestry/default.css")).andReturn(true);
        auths.add(auth);

        logger.debug("Denying access to /cayenne.xml");
        logger.debug("Allowing access to org/apache/tapestry/default.css");

        Request request = createMock(Request.class);
        Response response = createMock(Response.class);
        expect(request.getPath()).andReturn(assetPathPrefix + "1.0/cay/cayenne.xml");
        expect(request.getPath()).andReturn(assetPathPrefix + "1.0/corelib/default.css");
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "/cayenne.xml");
        
        ClasspathAssetAliasManager manager = createMock(ClasspathAssetAliasManager.class);
        Map<String, String> mappings = new HashMap<String, String>();
        mappings.put("cay","");
        mappings.put("corelib","org/apache/tapestry");
        expect(manager.getMappings()).andReturn(mappings);
        replay(auth,request,response,manager,logger);
        AssetProtectionDispatcher disp = new AssetProtectionDispatcher(auths,manager,"1.0", "", assetPathPrefix, logger);
        
        assertTrue(disp.dispatch(request,response));
        assertFalse(disp.dispatch(request, response));
        verify(auth,request,response,logger);
    }
}
