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

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.log.LogLevel;
import com.blackducksoftware.integration.log.PrintStreamIntLogger;
import com.blackducksoftware.integration.phonehome.enums.BlackDuckName;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeRequestFieldEnum;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;
import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;

public class PhoneHomeClientUnitTest {
    public static final int TIMEOUT = 5;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void callHomeIntegrationsTest() throws Exception {
        final String targetUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(new IntBufferedLogger(), GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, restConnection.timeout, restConnection.getProxyInfo(),
                restConnection.alwaysTrustServerCertificate);

        final PhoneHomeRequestBodyBuilder phoneHomeRequestBuilder = new PhoneHomeRequestBodyBuilder();
        phoneHomeRequestBuilder.setRegistrationId("regKey");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setBlackDuckName(BlackDuckName.HUB);
        phoneHomeRequestBuilder.setBlackDuckVersion("blackDuckVersion");
        phoneHomeRequestBuilder.setPluginVersion("pluginVersion");
        phoneHomeRequestBuilder.setThirdPartyName(ThirdPartyName.JENKINS);
        phoneHomeRequestBuilder.setThirdPartyVersion("thirdPartyVersion");
        phoneHomeRequestBuilder.setSource(PhoneHomeSource.INTEGRATIONS);
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        phClient.postPhoneHomeRequest(phoneHomeRequest, new CIEnvironmentVariables());
    }

    @Test
    public void callHomeIntegrationsTestWithHostName() throws Exception {
        final String targetUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();
        builder.setLogger(new IntBufferedLogger());
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(new IntBufferedLogger(), GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, restConnection.timeout, restConnection.getProxyInfo(),
                restConnection.alwaysTrustServerCertificate);

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

        phClient.postPhoneHomeRequest(phoneHomeRequest, new CIEnvironmentVariables());
    }

    @Test
    public void callHomeSkip() throws Exception {
        final String targetUrl = GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT;
        final UnauthenticatedRestConnectionBuilder builder = new UnauthenticatedRestConnectionBuilder();

        final IntBufferedLogger bufferedLogger = new IntBufferedLogger();
        builder.setLogger(bufferedLogger);
        builder.setBaseUrl(targetUrl);
        builder.setTimeout(TIMEOUT);
        final RestConnection restConnection = builder.build();
        final PhoneHomeClient phClient = new PhoneHomeClient(bufferedLogger, GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, restConnection.timeout, restConnection.getProxyInfo(), restConnection.alwaysTrustServerCertificate);

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

        final CIEnvironmentVariables environmentVariables = new CIEnvironmentVariables();
        environmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "true");

        phClient.postPhoneHomeRequest(phoneHomeRequest, environmentVariables);
        assertTrue(bufferedLogger.getOutputString(LogLevel.DEBUG).contains("Skipping phone home"));

        environmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "false");

        phClient.postPhoneHomeRequest(phoneHomeRequest, environmentVariables);
        assertTrue(bufferedLogger.getOutputString(LogLevel.DEBUG).contains("Phoning home to "));
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
        phoneHomeRequestBuilder.setHostName("hostName");
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
        final PhoneHomeRequestBody phoneHomeRequestWithHost = phoneHomeRequestBuilder.build();
        assertTrue(phoneHomeRequestBuilder.md5Hash(phoneHomeRequestBuilder.getHostName()).equals(phoneHomeRequestWithHost.getRegId()));
    }

    @Test
    public void validateBadPhoneHomeBackend() throws Exception {
        final PhoneHomeClient phClient = new PhoneHomeClient(new PrintStreamIntLogger(System.out, LogLevel.DEBUG), null, 0, null, false);
        try {
            phClient.postPhoneHomeRequest(null, new CIEnvironmentVariables());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }
    }

}
