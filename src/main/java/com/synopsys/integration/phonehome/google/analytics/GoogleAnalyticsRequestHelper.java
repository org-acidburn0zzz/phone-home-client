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
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleAnalyticsRequestHelper {
    private final Gson gson;

    public GoogleAnalyticsRequestHelper(Gson gson) {
        this.gson = gson;
    }

    public HttpPost createRequest(PhoneHomeRequestBody phoneHomeRequestBody) throws UnsupportedEncodingException {
        return createRequest(phoneHomeRequestBody, null, GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID);
    }

    public HttpPost createRequest(PhoneHomeRequestBody phoneHomeRequestBody, String url) throws UnsupportedEncodingException {
        return createRequest(phoneHomeRequestBody, url, GoogleAnalyticsConstants.PRODUCTION_INTEGRATIONS_TRACKING_ID);
    }

    public HttpPost createRequest(PhoneHomeRequestBody phoneHomeRequestBody, String url, String trackingId) throws UnsupportedEncodingException {
        GoogleAnalyticsRequestTransformer transformer = new GoogleAnalyticsRequestTransformer(gson, trackingId, phoneHomeRequestBody);
        List<NameValuePair> parameters = transformer.getParameters();
        AbstractHttpEntity entity;
        String requestUrl = url;

        // Not in the transformer because this will likely go away -- rotte 8/6/2019
        List<String> artifactModules = phoneHomeRequestBody.getArtifactModules();

        if (artifactModules == null || artifactModules.size() == 0) {
            if (StringUtils.isBlank(requestUrl)) {
                requestUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.COLLECT_ENDPOINT;
            }

            entity = new UrlEncodedFormEntity(parameters);
        } else {
            String requestString = artifactModules.stream()
                    .map(module -> createModuleParameters(parameters, module))
                    .map(moduleParameters -> URLEncodedUtils.format(moduleParameters, StandardCharsets.ISO_8859_1))
                    .collect(Collectors.joining("\n"));

            if (StringUtils.isBlank(requestUrl)) {
                requestUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.BATCH_ENDPOINT;
            }

            entity = new StringEntity(requestString, StandardCharsets.ISO_8859_1);
        }

        return this.createRequest(requestUrl, entity);

    }

    public HttpPost createRequest(String url, HttpEntity httpEntity) {
        HttpPost post = new HttpPost(url);

        post.setEntity(httpEntity);
        // TODO post.addHeader(HttpHeaders.ACCEPT, ContentType.TEXT_PLAIN.getMimeType());

        return post;
    }

    private List<NameValuePair> createModuleParameters(List<NameValuePair> parameters, String module) {
        NameValuePair parameter = new BasicNameValuePair(GoogleAnalyticsConstants.MODULE_ID, module);
        List<NameValuePair> newParameters = new ArrayList<>(parameters);
        newParameters.add(parameter);
        return newParameters;
    }

}
