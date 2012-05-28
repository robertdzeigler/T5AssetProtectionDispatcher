/*
* Copyright 2012 Robert D. Zeigler
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
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
