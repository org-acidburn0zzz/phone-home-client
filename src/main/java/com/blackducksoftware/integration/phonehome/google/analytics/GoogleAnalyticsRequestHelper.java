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
import com.blackducksoftware.integration.phonehome.enums.BlackDuckName;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeRequestFieldEnum;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

public class GoogleAnalyticsRequestHelper {
    private final String collectUrl;
    private final String trackingId;

    private final String regId;
    private final String hostName;
    private final String source;

    private final String blackDuckName;
    private final String blackDuckVersion;
    private final String thirdPartyName;
    private final String thirdPartyVersion;
    private final String pluginVersion;

    public GoogleAnalyticsRequestHelper(final String collectUrl, final String trackingId, final PhoneHomeRequestBody phoneHomeRequestBody) {
        this.collectUrl = collectUrl;
        this.trackingId = trackingId;

        this.regId = phoneHomeRequestBody.getRegId();
        this.hostName = phoneHomeRequestBody.getHostName();
        this.source = phoneHomeRequestBody.getSource();

        final Map<String, String> infoMap = phoneHomeRequestBody.getInfoMap();
        this.blackDuckName = infoMap.get(PhoneHomeRequestFieldEnum.BLACKDUCKNAME.getKey());
        this.blackDuckVersion = infoMap.get(PhoneHomeRequestFieldEnum.BLACKDUCKVERSION.getKey());
        this.thirdPartyName = infoMap.get(PhoneHomeRequestFieldEnum.THIRDPARTYNAME.getKey());
        this.thirdPartyVersion = infoMap.get(PhoneHomeRequestFieldEnum.THIRDPARTYVERSION.getKey());
        this.pluginVersion = infoMap.get(PhoneHomeRequestFieldEnum.PLUGINVERSION.getKey());
    }

    public GoogleAnalyticsRequestHelper(final String collectUrl, final String trackingId, final String regId, final String hostName, final PhoneHomeSource source, final BlackDuckName blackDuckName, final String blackDuckVersion,
            final ThirdPartyName thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        this.collectUrl = collectUrl;
        this.trackingId = trackingId;

        this.regId = regId;
        this.hostName = hostName;
        this.source = source.getName();

        this.blackDuckName = blackDuckName.getName();
        this.blackDuckVersion = blackDuckVersion;
        this.thirdPartyName = thirdPartyName.getName();
        this.thirdPartyVersion = thirdPartyVersion;
        this.pluginVersion = pluginVersion;
    }

    public Request createRequest() {
        final BodyContent body = new BodyContent(getPayloadDataMap());
        return new Request.Builder(collectUrl)
                .mimeType(ContentType.TEXT_PLAIN.getMimeType())
                .method(HttpMethod.POST)
                .bodyContent(body)
                .build();
    }

    private Map<String, String> getPayloadDataMap() {
        final Map<String, String> payloadData = new HashMap<>();
        payloadData.put(GoogleAnalyticsConstants.API_VERSION_KEY, "1");
        payloadData.put(GoogleAnalyticsConstants.HIT_TYPE_KEY, "pageview");
        payloadData.put(GoogleAnalyticsConstants.CLIENT_ID_KEY, UUID.nameUUIDFromBytes(hostName.getBytes()).toString());
        payloadData.put(GoogleAnalyticsConstants.TRACKING_ID_KEY, trackingId);
        payloadData.put(GoogleAnalyticsConstants.DOCUMENT_PATH_KEY, "phone-home");

        // Phone Home Parameters
        payloadData.put(GoogleAnalyticsConstants.REGISTRATION_ID_KEY, regId);
        payloadData.put(GoogleAnalyticsConstants.HOST_NAME_KEY, hostName);
        payloadData.put(GoogleAnalyticsConstants.BLACK_DUCK_NAME_KEY, blackDuckName);
        payloadData.put(GoogleAnalyticsConstants.BLACK_DUCK_VERSION_KEY, blackDuckVersion);
        payloadData.put(GoogleAnalyticsConstants.THIRD_PARTY_NAME_KEY, thirdPartyName);
        payloadData.put(GoogleAnalyticsConstants.THIRD_PARTY_VERSION_KEY, thirdPartyVersion);
        payloadData.put(GoogleAnalyticsConstants.PLUGIN_VERSION_KEY, pluginVersion);
        payloadData.put(GoogleAnalyticsConstants.SOURCE_KEY, source);

        return payloadData;
    }

}
