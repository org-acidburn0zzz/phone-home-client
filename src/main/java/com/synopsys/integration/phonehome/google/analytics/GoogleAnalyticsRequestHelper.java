/**
 * phone-home-client
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.phonehome.google.analytics;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;
import com.synopsys.integration.phonehome.enums.ProductIdEnum;

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

    public HttpPost createRequest(final String url) throws UnsupportedEncodingException {
        final HttpPost post = new HttpPost(url);

        final List<NameValuePair> parameters = new ArrayList<>();
        for (final Entry<String, String> entry : getPayloadDataMap().entrySet()) {
            final NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            parameters.add(nameValuePair);
        }
        post.setEntity(new UrlEncodedFormEntity(parameters));
        // TODO post.addHeader(HttpHeaders.ACCEPT, ContentType.TEXT_PLAIN.getMimeType());

        return post;
    }

    private Map<String, String> getPayloadDataMap() {
        final Map<String, String> payloadData = new HashMap<>();

        payloadData.put(GoogleAnalyticsConstants.API_VERSION_KEY, "1");
        payloadData.put(GoogleAnalyticsConstants.HIT_TYPE_KEY, "pageview");
        payloadData.put(GoogleAnalyticsConstants.CLIENT_ID_KEY, generateClientId());
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

    private String generateClientId() {
        final String clientId;
        if (!PhoneHomeRequestBody.Builder.UNKNOWN_ID.equals(customerId)) {
            clientId = customerId;
        } else if (!PhoneHomeRequestBody.Builder.UNKNOWN_ID.equals(hostName)) {
            clientId = hostName;
        } else {
            clientId = PhoneHomeRequestBody.Builder.UNKNOWN_ID;
        }

        final byte[] bytesFromString = clientId.getBytes();
        return UUID.nameUUIDFromBytes(bytesFromString).toString();
    }

}
