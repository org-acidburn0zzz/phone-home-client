/**
 * phone-home-client
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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
    private final List<String> artifactModules;

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
        this.artifactModules = phoneHomeRequestBody.getArtifactModules();
    }

    public HttpPost createRequest() throws UnsupportedEncodingException {
        return createRequest(null);
    }

    public HttpPost createRequest(final String overrideUrl) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(overrideUrl)) {
            return this.createRequest(overrideUrl, new UrlEncodedFormEntity(getParameters()));
        } else if (artifactModules.size() == 0) {
            return this.createRequest(GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.COLLECT_ENDPOINT, new UrlEncodedFormEntity(getParameters()));
        } else {
            final String requestString = artifactModules.stream()
                                             .map(this::getParametersForModule)
                                             .map(parameters -> URLEncodedUtils.format(parameters, HTTP.DEF_CONTENT_CHARSET.name()))
                                             .collect(Collectors.joining("\n"));

            final StringEntity stringEntity = new StringEntity(requestString, HTTP.DEF_CONTENT_CHARSET);

            return this.createRequest(GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.BATCH_ENDPOINT, stringEntity);
        }

    }

    private HttpPost createRequest(final String url, final HttpEntity entity) {
        final HttpPost post = new HttpPost(url);

        post.setEntity(entity);
        // TODO post.addHeader(HttpHeaders.ACCEPT, ContentType.TEXT_PLAIN.getMimeType());

        return post;
    }

    private List<NameValuePair> getParametersForModule(final String module) {
        final List<NameValuePair> parameters = getParameters();
        addParameter(GoogleAnalyticsConstants.MODULE_ID, module, parameters::add);
        return parameters;
    }

    private List<NameValuePair> getParameters() {
        final List<NameValuePair> parameters = new ArrayList<>();

        addParameter(GoogleAnalyticsConstants.API_VERSION_KEY, "1", parameters::add);

        addParameter(GoogleAnalyticsConstants.API_VERSION_KEY, "1", parameters::add);
        addParameter(GoogleAnalyticsConstants.HIT_TYPE_KEY, "pageview", parameters::add);
        addParameter(GoogleAnalyticsConstants.CLIENT_ID_KEY, generateClientId(), parameters::add);
        addParameter(GoogleAnalyticsConstants.TRACKING_ID_KEY, trackingId, parameters::add);
        addParameter(GoogleAnalyticsConstants.DOCUMENT_PATH_KEY, "phone-home", parameters::add);

        // Phone Home Parameters
        addParameter(GoogleAnalyticsConstants.CUSTOMER_ID, customerId, parameters::add);
        addParameter(GoogleAnalyticsConstants.HOST_NAME, hostName, parameters::add);
        addParameter(GoogleAnalyticsConstants.ARTIFACT_ID, artifactId, parameters::add);
        addParameter(GoogleAnalyticsConstants.ARTIFACT_VERSION, artifactVersion, parameters::add);
        addParameter(GoogleAnalyticsConstants.PRODUCT_ID, productId.name(), parameters::add);
        addParameter(GoogleAnalyticsConstants.PRODUCT_VERSION, productVersion, parameters::add);
        addParameter(GoogleAnalyticsConstants.META_DATA, gson.toJson(metaData), parameters::add);

        return parameters;
    }

    private void addParameter(final String key, final String value, final Consumer<NameValuePair> adder) {
        final NameValuePair parameter = new BasicNameValuePair(key, value);
        adder.accept(parameter);
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
