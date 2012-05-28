/*
 * Created on Jul 28, 2007
 * 
 * 
 */
package com.saiwaisolutions.tapestry.services;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.saiwaisolutions.tapestry.services.AssetPathAuthorizer.Order;

public class WhitelistAuthorizerTest extends Assert {
    
    @Test
    public void run() {
        WhitelistAuthorizer auth = new WhitelistAuthorizer(Arrays.asList("foo"));
        assertEquals(auth.order().get(0),Order.ALLOW);
        assertEquals(auth.order().get(1),Order.DENY);
        assertEquals(auth.order().size(),2);
        assertTrue(auth.accessAllowed("foo"));
        assertFalse(auth.accessDenied("foo"));
        
        assertFalse(auth.accessAllowed("bar"));
        assertTrue(auth.accessDenied("bar"));
    }

}
