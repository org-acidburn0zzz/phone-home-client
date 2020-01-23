/**
 * phone-home-client
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.phonehome;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.phonehome.exception.PhoneHomeException;
import com.synopsys.integration.phonehome.google.analytics.GoogleAnalyticsRequestHelper;

public class PhoneHomeClient {
    public static final String BLACKDUCK_SKIP_PHONE_HOME_VARIABLE = "BLACKDUCK_SKIP_PHONE_HOME";
    public static final String BLACKDUCK_PHONE_HOME_URL_OVERRIDE_VARIABLE = "BLACKDUCK_PHONE_HOME_URL_OVERRIDE";
    public static final String SKIP_PHONE_HOME_VARIABLE = "SYNOPSYS_SKIP_PHONE_HOME";
    public static final String PHONE_HOME_URL_OVERRIDE_VARIABLE = "SYNOPSYS_PHONE_HOME_URL_OVERRIDE";

    private final HttpClientBuilder httpClientBuilder;
    private final IntLogger logger;
    private final Gson gson;

    public PhoneHomeClient(final IntLogger logger) {
        this(logger, createInitialRequestConfigBuilder(10).build());
    }

    public PhoneHomeClient(final IntLogger logger, final Gson gson) {
        this(logger, createInitialRequestConfigBuilder(10).build(), gson);
    }

    public PhoneHomeClient(final IntLogger logger, final RequestConfig httpRequestConfig) {
        this(logger, httpRequestConfig, new Gson());
    }

    public PhoneHomeClient(final IntLogger logger, final RequestConfig httpRequestConfig, final Gson gson) {
        this(logger, HttpClientBuilder.create().setDefaultRequestConfig(httpRequestConfig), gson);
    }

    public PhoneHomeClient(final IntLogger logger, final HttpClientBuilder httpClientBuilder) {
        this(logger, httpClientBuilder, new Gson());
    }

    public PhoneHomeClient(final IntLogger logger, final HttpClientBuilder httpClientBuilder, final Gson gson) {
        this.httpClientBuilder = httpClientBuilder;
        this.logger = logger;
        this.gson = gson;
    }

    public static RequestConfig.Builder createInitialRequestConfigBuilder(final int timeoutSeconds) {
        final int timeoutInMillis = timeoutSeconds * 1000;
        final RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(timeoutInMillis);
        builder.setConnectTimeout(timeoutInMillis);
        builder.setSocketTimeout(timeoutInMillis);
        return builder;
    }

    public static RequestConfig.Builder createInitialRequestConfigBuilder(final int timeoutSeconds, final HttpHost proxyHost) {
        final RequestConfig.Builder builder = createInitialRequestConfigBuilder(timeoutSeconds);
        builder.setProxy(proxyHost);
        return builder;
    }

    public void postPhoneHomeRequest(final PhoneHomeRequestBody phoneHomeRequestBody, final Map<String, String> environmentVariables) throws PhoneHomeException {
        if (skipPhoneHome(environmentVariables)) {
            logger.debug("Skipping phone home");
            return;
        }
        if (phoneHomeRequestBody == null) {
            throw new PhoneHomeException("The request body must not be null.");
        }
        String overrideUrl = checkOverridePhoneHomeUrl(environmentVariables);

        try (final CloseableHttpClient client = httpClientBuilder.build()) {
            final GoogleAnalyticsRequestHelper requestHelper = new GoogleAnalyticsRequestHelper(gson);
            final HttpUriRequest request;

            if (overrideUrl != null) {
                logger.debug("Overriding Phone-Home URL: " + overrideUrl);
                request = requestHelper.createRequest(phoneHomeRequestBody, overrideUrl);
            } else {
                request = requestHelper.createRequest(phoneHomeRequestBody);
            }

            logger.debug("Phoning home to " + request.getURI());
            final HttpResponse response = client.execute(request);
            logger.trace("Response Code: " + response.getStatusLine().getStatusCode());
        } catch (final Exception e) {
            throw new PhoneHomeException(e.getMessage(), e);
        }
    }

    private boolean skipPhoneHome(final Map<String, String> environmentVariables) {
        if (environmentVariables.containsKey(SKIP_PHONE_HOME_VARIABLE) || environmentVariables.containsKey(BLACKDUCK_SKIP_PHONE_HOME_VARIABLE)) {
            String valueString = environmentVariables.get(SKIP_PHONE_HOME_VARIABLE);
            if (StringUtils.isBlank(valueString)) {
                valueString = environmentVariables.get(BLACKDUCK_SKIP_PHONE_HOME_VARIABLE);
            }
            return BooleanUtils.toBoolean(valueString);
        }
        return false;
    }

    private String checkOverridePhoneHomeUrl(final Map<String, String> environmentVariables) {
        String overrideUrl;

        overrideUrl = environmentVariables.get(PHONE_HOME_URL_OVERRIDE_VARIABLE);
        if (StringUtils.isBlank(overrideUrl)) {
            overrideUrl = environmentVariables.get(BLACKDUCK_PHONE_HOME_URL_OVERRIDE_VARIABLE);
        }

        return overrideUrl;
    }

}
