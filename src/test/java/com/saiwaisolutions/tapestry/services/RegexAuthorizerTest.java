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
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegexAuthorizerTest extends Assert {

    @Test
    public void test_regexes() {
        List<String> patterns = Arrays.asList("^.*\\.png$","^.*\\.jpg","^.*\\.jpeg");
        RegexAuthorizer auth = new RegexAuthorizer(patterns);
        String pkg = "assets/com/saiwaisolutions/resources/";
        String png = pkg + "foo.png";
        String jpg = pkg + "foo.jpg";
        String jpeg = pkg + "foo.jpeg";
        String xml = pkg + "foo.xml";
        test(auth,png,true);
        test(auth,jpg,true);
        test(auth,jpeg,true);
        test(auth,xml,false);
    }
    
    private static void test(RegexAuthorizer auth, String one,boolean allowed) {
        assertEquals(auth.accessAllowed(one),allowed);
        assertEquals(
                auth.accessAllowed(
                        "http://localhost:8080" + one),
                allowed);
        assertFalse(auth.accessDenied(one));
        assertFalse(auth.accessDenied("http://localhost:8080" + one));
    }
}
