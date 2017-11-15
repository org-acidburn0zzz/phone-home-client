/**
 * Phone Home Client
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.phonehome.enums.BlackDuckName;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeRequestFieldEnum;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;
import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;

public class PhoneHomeClientUnitTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Rule
    public final MockServerRule msRule = new MockServerRule(this);

    public static final String LOCALHOST = "127.0.0.1";
    public static final int TIMEOUT = 5;

    private final MockServerClient msClient = new MockServerClient(LOCALHOST, msRule.getPort());
    private final int port = msRule.getPort();

    @Before
    public void startProxy() {
        msClient.when(new HttpRequest().withPath("/test")).respond(new HttpResponse().withHeader(new Header("Content-Type", "json")));
    }

    @After
    public void stopProxy() {
        // Intentionally left blank
    }

    @Test
    public void callHomeInvalidUrl() throws Exception {
        exception.expect(PhoneHomeException.class);
        final String targetUrl = "http://example.com:" + this.port + "/test";
        final URL url = new URL(targetUrl);
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(new IntBufferedLogger(), url, restConnection.timeout, restConnection.getProxyInfo(), restConnection.alwaysTrustServerCertificate);

        final String regId = "regId";
        final PhoneHomeSource source = PhoneHomeSource.INTEGRATIONS;
        final Map<String, String> infoMap = new HashMap<>();
        final PhoneHomeRequestBody phoneHomeRequest = new PhoneHomeRequestBody(regId, source.getName(), infoMap);

        phClient.postPhoneHomeRequest(phoneHomeRequest);
    }

    @Test
    public void callHomeValidUrl() throws Exception {
        final String targetUrl = "http://" + LOCALHOST + ":" + this.port + "/test";
        final URL url = new URL(targetUrl);
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(new IntBufferedLogger(), url, restConnection.timeout, restConnection.getProxyInfo(), restConnection.alwaysTrustServerCertificate);
        final String regId = "regId";
        final PhoneHomeSource source = PhoneHomeSource.INTEGRATIONS;
        final Map<String, String> infoMap = new HashMap<>();
        final PhoneHomeRequestBody phoneHomeRequest = new PhoneHomeRequestBody(regId, source.getName(), infoMap);

        phClient.postPhoneHomeRequest(phoneHomeRequest);
    }

    @Test
    public void callHomeIntegrationsTest() throws Exception {
        final String targetUrl = "http://" + LOCALHOST + ":" + this.port + "/test";
        final URL url = new URL(targetUrl);
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(new IntBufferedLogger(), url, restConnection.timeout, restConnection.getProxyInfo(), restConnection.alwaysTrustServerCertificate);

        final PhoneHomeRequestBodyBuilder phoneHomeRequestBuilder = new PhoneHomeRequestBodyBuilder();
        phoneHomeRequestBuilder.setRegistrationId("regKey");
        phoneHomeRequestBuilder.setHostName(null);
        phoneHomeRequestBuilder.setBlackDuckName(BlackDuckName.HUB);
        phoneHomeRequestBuilder.setBlackDuckVersion("blackDuckVersion");
        phoneHomeRequestBuilder.setPluginVersion("pluginVersion");
        phoneHomeRequestBuilder.setThirdPartyName(ThirdPartyName.JENKINS);
        phoneHomeRequestBuilder.setThirdPartyVersion("thirdPartyVersion");
        phoneHomeRequestBuilder.setSource(PhoneHomeSource.INTEGRATIONS);
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        phClient.postPhoneHomeRequest(phoneHomeRequest);
    }

    @Test
    public void callHomeIntegrationsTestWithHostName() throws Exception {
        final String targetUrl = "http://" + LOCALHOST + ":" + this.port + "/test";
        final URL url = new URL(targetUrl);
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(new IntBufferedLogger(), url, restConnection.timeout, restConnection.getProxyInfo(), restConnection.alwaysTrustServerCertificate);

        final PhoneHomeRequestBodyBuilder phoneHomeRequestBuilder = new PhoneHomeRequestBodyBuilder();
        phoneHomeRequestBuilder.setRegistrationId(null);
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setBlackDuckName(BlackDuckName.HUB);
        phoneHomeRequestBuilder.setBlackDuckVersion("blackDuckVersion");
        phoneHomeRequestBuilder.setPluginVersion("pluginVersion");
        phoneHomeRequestBuilder.setThirdPartyName(ThirdPartyName.JENKINS);
        phoneHomeRequestBuilder.setThirdPartyVersion("thirdPartyVersion");
        phoneHomeRequestBuilder.setSource(PhoneHomeSource.INTEGRATIONS);
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        phClient.postPhoneHomeRequest(phoneHomeRequest);
    }

    @Test
    public void testPhoneHomeException() {
        final PhoneHomeException emptyException = new PhoneHomeException();
        final PhoneHomeException exceptionException = new PhoneHomeException(emptyException);
        final PhoneHomeException messageException = new PhoneHomeException("message");
        final PhoneHomeException exceptionAndMessageException = new PhoneHomeException("message", emptyException);
        for (final PhoneHomeException phoneHomeException : new PhoneHomeException[] { emptyException, exceptionException, messageException, exceptionAndMessageException }) {
            try {
                throw phoneHomeException;
            } catch (final PhoneHomeException e) {
                // Do nothing
            }
        }
    }

    @Test
    public void testPostBadPhoneHomeRequestBuilding() throws Exception {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBuilder = new PhoneHomeRequestBodyBuilder();
        try {
            phoneHomeRequestBuilder.build();
            fail("Illegal state exception not thrown");
        } catch (final IllegalStateException e) {
            // Do nothing
        }
        try {
            phoneHomeRequestBuilder.setBlackDuckName(BlackDuckName.HUB);
            phoneHomeRequestBuilder.setThirdPartyName(ThirdPartyName.ARTIFACTORY);
            phoneHomeRequestBuilder.build();
            fail("Illegal state exception not thrown");
        } catch (final IllegalStateException e) {
            // Do nothing
        }
    }

    @Test
    public void validatePhoneHomeRequestBuilding() throws Exception {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBuilder = new PhoneHomeRequestBodyBuilder();
        phoneHomeRequestBuilder.setRegistrationId("regKey");
        phoneHomeRequestBuilder.setHostName(null);
        phoneHomeRequestBuilder.setBlackDuckName(BlackDuckName.HUB);
        phoneHomeRequestBuilder.setBlackDuckVersion("blackDuckVersion");
        phoneHomeRequestBuilder.setPluginVersion("pluginVersion");
        phoneHomeRequestBuilder.setThirdPartyName(ThirdPartyName.JENKINS);
        phoneHomeRequestBuilder.setThirdPartyVersion("thirdPartyVersion");
        phoneHomeRequestBuilder.setSource(PhoneHomeSource.INTEGRATIONS);
        phoneHomeRequestBuilder.addToMetaDataMap("some", "metadata");
        final Map<String, String> builderInfoMap = phoneHomeRequestBuilder.getMetaDataMap();
        builderInfoMap.put(PhoneHomeRequestFieldEnum.BLACKDUCKNAME.getKey(), phoneHomeRequestBuilder.getBlackDuckName());
        builderInfoMap.put(PhoneHomeRequestFieldEnum.BLACKDUCKVERSION.getKey(), phoneHomeRequestBuilder.getBlackDuckVersion());
        builderInfoMap.put(PhoneHomeRequestFieldEnum.THIRDPARTYNAME.getKey(), phoneHomeRequestBuilder.getThirdPartyName());
        builderInfoMap.put(PhoneHomeRequestFieldEnum.THIRDPARTYVERSION.getKey(), phoneHomeRequestBuilder.getThirdPartyVersion());
        builderInfoMap.put(PhoneHomeRequestFieldEnum.PLUGINVERSION.getKey(), phoneHomeRequestBuilder.getPluginVersion());

        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        assertTrue(phoneHomeRequestBuilder.getRegistrationId().equals(phoneHomeRequest.getRegId()));
        assertTrue(phoneHomeRequestBuilder.getSource().equals(phoneHomeRequest.getSource()));
        assertTrue(builderInfoMap.equals((phoneHomeRequest.getInfoMap())));

        phoneHomeRequestBuilder.setRegistrationId(null);
        phoneHomeRequestBuilder.setHostName(LOCALHOST);
        final PhoneHomeRequestBody phoneHomeRequestWithHost = phoneHomeRequestBuilder.build();
        assertTrue(phoneHomeRequestBuilder.md5Hash(phoneHomeRequestBuilder.getHostName()).equals(phoneHomeRequestWithHost.getRegId()));
    }

    @Test
    public void validateBadPhoneHomeBackend() throws Exception {
        final PhoneHomeClient phClient = new PhoneHomeClient(null, null, 0, null, false);
        try {
            phClient.postPhoneHomeRequest(null);
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }
    }

    @Test
    public void constructPhoneHomeClientWithoutUrl() throws Exception {
        final String targetUrl = "http://example.com:" + this.port + "/test";
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        new PhoneHomeClient(new IntBufferedLogger(), restConnection.hubBaseUrl, restConnection.timeout, restConnection.getProxyInfo(), restConnection.alwaysTrustServerCertificate);
        // Cannot test this meaningfully without phoning home to an actual server, which is bad.
    }

}
