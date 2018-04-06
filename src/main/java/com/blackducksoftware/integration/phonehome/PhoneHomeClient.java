/**
 * phone-home-client
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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

import java.io.IOException;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.phonehome.body.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsRequestHelper;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;

public class PhoneHomeClient {
    public static final String SKIP_PHONE_HOME_VARIABLE = "BLACKDUCK_SKIP_PHONE_HOME";
    private final IntLogger logger;
    private final String googleAnalyticsTrackingId;
    private final String phoneHomeBackendUrl;
    private final int timeout;
    private final ProxyInfo proxyInfo;
    private final boolean alwaysTrustServerCertificate;

    public PhoneHomeClient(final IntLogger logger, final String googleAnalyticsTrackingId, final int timeout, final ProxyInfo proxyInfo, final boolean alwaysTrustServerCertificate) {
        this.logger = logger;
        this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
        this.phoneHomeBackendUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.COLLECT_ENDPOINT;
        this.timeout = timeout;
        this.proxyInfo = proxyInfo;
        this.alwaysTrustServerCertificate = alwaysTrustServerCertificate;

    }

    public void postPhoneHomeRequest(final PhoneHomeRequestBody phoneHomeRequestBody, final CIEnvironmentVariables environmentVariables) throws PhoneHomeException {
        if (skipPhoneHome(environmentVariables)) {
            logger.debug("Skipping phone home");
            return;
        }
        if (phoneHomeRequestBody == null) {
            throw new PhoneHomeException("The request body must not be null.");
        }
        logger.debug("Phoning home to " + phoneHomeBackendUrl);

        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(logger);
        builder.setBaseUrl(phoneHomeBackendUrl);
        builder.setTimeout(timeout);
        builder.applyProxyInfo(proxyInfo);
        builder.setAlwaysTrustServerCertificate(alwaysTrustServerCertificate);
        final RestConnection restConnection = builder.build();

        final GoogleAnalyticsRequestHelper requestHelper = new GoogleAnalyticsRequestHelper(googleAnalyticsTrackingId, phoneHomeRequestBody);
        final Request request = requestHelper.createRequest(phoneHomeBackendUrl);

        try (Response response = restConnection.executeRequest(request)) {
            logger.trace("Google Analytics Response Code: " + response.getStatusCode());
        } catch (final IntegrationException | IOException requestException) {
            throw new PhoneHomeException(requestException.getMessage(), requestException);
        }
    }

    private boolean skipPhoneHome(final CIEnvironmentVariables environmentVariables) {
        if (environmentVariables.containsKey(SKIP_PHONE_HOME_VARIABLE)) {
            final String valueString = environmentVariables.getValue(SKIP_PHONE_HOME_VARIABLE);
            return Boolean.valueOf(valueString).booleanValue();
        }
        return false;
    }

}
