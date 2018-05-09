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
import java.util.Optional;

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
    private final HttpClientBuilder httpClientBuilder;
    private final Logger logger;
    private final Gson gson;

    private String phoneHomeBackendUrl;

    public PhoneHomeClient(final String googleAnalyticsTrackingId) {
        this(googleAnalyticsTrackingId, createInitialRequestConfigBuilder(10, Optional.empty()).build(), LoggerFactory.getLogger(PhoneHomeClient.class));
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final Logger logger) {
        this(googleAnalyticsTrackingId, createInitialRequestConfigBuilder(10, Optional.empty()).build(), logger);
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final Gson gson) {
        this(googleAnalyticsTrackingId, createInitialRequestConfigBuilder(10, Optional.empty()).build(), gson);
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final Logger logger, final Gson gson) {
        this(googleAnalyticsTrackingId, createInitialRequestConfigBuilder(10, Optional.empty()).build(), logger, gson);
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final RequestConfig httpRequestConfig) {
        this(googleAnalyticsTrackingId, httpRequestConfig, LoggerFactory.getLogger(PhoneHomeClient.class), new Gson());
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final RequestConfig httpRequestConfig, final Gson gson) {
        this(googleAnalyticsTrackingId, httpRequestConfig, LoggerFactory.getLogger(PhoneHomeClient.class), gson);
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final RequestConfig httpRequestConfig, final Logger logger) {
        this(googleAnalyticsTrackingId, httpRequestConfig, logger, new Gson());
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final RequestConfig httpRequestConfig, final Logger logger, final Gson gson) {
        this(googleAnalyticsTrackingId, HttpClientBuilder.create().setDefaultRequestConfig(httpRequestConfig), logger, gson);
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final HttpClientBuilder httpClientBuilder) {
        this(googleAnalyticsTrackingId, httpClientBuilder, LoggerFactory.getLogger(PhoneHomeClient.class));
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final HttpClientBuilder httpClientBuilder, final Logger logger) {
        this(googleAnalyticsTrackingId, httpClientBuilder, logger, new Gson());
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final HttpClientBuilder httpClientBuilder, final Gson gson) {
        this(googleAnalyticsTrackingId, httpClientBuilder, LoggerFactory.getLogger(PhoneHomeClient.class), gson);
    }

    public PhoneHomeClient(final String googleAnalyticsTrackingId, final HttpClientBuilder httpClientBuilder, final Logger logger, final Gson gson) {
        this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
        this.httpClientBuilder = httpClientBuilder;
        this.logger = logger;
        this.gson = gson;

        this.phoneHomeBackendUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.COLLECT_ENDPOINT;
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

        try (final CloseableHttpClient client = httpClientBuilder.build()) {
            final GoogleAnalyticsRequestHelper requestHelper = new GoogleAnalyticsRequestHelper(gson, googleAnalyticsTrackingId, phoneHomeRequestBody);
            final HttpUriRequest request = requestHelper.createRequest(phoneHomeBackendUrl);

            final HttpResponse response = client.execute(request);
            logger.trace("Response Code: " + response.getStatusLine().getStatusCode());
        } catch (final Exception e) {
            throw new PhoneHomeException(e.getMessage(), e);
        }
    }

    public static RequestConfig.Builder createInitialRequestConfigBuilder(final int timeoutSeconds, final Optional<HttpHost> proxyHost) {
        final int timeoutInMillis = timeoutSeconds * 1000;
        final RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(timeoutInMillis);
        builder.setConnectTimeout(timeoutInMillis);
        builder.setSocketTimeout(timeoutInMillis);
        if (proxyHost.isPresent()) {
            builder.setProxy(proxyHost.get());
        }
        return builder;
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

}
