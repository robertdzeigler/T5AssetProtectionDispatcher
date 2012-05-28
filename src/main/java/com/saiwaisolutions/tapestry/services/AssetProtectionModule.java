/*
 * Created on Jul 28, 2007
 * 
 * 
 */
package com.saiwaisolutions.tapestry.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Dispatcher;

public class AssetProtectionModule {
   
    public static void bind(ServiceBinder binder) {
        binder.bind(Dispatcher.class,AssetProtectionDispatcher.class).withId("AssetProtectionDispatcher");
        binder.bind(AssetPathAuthorizer.class,WhitelistAuthorizer.class).withId("WhitelistAuthorizer");
        binder.bind(AssetPathAuthorizer.class,RegexAuthorizer.class).withId("RegexAuthorizer");
    }
    
    //toss the protector dispatcher into the master config...
    public void contributeMasterDispatcher(
            OrderedConfiguration<Dispatcher> conf, 
            @InjectService("AssetProtectionDispatcher") Dispatcher disp) {
        conf.add("AssetProtection", disp, "before:Asset");
    }
    
    //add the whitelist authorizer ...
    public void contributeAssetProtectionDispatcher(
            @InjectService("WhitelistAuthorizer") AssetPathAuthorizer whitelist,
            @InjectService("RegexAuthorizer") AssetPathAuthorizer regex,
            OrderedConfiguration<AssetPathAuthorizer> conf) {
        //putting whitelist after everything ensures that, in fact, nothing falls through.
        //also ensures that whitelist gives other authorizers the chance to act...
        conf.add("regex",regex,"before:whitelist");
        conf.add("whitelist", whitelist,"after:*");
    }

    public void contributeRegexAuthorizer(Configuration<String> regex, 
                @Symbol("tapestry.scriptaculous.path") String scriptPath,
                @Symbol("tapestry.blackbird.path") String blackbirdPath,
                @Symbol("tapestry.datepicker.path") String datepickerPath) {
        //allow any js, jpg, jpeg, png, or css under org/chenillekit/tapstry. The funky bit of ([^/.]+/)* is what allows
        //multiple paths, while not allowing any of those paths to contains ./ or ../ thereby preventing paths like:
        //org/chenillekit/tapestry/../../../foo.js
        String pathPattern = "([^/.]+/)*[^/.]+\\.((css)|(js)|(jpg)|(jpeg)|(png)|(gif))$";
        regex.add("^org/chenillekit/tapestry/" + pathPattern);
        
        regex.add("^org/apache/tapestry5/" + pathPattern);

        regex.add(blackbirdPath + "/" + pathPattern);
        regex.add(datepickerPath + "/" + pathPattern);
        regex.add(scriptPath + "/" + pathPattern);
    }
}
