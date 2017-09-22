/**
 * Phone Home Client
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.phonehome;

import java.net.MalformedURLException;
import java.net.URL;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;

public class PhoneHomeClient {
    public static final String PHONE_HOME_BACKEND = "https://collect.blackducksoftware.com";
    private final IntLogger logger;
    private URL phoneHomeBackendUrl;
    private final RestConnection baseConnection;

    public PhoneHomeClient(final IntLogger logger, final RestConnection restConnection) {
        this.logger = logger;
        try {
            this.phoneHomeBackendUrl = new URL(PHONE_HOME_BACKEND);
        } catch (final MalformedURLException e) {
            phoneHomeBackendUrl = null;
        }
        this.baseConnection = restConnection;
    }

    public PhoneHomeClient(final IntLogger logger, final URL phoneHomeBackendUrl, final RestConnection restConnection) {
        this.logger = logger;
        this.phoneHomeBackendUrl = phoneHomeBackendUrl;
        this.baseConnection = restConnection;
    }

    public void postPhoneHomeRequest(final PhoneHomeRequestBody phoneHomeRequestBody) throws PhoneHomeException {
        if (phoneHomeBackendUrl == null) {
            throw new PhoneHomeException("No phone home server found.");
        }
        logger.debug("Phoning home to " + phoneHomeBackendUrl);
        final RestConnection restConnection = new UnauthenticatedRestConnection(logger, phoneHomeBackendUrl, baseConnection.timeout);
        restConnection.proxyHost = baseConnection.proxyHost;
        restConnection.proxyPort = baseConnection.proxyPort;
        restConnection.proxyNoHosts = baseConnection.proxyNoHosts;
        restConnection.proxyUsername = baseConnection.proxyUsername;
        restConnection.proxyPassword = baseConnection.proxyPassword;
        restConnection.alwaysTrustServerCertificate = baseConnection.alwaysTrustServerCertificate;
        final HubRequest request = new HubRequest(restConnection);
        try {
            request.executePost(restConnection.gson.toJson(phoneHomeRequestBody));
        } catch (final IntegrationException e) {
            throw new PhoneHomeException(e.getMessage(), e);
        }
    }

}
