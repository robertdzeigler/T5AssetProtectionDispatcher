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
