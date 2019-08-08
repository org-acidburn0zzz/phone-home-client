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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;

public class GoogleAnalyticsRequestHelper {
    private final Gson gson;

    public GoogleAnalyticsRequestHelper(final Gson gson) {
        this.gson = gson;
    }

    public HttpPost createRequest(final PhoneHomeRequestBody phoneHomeRequestBody) throws UnsupportedEncodingException {
        return createRequest(phoneHomeRequestBody, null, GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID);
    }

    public HttpPost createRequest(final PhoneHomeRequestBody phoneHomeRequestBody, final String url) throws UnsupportedEncodingException {
        return createRequest(phoneHomeRequestBody, url, GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID);
    }

    public HttpPost createRequest(final PhoneHomeRequestBody phoneHomeRequestBody, final String url, final String trackingId) throws UnsupportedEncodingException {
        final RequestTransformer transformer = new RequestTransformer(trackingId, phoneHomeRequestBody);
        final List<NameValuePair> parameters = transformer.getParameters();
        final AbstractHttpEntity entity;
        String requestUrl = url;

        // Not in the transformer because this will likely go away -- rotte 8/6/2019
        final List<String> artifactModules = phoneHomeRequestBody.getArtifactModules();

        if (artifactModules == null || artifactModules.size() == 0) {
            if (StringUtils.isBlank(requestUrl)) {
                requestUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.COLLECT_ENDPOINT;
            }

            entity = new UrlEncodedFormEntity(parameters);
        } else {
            final String requestString = artifactModules.stream()
                                             .map(module -> addModuleMetadata(parameters, module))
                                             .map(moduleParameters -> URLEncodedUtils.format(moduleParameters, HTTP.DEF_CONTENT_CHARSET.name()))
                                             .collect(Collectors.joining("\n"));

            if (StringUtils.isBlank(requestUrl)) {
                requestUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.BATCH_ENDPOINT;
            }

            entity = new StringEntity(requestString, HTTP.DEF_CONTENT_CHARSET);
        }

        return this.createRequest(requestUrl, entity);

    }

    public HttpPost createRequest(final String url, final HttpEntity httpEntity) {
        final HttpPost post = new HttpPost(url);

        post.setEntity(httpEntity);
        // TODO post.addHeader(HttpHeaders.ACCEPT, ContentType.TEXT_PLAIN.getMimeType());

        return post;
    }

    private List<NameValuePair> addModuleMetadata(final List<NameValuePair> parameters, final String module) {
        final NameValuePair parameter = new BasicNameValuePair(GoogleAnalyticsConstants.MODULE_ID, module);
        final List<NameValuePair> newParameters = new ArrayList<>(parameters);
        newParameters.add(parameter);
        return newParameters;
    }

    private class RequestTransformer {
        final List<NameValuePair> parameters = new ArrayList<>();
        final PhoneHomeRequestBody phoneHomeRequestBody;
        final String trackingId;
        private Consumer<NameValuePair> adder = parameters::add;

        public RequestTransformer(final String trackingId, final PhoneHomeRequestBody phoneHomeRequestBody) {
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
            addParameter(GoogleAnalyticsConstants.PRODUCT_ID, phoneHomeRequestBody.getProductId().name());
            addParameter(GoogleAnalyticsConstants.PRODUCT_VERSION, phoneHomeRequestBody.getProductVersion());
            addParameter(GoogleAnalyticsConstants.META_DATA, gson.toJson(phoneHomeRequestBody.getMetaData()));

            return parameters;
        }

        private void addParameter(final String key, final String value) {
            final NameValuePair parameter = new BasicNameValuePair(key, value);
            adder.accept(parameter);
        }

        private String generateClientId(final String customerId, final String hostName) {
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
}
