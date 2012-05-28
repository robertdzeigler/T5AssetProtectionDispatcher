/*
 * Created on Nov 26, 2007
 * 
 * 
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
