/**
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.is.sso.qsg;

import org.wso2.carbon.identity.sso.agent.SSOAgentConstants;
import org.wso2.carbon.identity.sso.agent.SSOAgentException;
import org.wso2.carbon.identity.sso.agent.bean.SSOAgentConfig;
import org.wso2.carbon.identity.sso.agent.saml.SSOAgentX509Credential;
import org.wso2.carbon.identity.sso.agent.saml.SSOAgentX509KeyStoreCredential;
import org.wso2.is.sso.qsg.util.QSGConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Context Event Listener Class used to do the necessary initializations of the App.
 */
public class QSGContextEventListener implements ServletContextListener {

    private static Logger LOGGER = Logger.getLogger(QSGConstants.LOGGER_NAME);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Properties properties = new Properties();
        try {

            // if swift.com load the swift property file, otherwise load default dispatch property file
            if (servletContextEvent.getServletContext().getContextPath().contains("swift.com")) {
                properties.load(servletContextEvent.getServletContext().getResourceAsStream(
                        "/WEB-INF/classes/swift.properties"));
            } else {
                properties.load(servletContextEvent.getServletContext().
                        getResourceAsStream("/WEB-INF/classes/dispatch.properties"));
            }

            InputStream keyStoreInputStream = servletContextEvent.getServletContext().
                    getResourceAsStream("/WEB-INF/classes/wso2carbon.jks");
            SSOAgentX509Credential credential = new SSOAgentX509KeyStoreCredential(keyStoreInputStream,
                            properties.getProperty("KeyStorePassword").toCharArray(),
                            properties.getProperty("IdPPublicCertAlias"),
                            properties.getProperty("PrivateKeyAlias"),
                            properties.getProperty("PrivateKeyPassword").toCharArray());

            SSOAgentConfig config = new SSOAgentConfig();
            config.initConfig(properties);
            config.getSAML2().setSSOAgentX509Credential(credential);
            servletContextEvent.getServletContext().
                    setAttribute(SSOAgentConstants.CONFIG_BEAN_NAME, config);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (SSOAgentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
