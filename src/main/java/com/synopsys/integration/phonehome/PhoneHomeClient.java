/**
 * phone-home-client
 * <p>
 * Copyright (c) 2020 Synopsys, Inc.
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.phonehome;

import com.google.gson.Gson;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.phonehome.exception.PhoneHomeException;
import com.synopsys.integration.phonehome.google.analytics.GoogleAnalyticsRequestHelper;
import com.synopsys.integration.phonehome.request.PhoneHomeRequestBody;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;

import java.util.Map;

public class PhoneHomeClient {
    public static final String BLACKDUCK_SKIP_PHONE_HOME_VARIABLE = "BLACKDUCK_SKIP_PHONE_HOME";
    public static final String BLACKDUCK_PHONE_HOME_URL_OVERRIDE_VARIABLE = "BLACKDUCK_PHONE_HOME_URL_OVERRIDE";
    public static final String SKIP_PHONE_HOME_VARIABLE = "SYNOPSYS_SKIP_PHONE_HOME";
    public static final String PHONE_HOME_URL_OVERRIDE_VARIABLE = "SYNOPSYS_PHONE_HOME_URL_OVERRIDE";

    private final HttpClientBuilder httpClientBuilder;
    private final IntLogger logger;
    private final Gson gson;

    public PhoneHomeClient(IntLogger logger, HttpClientBuilder httpClientBuilder, Gson gson) {
        this.httpClientBuilder = httpClientBuilder;
        this.logger = logger;
        this.gson = gson;
    }

    public void postPhoneHomeRequest(PhoneHomeRequestBody phoneHomeRequestBody, Map<String, String> environmentVariables) throws PhoneHomeException {
        if (skipPhoneHome(environmentVariables)) {
            logger.debug("Skipping phone home");
            return;
        }
        if (phoneHomeRequestBody == null) {
            throw new PhoneHomeException("The request body must not be null.");
        }
        String overrideUrl = checkOverridePhoneHomeUrl(environmentVariables);

        try (CloseableHttpClient client = httpClientBuilder.build()) {
            GoogleAnalyticsRequestHelper requestHelper = new GoogleAnalyticsRequestHelper(gson);
            HttpUriRequest request;

            if (overrideUrl != null) {
                logger.debug("Overriding Phone-Home URL: " + overrideUrl);
                request = requestHelper.createRequest(phoneHomeRequestBody, overrideUrl);
            } else {
                request = requestHelper.createRequest(phoneHomeRequestBody);
            }

            logger.debug("Phoning home to " + request.getUri());
            HttpResponse response = client.execute(request);
            logger.trace("Response Code: " + response.getCode());
        } catch (Exception e) {
            throw new PhoneHomeException(e.getMessage(), e);
        }
    }

    private boolean skipPhoneHome(Map<String, String> environmentVariables) {
        if (environmentVariables.containsKey(SKIP_PHONE_HOME_VARIABLE) || environmentVariables.containsKey(BLACKDUCK_SKIP_PHONE_HOME_VARIABLE)) {
            String valueString = environmentVariables.get(SKIP_PHONE_HOME_VARIABLE);
            if (StringUtils.isBlank(valueString)) {
                valueString = environmentVariables.get(BLACKDUCK_SKIP_PHONE_HOME_VARIABLE);
            }
            return BooleanUtils.toBoolean(valueString);
        }
        return false;
    }

    private String checkOverridePhoneHomeUrl(Map<String, String> environmentVariables) {
        String overrideUrl;

        overrideUrl = environmentVariables.get(PHONE_HOME_URL_OVERRIDE_VARIABLE);
        if (StringUtils.isBlank(overrideUrl)) {
            overrideUrl = environmentVariables.get(BLACKDUCK_PHONE_HOME_URL_OVERRIDE_VARIABLE);
        }

        return overrideUrl;
    }

}
