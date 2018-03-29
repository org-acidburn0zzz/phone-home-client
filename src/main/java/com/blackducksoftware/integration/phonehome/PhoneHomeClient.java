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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.proxy.ProxyInfo;
import com.blackducksoftware.integration.hub.request.BodyContent;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;

public class PhoneHomeClient {
    public static final String SKIP_PHONE_HOME_VARIABLE = "BLACKDUCK_SKIP_PHONE_HOME";
    public static final String PHONE_HOME_BACKEND = "https://collect.blackducksoftware.com";
    private final IntLogger logger;
    private URL phoneHomeBackendUrl;
    private final int timeout;
    private final ProxyInfo proxyInfo;
    private final boolean alwaysTrustServerCertificate;

    public PhoneHomeClient(final IntLogger logger, final int timeout, final ProxyInfo proxyInfo, final boolean alwaysTrustServerCertificate) {
        this.logger = logger;
        try {
            this.phoneHomeBackendUrl = new URL(PHONE_HOME_BACKEND);
        } catch (final MalformedURLException e) {
            phoneHomeBackendUrl = null;
        }
        this.timeout = timeout;
        this.proxyInfo = proxyInfo;
        this.alwaysTrustServerCertificate = alwaysTrustServerCertificate;
    }

    public PhoneHomeClient(final IntLogger logger, final URL phoneHomeBackendUrl, final int timeout, final ProxyInfo proxyInfo, final boolean alwaysTrustServerCertificate) {
        this.logger = logger;
        this.phoneHomeBackendUrl = phoneHomeBackendUrl;
        this.timeout = timeout;
        this.proxyInfo = proxyInfo;
        this.alwaysTrustServerCertificate = alwaysTrustServerCertificate;
    }

    public void postPhoneHomeRequest(final PhoneHomeRequestBody phoneHomeRequestBody, final CIEnvironmentVariables environmentVariables) throws PhoneHomeException {
        if (environmentVariables.containsKey(SKIP_PHONE_HOME_VARIABLE)) {
            final Boolean skipPhoneHome = Boolean.valueOf(environmentVariables.getValue(SKIP_PHONE_HOME_VARIABLE));
            if (skipPhoneHome) {
                logger.debug("Skipping phone home");
                return;
            }
        }
        if (phoneHomeBackendUrl == null) {
            throw new PhoneHomeException("No phone home server found.");
        }
        logger.debug("Phoning home to " + phoneHomeBackendUrl);

        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(logger);
        builder.setBaseUrl(phoneHomeBackendUrl.toString());
        builder.setTimeout(timeout);
        builder.applyProxyInfo(proxyInfo);
        builder.setAlwaysTrustServerCertificate(alwaysTrustServerCertificate);
        final RestConnection restConnection = builder.build();
        try {
            final Request request = new Request.Builder(phoneHomeBackendUrl.toString()).method(HttpMethod.POST).bodyContent(new BodyContent(phoneHomeRequestBody)).build();
            try (Response response = restConnection.executeRequest(request)) {
            } catch (final IOException io) {
                throw new PhoneHomeException(io.getMessage(), io);
            }
        } catch (final IntegrationException e) {
            throw new PhoneHomeException(e.getMessage(), e);
        }
    }

    public Request createGoogleAnalyticsRequest(final String trackingId, final String registrationId, final String hostName, final String blackDuckName, final String blackDuckVersion, final String thirdPartyName,
            final String thirdPartyVersion, final String pluginVersion, final String source) {
        final Map<String, String> payloadData = new HashMap<>();
        // Api Version
        payloadData.put("v", "1");
        // Hit Type
        payloadData.put("t", "event");
        // Document Path
        payloadData.put("dp", "INTEGRATIONS");
        // Tracking ID
        payloadData.put("tid", trackingId);
        // User ID
        payloadData.put("uid", registrationId);
        // Document Referrer
        payloadData.put("dr", hostName);
        // App Name
        payloadData.put("an", blackDuckName);
        // App Version
        payloadData.put("av", blackDuckVersion);
        // App Installer ID
        payloadData.put("aiid", thirdPartyName);
        // App ID
        payloadData.put("aid", thirdPartyVersion);
        // TODO pluginVersion, source

        // return new Request.Builder("https://www.google-analytics.com/collect")
        // .mimeType(ContentType.TEXT_PLAIN.getMimeType())
        // .method(HttpMethod.GET)
        // .queryParameters(payloadData)
        // .build();

        // TODO

        final BodyContent body = new BodyContent(payloadData);
        return new Request.Builder("https://www.google-analytics.com/analytics.js/collect")
                .mimeType(ContentType.TEXT_PLAIN.getMimeType())
                // .addAdditionalHeader("User-Agent", "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14") // TODO
                .method(HttpMethod.POST)
                .bodyContent(body)
                .build();
    }

}
