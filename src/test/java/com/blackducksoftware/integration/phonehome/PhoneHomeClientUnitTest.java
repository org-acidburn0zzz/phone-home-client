/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.phonehome;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;
import com.blackducksoftware.integration.phonehome.exception.PhoneHomeException;
import com.blackducksoftware.integration.phonehome.google.analytics.GoogleAnalyticsConstants;
import com.blackducksoftware.integration.phonehome.mock.MockLogger;

public class PhoneHomeClientUnitTest {
    public static final int TIMEOUT = 5;

    private Map<String, String> defualtEnvironmentVariables;
    private HttpHost proxyHost;
    private PhoneHomeClient defaultClient;

    @Before
    public void init() {
        final MockLogger logger = new MockLogger();
        logger.info("\n");
        logger.info("Test Class: PhoneHomeClientUnitTest");
        defualtEnvironmentVariables = new HashMap<>();
        defualtEnvironmentVariables.put(PhoneHomeClient.PHONE_HOME_URL_OVERRIDE_VARIABLE, GoogleAnalyticsConstants.BASE_URL + GoogleAnalyticsConstants.DEBUG_ENDPOINT);
        proxyHost = null;
        defaultClient = new PhoneHomeClient(logger, GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, TIMEOUT, proxyHost);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void callHomeIntegrationsTest() throws Exception {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        defaultClient.postPhoneHomeRequest(phoneHomeRequest, defualtEnvironmentVariables);
    }

    @Test
    public void callHomeIntegrationsTestWithHostName() throws Exception {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        defaultClient.postPhoneHomeRequest(phoneHomeRequest, defualtEnvironmentVariables);
    }

    @Test
    public void callHomeSkip() throws Exception {
        final MockLogger logger = new MockLogger();
        final PhoneHomeClient clientWithTrackableLogger = new PhoneHomeClient(logger, GoogleAnalyticsConstants.TEST_INTEGRATIONS_TRACKING_ID, TIMEOUT, proxyHost);

        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        defualtEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "true");

        clientWithTrackableLogger.postPhoneHomeRequest(phoneHomeRequest, defualtEnvironmentVariables);
        assertTrue(logger.doLogsContainText("Skipping phone home"));

        defualtEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, "false");

        clientWithTrackableLogger.postPhoneHomeRequest(phoneHomeRequest, defualtEnvironmentVariables);
        assertTrue(logger.doLogsContainText("Phoning home to "));
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
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        try {
            phoneHomeRequestBuilder.build();
            fail("Illegal state exception not thrown");
        } catch (final IllegalStateException e) {
            // Do nothing
        }
        try {
            phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
            phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
            phoneHomeRequestBuilder.build();
            fail("Illegal state exception not thrown");
        } catch (final IllegalStateException e) {
            // Do nothing
        }
    }

    @Test
    public void validatePhoneHomeRequestBuilding() throws Exception {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBuilder.setCustomerId("customerId");
        phoneHomeRequestBuilder.setHostName("hostName");
        phoneHomeRequestBuilder.setArtifactId("artifactId");
        phoneHomeRequestBuilder.setArtifactVersion("artifactVersion");
        phoneHomeRequestBuilder.setProductId(ProductIdEnum.CODE_CENTER);
        phoneHomeRequestBuilder.setProductVersion("productVersion");
        final Map<String, String> builderMetaData = phoneHomeRequestBuilder.getMetaData();
        builderMetaData.put("example_meta_data", "data");

        final PhoneHomeRequestBody phoneHomeRequest = phoneHomeRequestBuilder.build();

        assertTrue(phoneHomeRequestBuilder.getCustomerId().equals(phoneHomeRequest.getCustomerId()));
        assertTrue(phoneHomeRequestBuilder.getArtifactId().equals(phoneHomeRequest.getArtifactId()));
        assertTrue(phoneHomeRequestBuilder.getArtifactVersion().equals(phoneHomeRequest.getArtifactVersion()));
        assertTrue(phoneHomeRequestBuilder.getProductId().equals(phoneHomeRequest.getProductId()));
        assertTrue(phoneHomeRequestBuilder.getProductVersion().equals(phoneHomeRequest.getProductVersion()));
        assertTrue(builderMetaData.equals((phoneHomeRequest.getMetaData())));
    }

    @Test
    public void validateBadPhoneHomeBackend() throws Exception {
        final PhoneHomeClient phClient = new PhoneHomeClient(null, 0, null);
        try {
            phClient.postPhoneHomeRequest(null, Collections.emptyMap());
            fail("Phone home exception not thrown");
        } catch (final PhoneHomeException e) {
            // Do nothing
        }
    }

}
