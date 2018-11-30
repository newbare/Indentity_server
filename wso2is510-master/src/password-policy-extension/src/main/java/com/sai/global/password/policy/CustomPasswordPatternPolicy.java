package com.sai.global.password.policy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.mgt.policy.AbstractPasswordPolicyEnforcer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom password policy extension
 */

public class CustomPasswordPatternPolicy extends AbstractPasswordPolicyEnforcer {

    private static final Log log = LogFactory.getLog(CustomPasswordPatternPolicy.class);
    private static final String SUPER_TENANT_DOMAIN = "carbon.super";
    private static final String PATTERN = "pattern";
    private static final String ERROR_MSG = "errorMsg";
    private static final String DOT = ".";
    private static final String TENANT = "tenant";

    private Map<String, Map<String, String>> tenantPasswordPolicyContainer = new HashMap<String, Map<String, String>>();
    private String defaultPasswordPolicyPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private String defaultErrorMessage = "Password does not match the pattern " + defaultPasswordPolicyPattern;


    /**
     * Validates the password against the pattern provided
     *
     * @param args - comes as object array, contains the username and the password.
     * @return boolean
     */
    public boolean enforce(Object... args) {

        boolean status = true;

        if (args != null) {

            String password = args[0].toString();
            String userName = args[1].toString();

            String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();

            Matcher matcher = getPasswordPattern(tenantDomain).matcher(password);

            if (matcher.matches()) {

                log.info("password : " + password + " matches with the pattern" + matcher.pattern().toString());
                status = true;

            } else {

                log.info("password : " + password + " did not match the patten " + matcher.pattern().toString());
                errorMessage = getErrorMsg(tenantDomain);

                if (errorMessage == null){
                    errorMessage = "Password does not match the pattern " + matcher.pattern().toString();
                }

                status = false;
            }

        }

        return status;
    }

    /**
     * Load the extension while startup
     *
     * @param parameters contains values related to this extension define in the identity-mgt.properties
     */
    public void init(Map<String, String> parameters) {

        if (parameters != null && parameters.size() > 0) {
            // Parameters related to this extension defined in identity-mgt.properties file
            if (StringUtils.isNotEmpty(parameters.get(PATTERN))) {
                defaultPasswordPolicyPattern = parameters.get(PATTERN);
            }

            if (StringUtils.isNotEmpty(parameters.get("errorMsg"))){
                defaultErrorMessage = parameters.get("errorMsg");
            }
        }

        Map<String, String> defaultPasswordPolicyProperties = getTenantPasswordPolicyProperties(new String[]{defaultPasswordPolicyPattern, defaultErrorMessage});
        tenantPasswordPolicyContainer.put(SUPER_TENANT_DOMAIN, defaultPasswordPolicyProperties);

        for (Map.Entry<String, String> entry : parameters.entrySet())
            if (entry.getKey().contains("tenant")) {
                String tenantDomain = null;
                if (entry.getKey().contains("pattern")) {
                    tenantDomain = entry.getKey().substring(7, (entry.getKey().indexOf("pattern")-1));
                }

                if(tenantDomain == null){
                    tenantDomain = entry.getKey().substring(7, (entry.getKey().indexOf("errorMsg")-1));
                }

                String pattern = parameters.get(TENANT + DOT + tenantDomain + DOT + PATTERN);
                String errorMsg = parameters.get(TENANT + DOT + tenantDomain + DOT + ERROR_MSG);

                if (pattern == null) {
                    log.error("Password Policy Pattern is not defined for tenant: " + tenantDomain + "Hence Using " +
                            "default Password Policy Pattern: " + defaultPasswordPolicyPattern);
                    pattern = defaultPasswordPolicyPattern;
                }

                if (errorMsg == null) {
                    log.error("Password Policy Error Message is not defined for tenant: " + tenantDomain + "Hence Using " +
                            "default Error Message: " + defaultErrorMessage);
                    errorMsg = defaultErrorMessage;
                }

                Map<String, String> tenantPasswordPolicyProperties = getTenantPasswordPolicyProperties(new String[]{pattern, errorMsg});


                tenantPasswordPolicyContainer.put(tenantDomain, tenantPasswordPolicyProperties);
            }

    }


    /**
     * Get the password policy pattern associated with a particular tenant.
     *
     * @param tenantDomain
     * @return Pattern related to the tenant
     */
    private Pattern getPasswordPattern(String tenantDomain) {

        Map<String, String> tenantPasswordPolicyProperties = tenantPasswordPolicyContainer.get(tenantDomain);

        if (tenantPasswordPolicyProperties == null || tenantPasswordPolicyProperties.get("pattern") == null){
            log.info("Could not find password policy for tenant: " + tenantDomain + "hence enforcing default password policy pattern: " + defaultPasswordPolicyPattern);
            return Pattern.compile(defaultPasswordPolicyPattern);
        }

        return Pattern.compile(tenantPasswordPolicyProperties.get(PATTERN));
    }

    /**
     * Get error message defined for tenant in identity-mgt.properties file
     *
     * @param tenantDomain
     * @return
     */
    private String getErrorMsg(String tenantDomain){
        Map<String, String> tenantPasswordPolicyProperties = tenantPasswordPolicyContainer.get(tenantDomain);

        if (tenantPasswordPolicyProperties == null || tenantPasswordPolicyProperties.get("errorMsg") == null){
            log.info("Could not find Error Message for tenant: " + tenantDomain + "hence using default Error Message: " + defaultErrorMessage);
            return defaultErrorMessage;
        }

        return tenantPasswordPolicyProperties.get(ERROR_MSG);
    }

    /**
     * Build password policy properties (pattern, errorMsg)
     *
     * @param properties
     * @return
     */
    private Map<String, String> getTenantPasswordPolicyProperties(String[] properties){
        Map<String, String> tenantPasswordPolicyProperties = new HashMap<String, String>();

        tenantPasswordPolicyProperties.put(PATTERN, properties[0]);
        tenantPasswordPolicyProperties.put(ERROR_MSG, properties[1]);

        return tenantPasswordPolicyProperties;
    }


}
