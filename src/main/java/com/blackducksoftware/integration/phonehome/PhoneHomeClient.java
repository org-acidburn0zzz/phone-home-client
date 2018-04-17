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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsRequestHelper;
import com.google.gson.Gson;

public class PhoneHomeClient {
    public static final String SKIP_PHONE_HOME_VARIABLE = "BLACKDUCK_SKIP_PHONE_HOME";
    public static final String PHONE_HOME_URL_OVERRIDE_VARIABLE = "BLACKDUCK_PHONE_HOME_URL_OVERRIDE";

    private final String googleAnalyticsTrackingId;
    private final int timeout;
    private final HttpHost proxyHost;
    private final Gson gson;

    private String phoneHomeBackendUrl;
    private final Logger logger;

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final int timeout, final HttpHost proxyHost) {
        this(LoggerFactory.getLogger(PhoneHomeClient.class), googleAnalyticsTrackingId, timeout, proxyHost, new Gson());
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final int timeout, final HttpHost proxyHost, final Gson gson) {
        this(LoggerFactory.getLogger(PhoneHomeClient.class), googleAnalyticsTrackingId, timeout, proxyHost, gson);
    }

    public PhoneHomeClient(final Logger logger, final String googleAnalyticsTrackingId, final int timeout, final HttpHost proxyHost) {
        this(logger, googleAnalyticsTrackingId, timeout, proxyHost, new Gson());
    }

    public PhoneHomeClient(final Logger logger, final String googleAnalyticsTrackingId, final int timeout, final HttpHost proxyHost, final Gson gson) {
        this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
        this.timeout = timeout;
        this.proxyHost = proxyHost;
        this.gson = gson;

        this.phoneHomeBackendUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.COLLECT_ENDPOINT;
        this.logger = logger;
    }

    public void postPhoneHomeRequest(final PhoneHomeRequestBody phoneHomeRequestBody, final Map<String, String> environmentVariables) throws PhoneHomeException {
        if (skipPhoneHome(environmentVariables)) {
            logger.debug("Skipping phone home");
            return;
        }
        if (phoneHomeRequestBody == null) {
            throw new PhoneHomeException("The request body must not be null.");
        }
        checkOverridePhoneHomeUrl(environmentVariables);
        logger.debug("Phoning home to " + phoneHomeBackendUrl);

        try (final CloseableHttpClient client = createHttpClient()) {
            final GoogleAnalyticsRequestHelper requestHelper = new GoogleAnalyticsRequestHelper(gson, googleAnalyticsTrackingId, phoneHomeRequestBody);
            final HttpUriRequest request = requestHelper.createRequest(phoneHomeBackendUrl);

            final HttpResponse response = client.execute(request);
            logger.trace("Response Code: " + response.getStatusLine().getStatusCode());
        } catch (final Exception e) {
            throw new PhoneHomeException(e.getMessage(), e);
        }
    }

    private boolean skipPhoneHome(final Map<String, String> environmentVariables) {
        if (environmentVariables.containsKey(SKIP_PHONE_HOME_VARIABLE)) {
            final String valueString = environmentVariables.get(SKIP_PHONE_HOME_VARIABLE);
            return Boolean.valueOf(valueString).booleanValue();
        }
        return false;
    }

    private void checkOverridePhoneHomeUrl(final Map<String, String> environmentVariables) {
        if (environmentVariables.containsKey(PHONE_HOME_URL_OVERRIDE_VARIABLE)) {
            final String overrideUrl = environmentVariables.get(PHONE_HOME_URL_OVERRIDE_VARIABLE);
            if (StringUtils.isNotBlank(overrideUrl)) {
                phoneHomeBackendUrl = overrideUrl;
                logger.debug("Overriding Phone-Home URL: " + overrideUrl);
            }
        }
    }

    private CloseableHttpClient createHttpClient() {
        final RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectionRequestTimeout(timeout * 1000);
        configBuilder.setConnectTimeout(timeout * 1000);
        configBuilder.setSocketTimeout(timeout * 1000);
        if (proxyHost != null) {
            configBuilder.setProxy(proxyHost);
        }

        final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(configBuilder.build());
        return clientBuilder.build();
    }

}
