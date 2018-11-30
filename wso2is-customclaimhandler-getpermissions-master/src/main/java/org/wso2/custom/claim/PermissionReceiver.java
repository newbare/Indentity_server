/*
 * Copyright (c) 2018 Shanaka Gihan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.wso2.custom.claim;

import org.wso2.carbon.identity.application.authentication.framework.config.model.StepConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl.DefaultClaimHandler;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.AuthorizationManager;
import org.wso2.custom.claim.internal.PermissionReceiverComponent;


import java.util.Arrays;
import java.util.Map;

/**
 * Permission Receiver main class.
 */
public class PermissionReceiver extends DefaultClaimHandler  {

    private static volatile PermissionReceiver instance = null;

    public static PermissionReceiver getInstance() {
        if (instance == null) {
            synchronized (PermissionReceiver.class) {
                if (instance == null) {
                    instance = new PermissionReceiver();
                }
            }
        }
        return instance;
    }

    /**
     * Appending extra requested claims.
     *
     * @param stepConfig
     * @param context
     * @param remoteClaims
     * @param isFederatedClaims
     * @return
     * @throws FrameworkException
     */
    @Override
    public Map<String, String> handleClaimMappings(StepConfig stepConfig,
                                                   AuthenticationContext context, Map<String, String> remoteClaims,
                                                   boolean isFederatedClaims) throws FrameworkException {

        String userName = stepConfig.getAuthenticatedUser().getUserName();
        Map<String, String> requestedClaimMappings = context.getSequenceConfig().getApplicationConfig().getRequestedClaimMappings();
        Map<String, String> claimMappings = super.handleClaimMappings(stepConfig, context, remoteClaims, isFederatedClaims);
        try {
            AuthorizationManager authorizationManager =  PermissionReceiverComponent.getRealmService()
                                                            .getBootstrapRealm().getAuthorizationManager();
            // Get permission from the root for a user.
            String[] permissionList = authorizationManager.getAllowedUIResourcesForUser(userName, "/");
            String permissions = Arrays.toString(permissionList);
            permissions = permissions.substring(1, permissions.length() - 1);
            for (String key : requestedClaimMappings.keySet()) {
                if ("http://wso2.org/claims/permission".equals(key)) {
                    claimMappings.put("permission", permissions);
                }
            }
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
        return claimMappings;
    }
}
