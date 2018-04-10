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
package com.blackducksoftware.integration.phonehome.google.analytics;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.entity.ContentType;

import com.blackducksoftware.integration.hub.request.BodyContent;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.rest.HttpMethod;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;
import com.google.gson.Gson;

public class GoogleAnalyticsRequestHelper {
    private final Gson gson;
    private final String trackingId;

    private final String customerId;
    private final String hostName;
    private final String artifactId;
    private final String artifactVersion;
    private final ProductIdEnum productId;
    private final String productVersion;
    private final Map<String, String> metaData;

    public GoogleAnalyticsRequestHelper(final Gson gson, final String trackingId, final PhoneHomeRequestBody phoneHomeRequestBody) {
        this.gson = gson;
        this.trackingId = trackingId;

        this.customerId = phoneHomeRequestBody.getCustomerId();
        this.hostName = phoneHomeRequestBody.getHostName();
        this.artifactId = phoneHomeRequestBody.getArtifactId();
        this.artifactVersion = phoneHomeRequestBody.getArtifactVersion();
        this.productId = phoneHomeRequestBody.getProductId();
        this.productVersion = phoneHomeRequestBody.getProductVersion();
        this.metaData = phoneHomeRequestBody.getMetaData();
    }

    public Request createRequest(final String url) {
        final BodyContent body = new BodyContent(getPayloadDataMap());
        return new Request.Builder(url)
                .mimeType(ContentType.TEXT_PLAIN.getMimeType())
                .method(HttpMethod.POST)
                .bodyContent(body)
                .build();
    }

    private Map<String, String> getPayloadDataMap() {
        final Map<String, String> payloadData = new HashMap<>();
        payloadData.put(GoogleAnalyticsConstants.API_VERSION_KEY, "1");
        payloadData.put(GoogleAnalyticsConstants.HIT_TYPE_KEY, "pageview");
        payloadData.put(GoogleAnalyticsConstants.CLIENT_ID_KEY, createUUIDFromString(customerId));
        payloadData.put(GoogleAnalyticsConstants.TRACKING_ID_KEY, trackingId);
        payloadData.put(GoogleAnalyticsConstants.DOCUMENT_PATH_KEY, "phone-home");

        // Phone Home Parameters
        payloadData.put(GoogleAnalyticsConstants.CUSTOMER_ID, customerId);
        payloadData.put(GoogleAnalyticsConstants.HOST_NAME, hostName);
        payloadData.put(GoogleAnalyticsConstants.ARTIFACT_ID, artifactId);
        payloadData.put(GoogleAnalyticsConstants.ARTIFACT_VERSION, artifactVersion);
        payloadData.put(GoogleAnalyticsConstants.PRODUCT_ID, productId.name());
        payloadData.put(GoogleAnalyticsConstants.PRODUCT_VERSION, productVersion);
        payloadData.put(GoogleAnalyticsConstants.META_DATA, gson.toJson(metaData));

        return payloadData;
    }

    private String createUUIDFromString(final String str) {
        final byte[] bytesFromString = str.getBytes();
        return UUID.nameUUIDFromBytes(bytesFromString).toString();
    }

}
