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
package com.synopsys.integration.phonehome.google.analytics;

import com.google.gson.Gson;
import com.synopsys.integration.phonehome.request.PhoneHomeRequestBody;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoogleAnalyticsRequestTransformer {
    private final List<NameValuePair> parameters = new ArrayList<>();
    private final PhoneHomeRequestBody phoneHomeRequestBody;
    private final String trackingId;
    private final Gson gson;

    public GoogleAnalyticsRequestTransformer(Gson gson, String trackingId, PhoneHomeRequestBody phoneHomeRequestBody) {
        this.gson = gson;
        this.phoneHomeRequestBody = phoneHomeRequestBody;
        this.trackingId = trackingId;
    }

    public List<NameValuePair> getParameters() {
        addParameter(GoogleAnalyticsConstants.API_VERSION_KEY, "1");
        addParameter(GoogleAnalyticsConstants.HIT_TYPE_KEY, "pageview");

        String clientId = generateClientId(phoneHomeRequestBody.getCustomerId(), phoneHomeRequestBody.getHostName());
        addParameter(GoogleAnalyticsConstants.CLIENT_ID_KEY, clientId);
        addParameter(GoogleAnalyticsConstants.TRACKING_ID_KEY, trackingId);
        addParameter(GoogleAnalyticsConstants.DOCUMENT_PATH_KEY, "phone-home");

        // Phone Home Parameters
        addParameter(GoogleAnalyticsConstants.CUSTOMER_ID, phoneHomeRequestBody.getCustomerId());
        addParameter(GoogleAnalyticsConstants.HOST_NAME, phoneHomeRequestBody.getHostName());
        addParameter(GoogleAnalyticsConstants.ARTIFACT_ID, phoneHomeRequestBody.getArtifactId());
        addParameter(GoogleAnalyticsConstants.ARTIFACT_VERSION, phoneHomeRequestBody.getArtifactVersion());
        addParameter(GoogleAnalyticsConstants.PRODUCT_ID, phoneHomeRequestBody.getProductName());
        addParameter(GoogleAnalyticsConstants.PRODUCT_VERSION, phoneHomeRequestBody.getProductVersion());
        addParameter(GoogleAnalyticsConstants.META_DATA, gson.toJson(phoneHomeRequestBody.getMetaData()));

        return parameters;
    }

    private void addParameter(String key, String value) {
        NameValuePair parameter = new BasicNameValuePair(key, value);
        parameters.add(parameter);
    }

    private String generateClientId(String customerId, String hostName) {
        String clientId;
        if (!PhoneHomeRequestBody.UNKNOWN_FIELD_VALUE.equals(customerId)) {
            clientId = customerId;
        } else if (!PhoneHomeRequestBody.UNKNOWN_FIELD_VALUE.equals(hostName)) {
            clientId = hostName;
        } else {
            clientId = PhoneHomeRequestBody.UNKNOWN_FIELD_VALUE;
        }

        byte[] bytesFromString = clientId.getBytes();
        return UUID.nameUUIDFromBytes(bytesFromString).toString();
    }

}
