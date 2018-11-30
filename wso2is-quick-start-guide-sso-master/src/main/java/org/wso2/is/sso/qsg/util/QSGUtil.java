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

package org.wso2.is.sso.qsg.util;

import org.wso2.carbon.identity.sso.agent.SSOAgentConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO move this to SSOAgent.
 */
public class QSGUtil {

    public static boolean shouldGoToWelcomePage(HttpServletRequest request) {

        if (request == null) {
            return true;
        }

        //check should go to welcome page, if so go to welcome page
        Object shouldGoToWelcomePage = request.getAttribute(SSOAgentConstants.SHOULD_GO_TO_WELCOME_PAGE);
        return shouldGoToWelcomePage instanceof String && Boolean.parseBoolean((String) shouldGoToWelcomePage);
    }
}
