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

import org.wso2.is.sso.qsg.util.QSGConstants;
import org.wso2.is.sso.qsg.util.QSGUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * QSG filter to redirect to login page when required.
 */
public class QSGFilter implements Filter {

    private static Logger LOGGER = Logger.getLogger(QSGConstants.LOGGER_NAME);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            if (request.getContextPath().contains("swift.com")) {
                if (QSGUtil.shouldGoToWelcomePage(request)) {
                    response.sendRedirect("/swift.com/index.jsp");
                    return;
                }

            } else if (request.getContextPath().contains("dispatch.com")) {
                if (QSGUtil.shouldGoToWelcomePage(request)) {
                    response.sendRedirect("/dispatch.com/index.jsp");
                    return;
                }
            }
        } catch (ClassCastException e) {
            LOGGER.log(Level.SEVERE, "Unexpected Request Response type.", e);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
