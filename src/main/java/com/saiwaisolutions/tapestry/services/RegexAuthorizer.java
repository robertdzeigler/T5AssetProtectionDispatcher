/*
 * Created on Nov 26, 2007
 * 
 * 
 */
package com.saiwaisolutions.tapestry.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides a regex-based authorization scheme for asset-access authorization.
 * Note that this implementation doesn't actually deny access to anything.
 * But it's placement within the chain of command of authorizers is just before
 * the whitelist authorizer, which has an explicit deny policy.
 * Hence, as long as the whitelist authorizer is being used in conjunction with
 * the regex authorizer, there is no need to worry about accessDenied.
 * @author robertz
 *
 */
public class RegexAuthorizer implements AssetPathAuthorizer {
    
    private final Collection<Pattern> _regexes;
    
    public RegexAuthorizer(final Collection<String> regex) {
        List<Pattern> tmp = new ArrayList<Pattern>();
        for(String exp : regex) {
            tmp.add(Pattern.compile(exp));
        }
        _regexes = Collections.unmodifiableCollection(tmp);
        
    }

    public boolean accessAllowed(String resourcePath) {
        for(Pattern regex : _regexes) {
            if (regex.matcher(resourcePath).matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean accessDenied(String resourcePath) {
        return false;
    }

    public List<Order> order() {
        return Arrays.asList(Order.ALLOW);
    }

}
