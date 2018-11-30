package com.sai.global.custom.filter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.endpoint.util.Constants;
import org.wso2.securevault.SecretResolver;
import org.wso2.securevault.SecretResolverFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class RedirectionFilter implements Filter {

    private static final Log log = LogFactory.getLog(RedirectionFilter.class);

    private Properties props;
    private static final String SECRET_ALIAS = "secretAlias:";
    private static final String TENANT = "tenant";
    private static final int TENANT_SEPARATOR_INDEX = 7;
    private HashMap<String, String> tenantProperties = new HashMap();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        InputStream inputStream = null;

        props = new Properties();
        String configFilePath = null;
        try {
            configFilePath = buildFilePath(Constants.TenantConstants.CONFIG_RELATIVE_PATH);

            File configFile = new File(configFilePath);

            if (configFile.exists()) {

                log.info(Constants.TenantConstants.CONFIG_FILE_NAME + " file loaded from " + Constants
                        .TenantConstants.CONFIG_RELATIVE_PATH);
                inputStream = new FileInputStream(configFile);

                props.load(inputStream);

                if (isSecuredPropertyAvailable(props)) {
                    // Resolve encrypted properties with secure vault
                    resolveSecrets(props);
                }

            } else {
                log.info(Constants.TenantConstants.CONFIG_FILE_NAME + " file loaded from authentication endpoint " +
                        "webapp");

                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.TenantConstants.CONFIG_FILE_NAME);
                props.load(inputStream);
            }

            Enumeration propertyNamesEnum = props.propertyNames();

            while (propertyNamesEnum.hasMoreElements()) {
                String key = (String) propertyNamesEnum.nextElement();
                if (key.contains(TENANT)) {
                    tenantProperties.put(key.substring(TENANT_SEPARATOR_INDEX), props.getProperty(key));
                }
            }
        } catch (IOException e) {
            log.error("Initialization failed : ", e);
        }


    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String tenantDomain = servletRequest.getParameter("tenantDomain");
        Iterator it = tenantProperties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String tenantDomainFromProperties = ((String) pair.getKey());
            if (tenantDomainFromProperties.equals(tenantDomain)) {
                httpResponse.sendRedirect(((String) pair.getValue()) + "?" + httpRequest.getQueryString());
            }
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * Build the absolute path of a give file path
     *
     * @param path File path
     * @return Absolute file path
     * @throws java.io.IOException
     */
    private String buildFilePath(String path) throws IOException {

        if (StringUtils.isNotEmpty(path) && path.startsWith(Constants.TenantConstants.RELATIVE_PATH_START_CHAR)) {
            // Relative file path is given
            File currentDirectory = new File(new File(Constants.TenantConstants.RELATIVE_PATH_START_CHAR)
                    .getAbsolutePath());
            path = currentDirectory.getCanonicalPath() + File.separator + path;
        }

        if (log.isDebugEnabled()) {
            log.debug("File path for KeyStore/TrustStore : " + path);
        }
        return path;
    }

    private static boolean isSecuredPropertyAvailable(Properties properties) {

        Enumeration propertyNames = properties.propertyNames();

        while (propertyNames.hasMoreElements()) {
            String key = (String) propertyNames.nextElement();
            if (StringUtils.startsWith(properties.getProperty(key), SECRET_ALIAS)) {
                return true;
            }
        }
        return false;
    }

    /**
     * There can be sensitive information like passwords in configuration file. If they are encrypted using secure
     * vault, this method will resolve them and replace with original values.
     */
    private static void resolveSecrets(Properties properties) {

        SecretResolver secretResolver = SecretResolverFactory.create(properties);
        Enumeration propertyNames = properties.propertyNames();
        if (secretResolver != null && secretResolver.isInitialized()) {
            // Iterate through whole config file and find encrypted properties and resolve them
            while (propertyNames.hasMoreElements()) {
                String key = (String) propertyNames.nextElement();
                if (secretResolver.isTokenProtected(key)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Resolving and replacing secret for " + key);
                    }
                    // Resolving the secret password.
                    String value = secretResolver.resolve(key);
                    // Replaces the original encrypted property with resolved property
                    properties.put(key, value);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("No encryption done for value with key :" + key);
                    }
                }
            }
        } else {
            log.warn("Secret Resolver is not present. Will not resolve encryptions in " + Constants.TenantConstants
                    .CONFIG_RELATIVE_PATH + " file");
        }
    }

}
